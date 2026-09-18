package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.DishStockMapper;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.mapper.MemberCouponMapper;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.mq.OrderMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 订单超时处理服务
 * <p>
 * 通过定时任务扫描"待支付"状态且超过支付时限的订单，自动取消并释放库存。
 * 支持 RabbitMQ 延迟队列（备用方案），但主要依赖定时任务兜底。
 * <p>
 * 支付超时时间默认 30 分钟，可通过配置文件 order.timeout-minutes 调整。
 * <p>
 * 每一个超时订单单独开启事务，避免某一个订单处理失败导致整批回滚。
 * 分页扫描（每批 50 条），避免一次加载过多数据。
 */
@Service
public class OrderTimeoutService {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutService.class);

    /** 订单超时时间（分钟），默认 30 分钟 */
    @Value("${order.timeout-minutes:30}")
    private int timeoutMinutes;

    /** 每批处理数量上限 */
    private static final int BATCH_SIZE = 50;

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final DishStockMapper dishStockMapper;
    private final MemberCouponMapper memberCouponMapper;
    private final TransactionTemplate transactionTemplate;

    @Autowired(required = false)
    private OrderMessageProducer orderMessageProducer;

    public OrderTimeoutService(OrdersMapper ordersMapper,
                               OrderDetailMapper orderDetailMapper,
                               DishStockMapper dishStockMapper,
                               MemberCouponMapper memberCouponMapper,
                               TransactionTemplate transactionTemplate) {
        this.ordersMapper = ordersMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.dishStockMapper = dishStockMapper;
        this.memberCouponMapper = memberCouponMapper;
        this.transactionTemplate = transactionTemplate;
    }

    /**
     * 每 1 分钟执行一次，分页扫描超时未支付的订单并逐笔自动取消
     * 每个订单在自己的事务里独立处理，一个失败不影响其他订单
     */
    @Scheduled(fixedRate = 60_000) // 每分钟执行一次
    public void autoCancelTimeoutOrders() {
        // 计算超时时间点
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(timeoutMinutes);
        Date deadlineDate = Date.from(deadline.atZone(ZoneId.systemDefault()).toInstant());

        int totalProcessed = 0;
        int totalCancelled = 0;

        // 分页处理：每次查一页，直到没有更多数据
        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            // 分页查询超时未支付订单
            int offset = (page - 1) * BATCH_SIZE;
            List<OrdersEntity> timeoutOrders = ordersMapper.selectTimeoutOrdersPage(deadlineDate, offset, BATCH_SIZE);

            if (timeoutOrders == null || timeoutOrders.isEmpty()) {
                hasMore = false;
                break;
            }

            for (OrdersEntity order : timeoutOrders) {
                try {
                    // 每个订单独立事务：一个失败不影响其他订单
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            doCancelOrder(order);
                        }
                    });
                    totalCancelled++;
                } catch (Exception e) {
                    log.error("自动取消订单失败（跳过继续处理下一个）：orderId={}", order.getId(), e);
                }
                totalProcessed++;
            }

            // 如果这一批不满 BATCH_SIZE，说明没有更多数据了
            if (timeoutOrders.size() < BATCH_SIZE) {
                hasMore = false;
            } else {
                page++;
            }
        }

        if (totalProcessed > 0) {
            log.info("本次超时取消完成：共处理 {} 个，成功取消 {} 个", totalProcessed, totalCancelled);
        }
    }

    /**
     * 执行单个订单的取消操作（在独立事务中运行）
     */
    private void doCancelOrder(OrdersEntity order) {
        // 使用乐观锁尝试取消（只有状态仍为"待支付"且 version 匹配才会成功）
        int affected = ordersMapper.transitionStatus(
                order.getId(),
                1,              // fromStatus = 待支付
                8,              // toStatus = 已取消
                null,           // newPayStatus 不变
                null,           // payTime 不变
                null,           // finishTime 不变
                new Date(),     // cancelTime = 现在
                "支付超时，系统自动取消", // cancelReason
                order.getVersion()
        );

        if (affected == 0) {
            log.warn("自动取消订单失败（可能已被其它实例处理）：orderId={}", order.getId());
            return; // 乐观锁冲突，跳过
        }

        log.info("自动取消订单成功：orderId={}, orderNo={}", order.getId(), order.getOrderNo());

        // 回滚库存：查询订单菜品明细，逐项恢复库存
        List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(order.getId());
        for (OrderDetailEntity detail : details) {
            Long storeId = order.getStoreId();
            Long dishId = detail.getDishId();
            int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
            if (quantity > 0) {
                dishStockMapper.increaseStock(storeId, dishId, quantity);
            }
        }

        // 释放优惠券：把该订单关联的优惠券全部回退为"未使用"
        memberCouponMapper.releaseCouponByOrder(order.getId());
        log.info("优惠券已释放：orderId={}", order.getId());

        // 发送取消通知（RabbitMQ 不可用时跳过）
        if (orderMessageProducer != null) {
            orderMessageProducer.sendOrderCancelled(order);
        } else {
            log.warn("RabbitMQ 未连接，跳过取消通知消息：orderId={}", order.getId());
        }
    }
}
