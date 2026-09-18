package com.iwe3.sec.mq;

import com.iwe3.sec.entity.MqConsumeLogEntity;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.MqConsumeLogMapper;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.service.IPrintService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 打印消息消费者
 * 异步处理订单打印任务，不阻塞主流程
 */
@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class PrintMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(PrintMessageConsumer.class);

    private final OrdersMapper ordersMapper;
    private final IPrintService printService;
    private final MqConsumeLogMapper mqConsumeLogMapper;

    public PrintMessageConsumer(OrdersMapper ordersMapper,
                                IPrintService printService,
                                MqConsumeLogMapper mqConsumeLogMapper) {
        this.ordersMapper = ordersMapper;
        this.printService = printService;
        this.mqConsumeLogMapper = mqConsumeLogMapper;
    }

    @RabbitListener(queues = RabbitConfig.ORDER_PRINT_QUEUE)
    public void handlePrintJob(Map<String, Object> message, Channel channel, Message amqpMessage) {
        Long orderId = (Long) message.get("orderId");
        String printType = (String) message.get("printType");
        String messageId = "print:" + printType + ":" + orderId;

        // 幂等检查
        if (isAlreadyConsumed(messageId)) {
            doAck(channel, amqpMessage, "print (dup): " + messageId);
            return;
        }

        try {
            // 查询订单
            OrdersEntity order = ordersMapper.selectById(orderId);
            if (order == null) {
                log.warn("打印任务：订单不存在 orderId={}", orderId);
                doAck(channel, amqpMessage, "print (not found): " + messageId);
                return;
            }

            // 根据打印类型执行打印
            if ("KITCHEN".equals(printType)) {
                printService.printKitchenTicket(order);
            } else if ("RECEIPT".equals(printType)) {
                printService.printReceipt(order);
            } else {
                log.warn("未知的打印类型: {}", printType);
            }

            doAck(channel, amqpMessage, "print done: " + messageId);
        } catch (Exception e) {
            log.error("打印任务处理异常: orderId={}, printType={}", orderId, printType, e);
            doNack(channel, amqpMessage, "print error: " + messageId);
        }
    }

    private boolean isAlreadyConsumed(String messageId) {
        if (messageId == null) return false;
        try {
            MqConsumeLogEntity logEntry = MqConsumeLogEntity.builder()
                    .messageId(messageId)
                    .queueName(RabbitConfig.ORDER_PRINT_QUEUE)
                    .status(1)
                    .consumedAt(new Date())
                    .build();
            mqConsumeLogMapper.insert(logEntry);
            return false;
        } catch (DuplicateKeyException e) {
            return true;
        }
    }

    private void doAck(Channel channel, Message message, String event) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("消息 ack 失败: {}", event, e);
        }
    }

    private void doNack(Channel channel, Message message, String event) {
        try {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } catch (Exception e) {
            log.error("消息 nack 失败: {}", event, e);
        }
    }
}
