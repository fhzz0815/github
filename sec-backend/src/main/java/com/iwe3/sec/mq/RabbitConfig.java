package com.iwe3.sec.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * 定义交换机、队列和绑定关系，用于订单相关的异步消息处理
 */
@Configuration
public class RabbitConfig {

    // ========== 交换机名称 ==========
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // ========== 队列名称 ==========
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_PAID_QUEUE = "order.paid.queue";
    public static final String ORDER_KITCHEN_QUEUE = "order.kitchen.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // ========== 路由键 ==========
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_PAID_KEY = "order.paid";
    public static final String ORDER_KITCHEN_KEY = "order.kitchen";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue orderPaidQueue() {
        return QueueBuilder.durable(ORDER_PAID_QUEUE).build();
    }

    @Bean
    public Queue orderKitchenQueue() {
        return QueueBuilder.durable(ORDER_KITCHEN_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding orderPaidBinding(Queue orderPaidQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderPaidQueue).to(orderExchange).with(ORDER_PAID_KEY);
    }

    @Bean
    public Binding orderKitchenBinding(Queue orderKitchenQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderKitchenQueue).to(orderExchange).with(ORDER_KITCHEN_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with("#");
    }
}
