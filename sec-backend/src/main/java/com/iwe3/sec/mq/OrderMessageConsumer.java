package com.iwe3.sec.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单消息消费者
 * 异步处理订单创建、支付成功等后续操作
 */
@Component
public class OrderMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @RabbitListener(queues = RabbitConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(Map<String, Object> message) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        log.info("【异步处理】订单创建成功，准备后厨打印等操作: orderNo={}", orderNo);
    }

    @RabbitListener(queues = RabbitConfig.ORDER_PAID_QUEUE)
    public void handleOrderPaid(Map<String, Object> message) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        log.info("【异步处理】订单支付成功，通知后厨开始制作: orderNo={}", orderNo);
    }

    @RabbitListener(queues = RabbitConfig.ORDER_KITCHEN_QUEUE)
    public void handleKitchenNotification(Map<String, Object> message) {
        Long orderId = (Long) message.get("orderId");
        String orderNo = (String) message.get("orderNo");
        log.info("【异步处理】后厨收到新制作单: orderNo={}", orderNo);
    }

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handleNotification(Map<String, Object> message) {
        log.info("【异步处理】发送通知: {}", message);
    }
}