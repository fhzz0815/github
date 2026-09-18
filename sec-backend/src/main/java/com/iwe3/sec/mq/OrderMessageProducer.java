package com.iwe3.sec.mq;

import com.iwe3.sec.entity.OrdersEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单消息生产者
 * 订单创建、支付、制作完成时发送消息，让其他模块异步处理
 *
 * 条件注入：只有 RabbitTemplate 可用时才加载（即 RabbitMQ 已连接）
 * 开发环境若没有 RabbitMQ，不会影响程序正常启动
 */
@Component
@ConditionalOnBean(RabbitTemplate.class)
public class OrderMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送订单创建消息
     * 触发：后厨打印制作单、通知客户、更新排队状态等
     */
    public void sendOrderCreated(OrdersEntity order) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("orderType", order.getOrderType());
        message.put("tableId", order.getTableId());
        message.put("timestamp", System.currentTimeMillis());

        log.info("发送订单创建消息: orderNo={}", order.getOrderNo());
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_CREATED_KEY, message);
    }

    /**
     * 发送订单支付成功消息
     * 触发：通知后厨开始制作、发送支付成功通知给客户等
     */
    public void sendOrderPaid(OrdersEntity order) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("payAmount", order.getActualAmount());
        message.put("timestamp", System.currentTimeMillis());

        log.info("发送订单支付消息: orderNo={}", order.getOrderNo());
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_PAID_KEY, message);
    }

    /**
     * 发送后厨制作通知
     * 触发：新订单需要后厨制作时通知
     */
    public void sendKitchenNotification(OrdersEntity order) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("tableId", order.getTableId());
        message.put("timestamp", System.currentTimeMillis());

        log.info("发送后厨制作通知: orderNo={}", order.getOrderNo());
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_KITCHEN_KEY, message);
    }

    /**
     * 发送打印任务消息
     * 触发：订单创建/支付成功时，异步通知打印机出票
     * @param order 订单信息
     * @param printType 打印类型：KITCHEN=后厨制作单, RECEIPT=前台小票
     */
    public void sendPrintJob(OrdersEntity order, String printType) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("printType", printType);
        message.put("timestamp", System.currentTimeMillis());

        log.info("发送打印任务: orderNo={}, printType={}", order.getOrderNo(), printType);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_PRINT_KEY, message);
    }

    /**
     * 发送订单取消消息
     * 触发：订单被取消时，通知释放库存、更新排队状态等
     */
    public void sendOrderCancelled(OrdersEntity order) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("cancelReason", order.getCancelReason());
        message.put("timestamp", System.currentTimeMillis());

        log.info("发送订单取消消息: orderNo={}", order.getOrderNo());
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_CANCELLED_KEY, message);
    }

    /**
     * 发送超时取消延迟消息
     * 触发：下单后立即发送，消息在延迟队列中等待 TTL 到期
     *       到期后自动转入死信交换机 → order.cancel.queue
     * @param order 订单信息
     * @param delayMs 延迟时间（毫秒），配置项 order.timeout-minutes 控制（默认 30 分钟 = 1800000 毫秒）
     */
    public void sendDelayCancel(OrdersEntity order, long delayMs) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("orderNo", order.getOrderNo());
        message.put("storeId", order.getStoreId());
        message.put("timestamp", System.currentTimeMillis());

        // 设置消息持久化 + TTL（消息在延迟队列中的存活时间）
        MessageProperties props = new MessageProperties();
        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        props.setExpiration(String.valueOf(delayMs));

        org.springframework.amqp.core.Message amqpMsg =
                new org.springframework.amqp.core.Message(
                        rabbitTemplate.getMessageConverter().toMessage(message, props).getBody(),
                        props
                );

        log.info("发送超时取消延迟消息: orderNo={}, delayMs={}", order.getOrderNo(), delayMs);
        rabbitTemplate.send(RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_DELAY_KEY, amqpMsg);
    }
}
