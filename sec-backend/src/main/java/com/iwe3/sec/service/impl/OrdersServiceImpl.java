package com.iwe3.sec.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.*;
import com.iwe3.sec.mapper.*;
import com.iwe3.sec.mq.OrderMessageProducer;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.common.*;
import com.iwe3.sec.common.lock.DistributedLockTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * orders 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店订单，总店长可看全部
 *
 * 状态机说明：
 *   订单状态流转必须经过 validateStateTransition 校验，不合法流转会抛出异常
 *   每次状态变更都带 version 乐观锁，防止并发覆盖
 *   每次状态变更都记录 order_status_log
 */
@Slf4j
@Service
public class OrdersServiceImpl implements IOrdersService {

    // 订单状态常量（与 entity 中一致）
    private static final int STATUS_PENDING_PAY = 1;     // 待支付
    private static final int STATUS_PENDING_COOK = 2;    // 待制作
    private static final int STATUS_COOKING = 3;         // 制作中
    private static final int STATUS_PENDING_DELIVERY = 4; // 待配送
    private static final int STATUS_DELIVERING = 5;      // 配送中
    private static final int STATUS_PENDING_PICKUP = 6;  // 待自取
    private static final int STATUS_COMPLETED = 7;       // 已完成
    private static final int STATUS_CANCELLED = 8;       // 已取消
    private static final int STATUS_REFUNDED = 9;        // 已退款

    // 支付状态常量
    private static final int PAY_PENDING = 1;   // 待支付
    private static final int PAY_PAID = 2;      // 已支付
    private static final int PAY_REFUNDED = 3;  // 已退款
    private static final int PAY_PARTIAL_REFUND = 4; // 部分退款

    /**
     * 订单状态流转规则表
     * key = 当前状态, value = 允许流转到的状态集合
     */
    private static final Map<Integer, Set<Integer>> STATE_TRANSITION_MAP = new HashMap<>();
    static {
        // 待支付 → 已取消
        STATE_TRANSITION_MAP.put(STATUS_PENDING_PAY, new HashSet<>(Arrays.asList(
                STATUS_PENDING_COOK, STATUS_PENDING_DELIVERY, STATUS_PENDING_PICKUP, STATUS_CANCELLED
        )));
        // 待制作 → 制作中
        STATE_TRANSITION_MAP.put(STATUS_PENDING_COOK, new HashSet<>(Arrays.asList(
                STATUS_COOKING, STATUS_REFUNDED
        )));
        // 制作中 → 已完成
        STATE_TRANSITION_MAP.put(STATUS_COOKING, new HashSet<>(Arrays.asList(
                STATUS_COMPLETED, STATUS_REFUNDED
        )));
        // 待配送 → 配送中 / 已完成
        STATE_TRANSITION_MAP.put(STATUS_PENDING_DELIVERY, new HashSet<>(Arrays.asList(
                STATUS_DELIVERING, STATUS_COMPLETED, STATUS_REFUNDED
        )));
        // 配送中 → 已完成
        STATE_TRANSITION_MAP.put(STATUS_DELIVERING, new HashSet<>(Arrays.asList(
                STATUS_COMPLETED, STATUS_REFUNDED
        )));
        // 待自取 → 已完成
        STATE_TRANSITION_MAP.put(STATUS_PENDING_PICKUP, new HashSet<>(Arrays.asList(
                STATUS_COMPLETED, STATUS_REFUNDED
        )));
        // 已完成、已取消、已退款 → 终点状态，不可再流转
        STATE_TRANSITION_MAP.put(STATUS_COMPLETED, Collections.emptySet());
        STATE_TRANSITION_MAP.put(STATUS_CANCELLED, Collections.emptySet());
        STATE_TRANSITION_MAP.put(STATUS_REFUNDED, Collections.emptySet());
    }

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final DishStockMapper dishStockMapper;
    private final PermissionChecker permissionChecker;
    @Autowired(required = false)
    private OrderMessageProducer orderMessageProducer;
    private final DistributedLockTemplate distributedLockTemplate;

    /** 订单超时自动取消的时间（分钟），从配置文件读取 */
    @Value("${order.timeout-minutes:30}")
    private int timeoutMinutes;

    public OrdersServiceImpl(OrdersMapper ordersMapper,
                             OrderDetailMapper orderDetailMapper,
                             OrderStatusLogMapper orderStatusLogMapper,
                             PaymentRecordMapper paymentRecordMapper,
                             DishStockMapper dishStockMapper,
                             PermissionChecker permissionChecker,
                             DistributedLockTemplate distributedLockTemplate) {
        this.ordersMapper = ordersMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.dishStockMapper = dishStockMapper;
        this.permissionChecker = permissionChecker;
        this.distributedLockTemplate = distributedLockTemplate;
    }

    @Override
    public PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size) {
        // 店长及以下：强制只看本门店
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无门店归属，无法查看订单");
            }
            query.setStoreId(myStoreId);
        }
        PageHelper.startPage(page, size);
        List<OrdersEntity> list = ordersMapper.selectList(query);
        PageInfo<OrdersEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public OrdersEntity getById(Long id) {
        OrdersEntity o = ordersMapper.selectById(id);
        if (o == null) {
            return null;
        }
        permissionChecker.assertInOwnStore(o.getStoreId());
        return o;
    }

    @Override
    public boolean add(OrdersEntity entity) {
        permissionChecker.setStoreIdIfNeeded(entity::setStoreId);
        return ordersMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrdersEntity entity) {
        if (entity.getId() != null) {
            OrdersEntity existing = ordersMapper.selectById(entity.getId());
            if (existing != null) {
                permissionChecker.assertInOwnStore(existing.getStoreId());
            }
        }
        return ordersMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        OrdersEntity existing = ordersMapper.selectById(id);
        if (existing != null) {
            permissionChecker.assertInOwnStore(existing.getStoreId());
        }
        return ordersMapper.deleteById(id) > 0;
    }

    // ========== 状态机核心方法 ==========

    /**
     * 校验状态流转是否合法
     * @param fromStatus 当前状态
     * @param toStatus 目标状态
     * @throws BusinessException 如果不合法
     */
    private void validateStateTransition(Integer fromStatus, Integer toStatus) {
        Set<Integer> allowedTargets = STATE_TRANSITION_MAP.get(fromStatus);
        if (allowedTargets == null) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "订单状态 [" + fromStatus + "] 不存在");
        }
        if (!allowedTargets.contains(toStatus)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "订单状态不允许从 " + fromStatus + " 变更为 " + toStatus + "，请刷新后重试");
        }
    }

    /**
     * 执行订单状态流转（带乐观锁 + 状态机校验）
     * @param order 当前订单（含原状态和版本号）
     * @param toStatus 目标状态
     * @param newPayStatus 新的支付状态（不更新传 null）
     * @param payTime 支付时间（不更新传 null）
     * @param operatorType 操作方
     * @param operatorId 操作者ID
     * @param remark 变更说明
     */
    private void doTransition(OrdersEntity order, Integer toStatus, Integer newPayStatus,
                               Date payTime, String operatorType, Long operatorId, String remark) {
        // 状态机校验
        validateStateTransition(order.getOrderStatus(), toStatus);

        // 执行带版本号的更新
        Date now = new Date();
        Date finishTime = (toStatus != null && toStatus == STATUS_COMPLETED) ? now : null;
        Date cancelTime = (toStatus != null && toStatus == STATUS_CANCELLED) ? now : null;

        int affected = ordersMapper.transitionStatus(
                order.getId(), order.getOrderStatus(), toStatus,
                newPayStatus, payTime, finishTime, cancelTime,
                remark, order.getVersion()
        );
        if (affected == 0) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_CHANGED,
                    "订单状态已变更，请刷新后重试");
        }

        // 记录状态变更日志
        saveStatusLog(order.getId(), order.getOrderStatus(), toStatus,
                operatorType, operatorId, remark);

        log.info("订单状态流转: orderId={}, from={}, to={}, operatorId={}",
                order.getId(), order.getOrderStatus(), toStatus, operatorId);
    }

    // ========== 业务接口实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrdersEntity entity, List<OrderDetailEntity> details, Long operatorId) {
        // 1. 校验门店权限
        Long storeId = permissionChecker.getValidStoreId();
        entity.setStoreId(storeId);
        entity.setOperatorId(operatorId);

        // 2. 生成订单号
        entity.setOrderNo(generateOrderNo(storeId));
        entity.setOrderStatus(STATUS_PENDING_PAY);
        entity.setPayStatus(PAY_PENDING);
        entity.setOrderTime(new Date());

        // 3. 计算金额
        BigDecimal dishAmount = BigDecimal.ZERO;
        for (OrderDetailEntity detail : details) {
            BigDecimal subtotal = detail.getDishPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            detail.setSubtotal(subtotal);
            dishAmount = dishAmount.add(subtotal);
            detail.setStatus(1);
        }
        entity.setDishAmount(dishAmount);

        BigDecimal payable = dishAmount
                .subtract(opt(entity.getDiscountAmount()))
                .subtract(opt(entity.getFreeAmount()))
                .subtract(opt(entity.getRoundingAmount()))
                .subtract(opt(entity.getCouponAmount()))
                .add(opt(entity.getDeliveryFee()));
        entity.setPayableAmount(payable.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : payable);

        // 4. 保存订单
        ordersMapper.insert(entity);
        Long orderId = entity.getId();

        // 5. 保存订单明细
        for (OrderDetailEntity detail : details) {
            detail.setOrderId(orderId);
        }
        orderDetailMapper.insertBatch(details);

        // 6. 记录状态日志
        saveStatusLog(orderId, null, STATUS_PENDING_PAY, "STAFF", operatorId, "下单成功");

        // 7. 在分布式锁内进行原子扣减库存（按 storeId 粒度锁，防止同一门店并发超卖）
        String stockLockKey = "sr:lock:stock:store:" + storeId;
        try {
            distributedLockTemplate.tryLock(stockLockKey, () -> {
                // 按 dish_id 升序扣减，避免死锁
                details.sort(Comparator.comparing(OrderDetailEntity::getDishId));
                for (OrderDetailEntity detail : details) {
                    int affected = dishStockMapper.decreaseStock(storeId, detail.getDishId(), detail.getQuantity());
                    if (affected == 0) {
                        throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH,
                                "库存不足：菜品ID=" + detail.getDishId() + "，请调整数量后重试");
                    }
                }
                return null;
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH, "库存扣减异常，请稍后重试");
        }

        log.info("订单提交成功：orderId={}, orderNo={}", orderId, entity.getOrderNo());

        // RabbitMQ 可用时发送异步消息，不可用则由定时任务兜底
        if (orderMessageProducer != null) {
            orderMessageProducer.sendOrderCreated(entity);

            // 发送打印任务：后厨打印制作单
            orderMessageProducer.sendPrintJob(entity, "KITCHEN");

            // 发送超时取消延迟消息：按配置的时间等待后自动取消未支付订单
            long delayMs = timeoutMinutes * 60 * 1000L;
            orderMessageProducer.sendDelayCancel(entity, delayMs);
            log.info("已发送超时取消延迟消息：orderId={}, delayMs={}", orderId, delayMs);
        } else {
            log.warn("RabbitMQ 未连接，跳过消息发送，订单超时将依赖定时任务处理：orderNo={}", entity.getOrderNo());
        }

        return orderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, String payType, BigDecimal actualAmount,
                         BigDecimal memberPayAmount, String idempotencyKey, Long operatorId) {
        // 分布式锁：按订单粒度锁，防止并发支付（同时多点点击支付按钮）
        String payLockKey = "sr:lock:pay:order:" + orderId;
        try {
            distributedLockTemplate.tryLock(payLockKey, () -> {
                _doPayOrder(orderId, payType, actualAmount, memberPayAmount, idempotencyKey, operatorId);
                return null;
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付处理异常，请稍后重试");
        }
    }

    /**
     * 支付核心逻辑（由 payOrder 的分布式锁保护）
     */
    private void _doPayOrder(Long orderId, String payType, BigDecimal actualAmount,
                              BigDecimal memberPayAmount, String idempotencyKey, Long operatorId) {
        // 幂等校验：如果该幂等键已存在，直接返回成功（不重复处理）
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            PaymentRecordEntity existing = paymentRecordMapper.selectByIdempotencyKey(idempotencyKey);
            if (existing != null) {
                log.info("幂等键重复，直接返回成功：idempotencyKey={}, payNo={}", idempotencyKey, existing.getPayNo());
                return;
            }
        }

        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        if (order.getPayStatus() != PAY_PENDING) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID, "订单已支付，请勿重复操作");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        // 根据订单类型决定支付后的订单状态
        Integer nextOrderStatus;
        if (order.getOrderType() == 1) {
            nextOrderStatus = STATUS_PENDING_COOK;   // 堂食→待制作
        } else if (order.getOrderType() == 2) {
            nextOrderStatus = STATUS_PENDING_DELIVERY; // 外卖→待配送
        } else {
            nextOrderStatus = STATUS_PENDING_PICKUP;   // 自取→待自取
        }

        // 执行状态流转（带乐观锁 + 状态机校验）
        doTransition(order, nextOrderStatus, PAY_PAID, new Date(),
                "STAFF", operatorId, "订单支付成功，支付方式：" + payType);

        // 生成支付记录（含幂等键）
        PaymentRecordEntity payRecord = PaymentRecordEntity.builder()
                .payNo(generatePayNo(order.getStoreId()))
                .orderId(orderId)
                .storeId(order.getStoreId())
                .memberId(order.getMemberId())
                .payType(payType)
                .payChannel(getPayChannel(payType))
                .amount(actualAmount)
                .idempotencyKey(idempotencyKey)
                .status(2) // 支付成功
                .payTime(new Date())
                .build();
        paymentRecordMapper.insert(payRecord);

        log.info("订单支付成功：orderId={}, payType={}, amount={}, idempotencyKey={}",
                orderId, payType, actualAmount, idempotencyKey);

        // 发送异步消息：通知后厨开始制作（RabbitMQ 不可用时跳过）
        if (orderMessageProducer != null) {
            orderMessageProducer.sendOrderPaid(order);
            orderMessageProducer.sendKitchenNotification(order);
            // 发送打印任务：前台打印消费小票
            orderMessageProducer.sendPrintJob(order, "RECEIPT");
        } else {
            log.warn("RabbitMQ 未连接，跳过支付消息发送：orderId={}", orderId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String reason, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        // 执行状态流转（带状态机校验：只有待支付才能取消）
        doTransition(order, STATUS_CANCELLED, null, null,
                "STAFF", operatorId, "订单取消：" + (reason != null ? reason : "无原因"));

        // 如果已支付（退款场景），后续走退款流程
        if (order.getPayStatus() == PAY_PAID) {
            log.warn("已支付订单被取消，需要走退款流程：orderId={}", orderId);
        }

        log.info("订单已取消：orderId={}, reason={}", orderId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        // 先更新所有明细为已完成
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(orderId);
        for (OrderDetailEntity detail : details) {
            orderDetailMapper.updateMakeStatus(detail.getId(), 3); // 3=已完成
        }

        // 执行状态流转（制作中 / 配送中 / 待自取 → 已完成）
        doTransition(order, STATUS_COMPLETED, null, null,
                "STAFF", operatorId, "订单已完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMakeStatus(Long orderId, Long detailId, Integer makeStatus, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        if (detailId != null) {
            // 更新单个明细的制作状态
            orderDetailMapper.updateMakeStatus(detailId, makeStatus);
            log.info("更新菜品制作状态：detailId={}, makeStatus={}", detailId, makeStatus);
        } else {
            // 更新整单所有明细
            List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(orderId);
            for (OrderDetailEntity detail : details) {
                orderDetailMapper.updateMakeStatus(detail.getId(), makeStatus);
            }

            if (makeStatus == 3) {
                // 全部已上齐 → 订单完成
                doTransition(order, STATUS_COMPLETED, null, null,
                        "STAFF", operatorId, "菜品已上齐，订单完成");
            } else if (makeStatus == 2) {
                // 开始制作 → 状态改为"制作中"
                doTransition(order, STATUS_COOKING, null, null,
                        "STAFF", operatorId, "开始制作");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Long orderId, String reason, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        // 只有已支付的订单才能退款
        if (order.getPayStatus() != PAY_PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "未支付的订单不能退款");
        }

        // 执行状态流转
        doTransition(order, STATUS_REFUNDED, PAY_REFUNDED, null,
                "STAFF", operatorId, "订单退款：" + (reason != null ? reason : "无原因"));

        log.info("订单已退款：orderId={}, reason={}", orderId, reason);
    }

    @Override
    public OrdersEntity getOrderWithDetails(Long orderId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        permissionChecker.assertInOwnStore(order.getStoreId());
        return order;
    }

    @Override
    public List<OrderStatusLogEntity> getOrderStatusLogs(Long orderId) {
        return orderStatusLogMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrderDetailEntity> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailMapper.selectByOrderId(orderId);
    }

    @Override
    public List<OrdersEntity> getKitchenOrders(Long storeId) {
        if (storeId == null) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无门店归属");
            }
            storeId = myStoreId;
        }
        return ordersMapper.selectKitchenOrders(storeId);
    }

    @Override
    public Map<String, Object> getSalesReport(Long storeId, String beginDate, String endDate) {
        if (storeId == null) {
            storeId = permissionChecker.getValidStoreId();
        }
        List<Map<String, Object>> dailyData = ordersMapper.selectSalesReport(storeId, beginDate, endDate);

        // 汇总统计
        Map<String, Object> result = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        int totalOrders = 0;

        for (Map<String, Object> day : dailyData) {
            totalIncome = totalIncome.add(toBigDecimal(day.get("actualIncome")));
            totalOrders += toInt(day.get("orderCount"));
        }

        result.put("dailyData", dailyData);
        result.put("totalIncome", totalIncome);
        result.put("totalOrders", totalOrders);
        result.put("beginDate", beginDate);
        result.put("endDate", endDate);
        return result;
    }

    @Override
    public Map<String, Object> getTodaySummary(Long storeId) {
        if (storeId == null) {
            storeId = permissionChecker.getValidStoreId();
        }
        Map<String, Object> summary = ordersMapper.selectTodaySummary(storeId);
        if (summary == null) {
            summary = new HashMap<>();
            summary.put("todayOrderCount", 0);
            summary.put("todayIncome", BigDecimal.ZERO);
        }
        // 查询待处理订单数（使用专用计数查询，避免全量查一条记录再取总数）
        int pendingCount = ordersMapper.countByStoreIdAndStatus(storeId, 1);
        summary.put("pendingOrderCount", pendingCount);
        return summary;
    }

    // ========== 私有方法 ==========

    private void saveStatusLog(Long orderId, Integer fromStatus, Integer toStatus,
                                String operatorType, Long operatorId, String remark) {
        OrderStatusLogEntity logEntry = OrderStatusLogEntity.builder()
                .orderId(orderId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .operatorType(operatorType)
                .operatorId(operatorId)
                .remark(remark)
                .createTime(new Date())
                .build();
        orderStatusLogMapper.insert(logEntry);
    }

    private String generateOrderNo(Long storeId) {
        // 订单号生成规则：SO + 日期(8位) + 时间(6位) + 门店(3位) + 随机数(5位)
        // 使用 ThreadLocalRandom 替代 Math.random()，避免并发碰撞
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String storeStr = String.format("%03d", storeId % 1000);
        int random = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "SO" + dateStr + storeStr + random;
    }

    private String generatePayNo(Long storeId) {
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String storeStr = String.format("%03d", storeId % 1000);
        int random = ThreadLocalRandom.current().nextInt(10000, 99999);
        return "PAY" + dateStr + storeStr + random;
    }

    private String getPayChannel(String payType) {
        Map<String, String> map = new HashMap<>();
        map.put("WECHAT", "微信支付");
        map.put("ALIPAY", "支付宝");
        map.put("MEMBER_BALANCE", "会员余额");
        map.put("CASH", "现金支付");
        return map.getOrDefault(payType, payType);
    }

    private BigDecimal opt(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        return new BigDecimal(val.toString());
    }
}
