package com.iwe3.sec.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * 定义交换机、队列和绑定关系，用于订单相关的异步消息处理
 *
 * 超时取消机制（TTL + 死信队列）：
 *   1. 下单时向 order.delay.queue 发消息，设置 expiration = 15分钟
 *   2. TTL 到期后消息自动投递到 order.dlx.exchange（死信交换机）
 *   3. DLX 路由到 order.cancel.queue，消费者执行取消逻辑
 *   4. 定时任务 OrderTimeoutService 兜底（防 MQ 消息丢失）
 *
 * 条件启动：当 rabbitmq.enabled=true 时才加载本配置（默认 true）
 * 开发环境若没有 RabbitMQ，在 application-dev.yml 设置 rabbitmq.enabled=false 即可跳过
 */
@Configuration
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitConfig {

    // ========== 交换机名称 ==========
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String ORDER_DLX_EXCHANGE = "order.dlx.exchange";

    // ========== 队列名称 ==========
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String ORDER_PAID_QUEUE = "order.paid.queue";
    public static final String ORDER_KITCHEN_QUEUE = "order.kitchen.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    /** 超时取消队列：消息在此等待 TTL 到期后进入 DLX */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    /** 取消处理队列：消费者从此队列读取超时订单并执行取消 */
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    /** 取消通知队列：用于广播订单被取消的消息 */
    public static final String ORDER_CANCELLED_QUEUE = "order.cancelled.queue";
    /** 打印队列：订单创建/支付后触发打印 */
    public static final String ORDER_PRINT_QUEUE = "order.print.queue";

    // ========== 路由键 ==========
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_PAID_KEY = "order.paid";
    public static final String ORDER_KITCHEN_KEY = "order.kitchen";
    public static final String ORDER_CANCELLED_KEY = "order.cancelled";
    public static final String ORDER_DELAY_KEY = "order.delay";
    public static final String ORDER_CANCEL_KEY = "order.cancel";
    public static final String ORDER_PRINT_KEY = "order.print";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public DirectExchange orderDlxExchange() {
        return new DirectExchange(ORDER_DLX_EXCHANGE);
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
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable(ORDER_CANCELLED_QUEUE).build();
    }

    /** 打印队列：订单创建/支付后的打印任务在此排队处理 */
    @Bean
    public Queue orderPrintQueue() {
        return QueueBuilder.durable(ORDER_PRINT_QUEUE).build();
    }

    /**
     * 延迟队列：消息在此等待 TTL 到期后投递给死信交换机
     * 下单时发送消息到此队列，设置 expiration = 15分钟
     * TTL 到期后，消息自动转入 order.dlx.exchange → order.cancel.queue
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_CANCEL_KEY)
                .build();
    }

    /**
     * 取消处理队列：消费者从此队列读取已过 TTL 的订单并执行取消
     * 绑定到死信交换机，路由键为 order.cancel
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(ORDER_CANCEL_QUEUE).build();
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

    @Bean
    public Binding orderCancelledBinding(Queue orderCancelledQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCancelledQueue).to(orderExchange).with(ORDER_CANCELLED_KEY);
    }

    @Bean
    public Binding orderDelayBinding(Queue orderDelayQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderDelayQueue).to(orderExchange).with(ORDER_DELAY_KEY);
    }

    @Bean
    public Binding orderCancelBinding(Queue orderCancelQueue, DirectExchange orderDlxExchange) {
        return BindingBuilder.bind(orderCancelQueue).to(orderDlxExchange).with(ORDER_CANCEL_KEY);
    }

    @Bean
    public Binding orderPrintBinding(Queue orderPrintQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderPrintQueue).to(orderExchange).with(ORDER_PRINT_KEY);
    }
}
