package com.iwe3.sec.mq;

import com.iwe3.sec.entity.MqConsumeLogEntity;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.DishStockMapper;
import com.iwe3.sec.mapper.MemberCouponMapper;
import com.iwe3.sec.mapper.MqConsumeLogMapper;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.mapper.OrdersMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单消息消费者
 * 异步处理订单创建、支付成功、超时取消等后续操作
 * 支持手动 ack + 消费幂等（mq_consume_log）
 */
@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class OrderMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageConsumer.class);

    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final DishStockMapper dishStockMapper;
    private final MemberCouponMapper memberCouponMapper;
    private final MqConsumeLogMapper mqConsumeLogMapper;

    public OrderMessageConsumer(OrdersMapper ordersMapper,
                                OrderDetailMapper orderDetailMapper,
                                DishStockMapper dishStockMapper,
                                MemberCouponMapper memberCouponMapper,
                                MqConsumeLogMapper mqConsumeLogMapper) {
        this.ordersMapper = ordersMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.dishStockMapper = dishStockMapper;
        this.memberCouponMapper = memberCouponMapper;
        this.mqConsumeLogMapper = mqConsumeLogMapper;
    }

    /**
     * 消费前置检查：通过 mq_consume_log 唯一键实现幂等
     * 返回 true 表示已消费过，跳过后续处理
     */
    private boolean isAlreadyConsumed(String messageId, String queueName) {
        if (messageId == null) return false;
        try {
            MqConsumeLogEntity logEntry = MqConsumeLogEntity.builder()
                    .messageId(messageId)
                    .queueName(queueName)
                    .status(1)
                    .consumedAt(new Date())
                    .build();
            mqConsumeLogMapper.insert(logEntry);
            return false; // 首次插入成功，未消费过
        } catch (DuplicateKeyException e) {
            log.info("消息已消费过，跳过: messageId={}, queue={}", messageId, queueName);
            return true;  // 唯一键冲突，已消费过
        }
    }

    /**
     * 消息处理完成后手动 ack
     */
    private void doAck(Channel channel, Message message, String event) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.debug("消息 ack 成功: {}", event);
        } catch (Exception e) {
            log.error("消息 ack 失败: {}", event, e);
        }
    }

    /**
     * 消息处理失败时 nack（不重新入队，进死信队列）
     */
    private void doNack(Channel channel, Message message, String event) {
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            log.warn("消息 nack（不入队）: {}", event);
        } catch (Exception e) {
            log.error("消息 nack 失败: {}", event, e);
        }
    }

    @RabbitListener(queues = RabbitConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        String messageId = "created:" + orderId;
        if (isAlreadyConsumed(messageId, RabbitConfig.ORDER_CREATED_QUEUE)) return;

        try {
            log.info("【异步处理】订单创建成功，准备后厨打印等操作: orderNo={}", orderNo);
            // 触发后厨打印制作单
            doAck(channel, amqpMessage, "order created: " + orderNo);
        } catch (Exception e) {
            log.error("处理订单创建消息异常: orderNo={}", orderNo, e);
            doNack(channel, amqpMessage, "order created: " + orderNo);
        }
    }

    @RabbitListener(queues = RabbitConfig.ORDER_PAID_QUEUE)
    public void handleOrderPaid(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        String messageId = "paid:" + orderId;
        if (isAlreadyConsumed(messageId, RabbitConfig.ORDER_PAID_QUEUE)) return;

        try {
            log.info("【异步处理】订单支付成功，通知后厨开始制作: orderNo={}", orderNo);
            // 支付成功后打印前台小票
            doAck(channel, amqpMessage, "order paid: " + orderNo);
        } catch (Exception e) {
            log.error("处理支付消息异常: orderNo={}", orderNo, e);
            doNack(channel, amqpMessage, "order paid: " + orderNo);
        }
    }

    @RabbitListener(queues = RabbitConfig.ORDER_KITCHEN_QUEUE)
    public void handleKitchenNotification(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        String messageId = "kitchen:" + orderId;
        if (isAlreadyConsumed(messageId, RabbitConfig.ORDER_KITCHEN_QUEUE)) return;

        try {
            log.info("【异步处理】后厨收到新制作单: orderNo={}", orderNo);
            doAck(channel, amqpMessage, "kitchen: " + orderNo);
        } catch (Exception e) {
            log.error("处理后厨消息异常: orderNo={}", orderNo, e);
            doNack(channel, amqpMessage, "kitchen: " + orderNo);
        }
    }

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handleNotification(Map<String, Object> message, Channel channel, Message amqpMessage) {
        String messageId = "notify:" + System.identityHashCode(message);
        if (isAlreadyConsumed(messageId, RabbitConfig.NOTIFICATION_QUEUE)) return;

        try {
            log.info("【异步处理】发送通知: {}", message);
            doAck(channel, amqpMessage, "notification");
        } catch (Exception e) {
            log.error("处理通知消息异常", e);
            doNack(channel, amqpMessage, "notification");
        }
    }

    /**
     * 超时取消订单消费者
     * 从死信队列接收已过 TTL 的订单消息，自动取消未支付订单
     * 手动 ack + 幂等消费 + 库存回滚 + 优惠券释放
     * 同时有 OrderTimeoutService 定时任务兜底，防止 MQ 消息丢失
     */
    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderTimeout(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        String messageId = "cancel:" + orderId;
        log.info("【超时取消】收到超时订单消息: orderNo={}", orderNo);

        // 幂等检查：防止 MQ 重复投递导致重复取消
        if (isAlreadyConsumed(messageId, RabbitConfig.ORDER_CANCEL_QUEUE)) {
            doAck(channel, amqpMessage, "order cancel (dup): " + orderNo);
            return;
        }

        try {
            // 查订单是否仍为"待支付"状态
            OrdersEntity order = ordersMapper.selectById(orderId);
            if (order == null) {
                log.warn("【超时取消】订单不存在: orderId={}", orderId);
                doAck(channel, amqpMessage, "order cancel (not found): " + orderNo);
                return;
            }
            if (order.getOrderStatus() != 1) {
                log.info("【超时取消】订单已支付或已取消，跳过: orderNo={}, status={}",
                        orderNo, order.getOrderStatus());
                doAck(channel, amqpMessage, "order cancel (skip): " + orderNo);
                return;
            }

            // 使用乐观锁取消订单（只有 version 匹配才成功）
            int affected = ordersMapper.transitionStatus(
                    orderId,
                    1,           // fromStatus = 待支付
                    8,           // toStatus = 已取消
                    null,
                    null,
                    null,
                    new Date(),  // cancelTime
                    "支付超时，系统自动取消",
                    order.getVersion()
            );

            if (affected > 0) {
                log.info("【超时取消】订单自动取消成功: orderNo={}", orderNo);

                // 回滚库存：查询订单菜品明细，逐项恢复库存
                List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(orderId);
                for (OrderDetailEntity detail : details) {
                    Long storeId = order.getStoreId();
                    Long dishId = detail.getDishId();
                    int quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
                    if (quantity > 0) {
                        dishStockMapper.increaseStock(storeId, dishId, quantity);
                        log.debug("库存回滚: storeId={}, dishId={}, quantity={}", storeId, dishId, quantity);
                    }
                }

                // 释放优惠券：把该订单关联的优惠券全部回退为"未使用"
                memberCouponMapper.releaseCouponByOrder(orderId);
                log.info("优惠券已释放: orderId={}", orderId);

                // 更新消费日志状态为成功
                MqConsumeLogEntity logEntry = MqConsumeLogEntity.builder()
                        .messageId(messageId)
                        .queueName(RabbitConfig.ORDER_CANCEL_QUEUE)
                        .status(1)
                        .consumedAt(new Date())
                        .build();
                try {
                    mqConsumeLogMapper.insert(logEntry);
                } catch (DuplicateKeyException ignored) {
                    // 幂等插入，已存在忽略
                }
            } else {
                log.warn("【超时取消】订单状态已变更，取消失败: orderNo={}", orderNo);
            }

            doAck(channel, amqpMessage, "order cancel done: " + orderNo);
        } catch (Exception e) {
            log.error("【超时取消】处理异常: orderNo={}", orderNo, e);
            doNack(channel, amqpMessage, "order cancel error: " + orderNo);
        }
    }

    @RabbitListener(queues = RabbitConfig.ORDER_CANCELLED_QUEUE)
    public void handleOrderCancelled(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        log.info("【订单取消通知】收到取消通知: orderNo={}", orderNo);
        // 此队列用于广播订单被取消，如通知前端刷新、释放台桌等
        doAck(channel, amqpMessage, "order cancelled: " + orderNo);
    }
}
