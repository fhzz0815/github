package com.iwe3.sec.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.*;
import com.iwe3.sec.mapper.*;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * orders 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店订单，总店长可看全部
 */
@Slf4j
@Service
public class OrdersServiceImpl implements IOrdersService {

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final DishStockMapper dishStockMapper;
    private final PermissionChecker permissionChecker;

    public OrdersServiceImpl(OrdersMapper ordersMapper,
                             OrderDetailMapper orderDetailMapper,
                             OrderStatusLogMapper orderStatusLogMapper,
                             PaymentRecordMapper paymentRecordMapper,
                             DishStockMapper dishStockMapper,
                             PermissionChecker permissionChecker) {
        this.ordersMapper = ordersMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.orderStatusLogMapper = orderStatusLogMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.dishStockMapper = dishStockMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size) {
        // 店长及以下：强制只看本门店
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(403, "无门店归属，无法查看订单");
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
        assertInOwnStore(o.getStoreId());
        return o;
    }

    @Override
    public boolean add(OrdersEntity entity) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            entity.setStoreId(permissionChecker.currentStoreId());
        }
        return ordersMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrdersEntity entity) {
        if (entity.getId() != null) {
            OrdersEntity existing = ordersMapper.selectById(entity.getId());
            if (existing != null) {
                assertInOwnStore(existing.getStoreId());
            }
        }
        return ordersMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        OrdersEntity existing = ordersMapper.selectById(id);
        if (existing != null) {
            assertInOwnStore(existing.getStoreId());
        }
        return ordersMapper.deleteById(id) > 0;
    }

    // ========== 业务接口实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrdersEntity entity, List<OrderDetailEntity> details, Long operatorId) {
        // 1. 校验门店权限
        Long storeId = getValidStoreId();
        entity.setStoreId(storeId);
        entity.setOperatorId(operatorId);

        // 2. 生成订单号
        entity.setOrderNo(generateOrderNo(storeId));
        entity.setOrderStatus(1); // 待支付
        entity.setPayStatus(1);   // 待支付
        entity.setOrderTime(new Date());

        // 3. 计算金额
        BigDecimal dishAmount = BigDecimal.ZERO;
        for (OrderDetailEntity detail : details) {
            BigDecimal subtotal = detail.getDishPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            detail.setSubtotal(subtotal);
            dishAmount = dishAmount.add(subtotal);
            detail.setStatus(1); // 待制作
        }
        entity.setDishAmount(dishAmount);

        // 计算应付金额 = 菜品原价 - 优惠 - 免单 - 抹零 - 优惠券 + 配送费
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
        saveStatusLog(orderId, null, 1, "STAFF", operatorId, "下单成功");

        // 7. 扣减库存（仅扣减有库存管理的菜品）
        for (OrderDetailEntity detail : details) {
            int affected = dishStockMapper.decreaseStock(storeId, detail.getDishId(), detail.getQuantity());
            if (affected == 0) {
                // 库存不足时记录日志但不阻断（菜品可能没有库存管理）
                log.warn("菜品库存扣减失败，可能库存不足或无库存记录：dishId={}, storeId={}", detail.getDishId(), storeId);
            }
        }

        log.info("订单提交成功：orderId={}, orderNo={}", orderId, entity.getOrderNo());
        return orderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, String payType, BigDecimal actualAmount,
                         BigDecimal memberPayAmount, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getPayStatus() != 1) {
            throw new BusinessException(400, "订单已支付，请勿重复操作");
        }
        assertInOwnStore(order.getStoreId());

        // 更新订单支付状态
        Integer nextOrderStatus;
        if (order.getOrderType() == 1) {
            nextOrderStatus = 2; // 堂食→待制作
        } else if (order.getOrderType() == 2) {
            nextOrderStatus = 4; // 外卖→待配送
        } else {
            nextOrderStatus = 6; // 自取→待自取
        }

        int affected = ordersMapper.updatePayStatus(orderId, 2, nextOrderStatus, new Date(), order.getVersion());
        if (affected == 0) {
            throw new BusinessException(500, "支付失败，订单状态已变更，请刷新后重试");
        }

        // 生成支付记录
        PaymentRecordEntity payRecord = PaymentRecordEntity.builder()
                .payNo(generatePayNo(order.getStoreId()))
                .orderId(orderId)
                .storeId(order.getStoreId())
                .memberId(order.getMemberId())
                .payType(payType)
                .payChannel(getPayChannel(payType))
                .amount(actualAmount)
                .status(2) // 成功
                .payTime(new Date())
                .build();
        paymentRecordMapper.insert(payRecord);

        // 记录状态日志
        saveStatusLog(orderId, order.getOrderStatus(), nextOrderStatus, "STAFF", operatorId,
                "订单支付成功，支付方式：" + payType);

        log.info("订单支付成功：orderId={}, payType={}, amount={}", orderId, payType, actualAmount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String reason, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getOrderStatus() == 7 || order.getOrderStatus() == 8 || order.getOrderStatus() == 9) {
            throw new BusinessException(400, "订单已完成或已取消，无法重复操作");
        }
        assertInOwnStore(order.getStoreId());

        // 更新订单状态为已取消
        int affected = ordersMapper.updateOrderStatus(orderId, 8, order.getVersion());
        if (affected == 0) {
            throw new BusinessException(500, "取消失败，订单状态已变更，请刷新后重试");
        }

        // 记录状态日志
        saveStatusLog(orderId, order.getOrderStatus(), 8, "STAFF", operatorId,
                "订单取消：" + (reason != null ? reason : "无原因"));

        log.info("订单已取消：orderId={}, reason={}", orderId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMakeStatus(Long orderId, Long detailId, Integer makeStatus, Long operatorId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        assertInOwnStore(order.getStoreId());

        if (detailId != null) {
            // 更新单个明细的制作状态
            orderDetailMapper.updateMakeStatus(detailId, makeStatus);
        } else {
            // 更新整单所有明细
            List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(orderId);
            for (OrderDetailEntity detail : details) {
                orderDetailMapper.updateMakeStatus(detail.getId(), makeStatus);
            }
            // 更新订单主状态
            if (makeStatus == 3) {
                // 全部已上齐 → 订单完成
                ordersMapper.updateOrderStatus(orderId, 7, order.getVersion());
                saveStatusLog(orderId, order.getOrderStatus(), 7, "STAFF", operatorId, "菜品已上齐，订单完成");
            } else {
                ordersMapper.updateOrderStatus(orderId,
                        makeStatus == 2 ? 3 : order.getOrderStatus(), order.getVersion());
                saveStatusLog(orderId, order.getOrderStatus(), 3, "STAFF", operatorId,
                        makeStatus == 2 ? "开始制作" : "更新制作状态");
            }
        }
    }

    @Override
    public OrdersEntity getOrderWithDetails(Long orderId) {
        OrdersEntity order = ordersMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        assertInOwnStore(order.getStoreId());
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(orderId);
        // 通过扩展字段传递明细（借用addressSnapshot暂存，实际可创建VO）
        // 这里简单处理，由调用方再查明细
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
                throw new BusinessException(403, "无门店归属");
            }
            storeId = myStoreId;
        }
        return ordersMapper.selectKitchenOrders(storeId);
    }

    @Override
    public Map<String, Object> getSalesReport(Long storeId, String beginDate, String endDate) {
        if (storeId == null) {
            storeId = getValidStoreId();
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
            storeId = getValidStoreId();
        }
        Map<String, Object> summary = ordersMapper.selectTodaySummary(storeId);
        if (summary == null) {
            summary = new HashMap<>();
            summary.put("todayOrderCount", 0);
            summary.put("todayIncome", BigDecimal.ZERO);
        }
        // 查询待处理订单数
        OrdersEntity query = new OrdersEntity();
        query.setStoreId(storeId);
        query.setOrderStatus(1); // 待支付
        PageHelper.startPage(1, 1);
        List<OrdersEntity> pendingList = ordersMapper.selectList(query);
        summary.put("pendingOrderCount", new PageInfo<>(pendingList).getTotal());
        return summary;
    }

    // ========== 私有方法 ==========

    private void saveStatusLog(Long orderId, Integer fromStatus, Integer toStatus,
                                String operatorType, Long operatorId, String remark) {
        OrderStatusLogEntity log = OrderStatusLogEntity.builder()
                .orderId(orderId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .operatorType(operatorType)
                .operatorId(operatorId)
                .remark(remark)
                .createTime(new Date())
                .build();
        orderStatusLogMapper.insert(log);
    }

    private String generateOrderNo(Long storeId) {
        // 订单号：SO + 日期(8位) + 门店(3位) + 序号(4位)
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        String storeStr = String.format("%03d", storeId % 1000);
        String seq = String.format("%04d", (int) (Math.random() * 10000));
        return "SO" + dateStr + storeStr + seq;
    }

    private String generatePayNo(Long storeId) {
        String dateStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String storeStr = String.format("%03d", storeId % 1000);
        String seq = String.format("%04d", (int) (Math.random() * 10000));
        return "PAY" + dateStr + storeStr + seq;
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

    private Long getValidStoreId() {
        Integer level = permissionChecker.currentRoleLevel();
        if (level != null && level >= PermissionChecker.LEVEL_GENERAL_MANAGER) {
            return null; // 总店长不限制
        }
        Long myStoreId = permissionChecker.currentStoreId();
        if (myStoreId == null) {
            throw new BusinessException(403, "无门店归属");
        }
        return myStoreId;
    }

    /** 校验目标数据是否属于当前用户的门店（总店长跳过） */
    private void assertInOwnStore(Long targetStoreId) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level != null && level >= PermissionChecker.LEVEL_GENERAL_MANAGER) {
            return;
        }
        Long myStoreId = permissionChecker.currentStoreId();
        if (myStoreId == null || !myStoreId.equals(targetStoreId)) {
            throw new BusinessException(403, "无权限，只能操作本门店数据");
        }
    }
}
