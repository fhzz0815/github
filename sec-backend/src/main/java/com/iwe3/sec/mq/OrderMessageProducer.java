package com.iwe3.sec.mq;

import com.iwe3.sec.entity.OrdersEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单消息生产者
 * 订单创建、支付、制作完成时发送消息，让其他模块异步处理
 */
@Component
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
}
