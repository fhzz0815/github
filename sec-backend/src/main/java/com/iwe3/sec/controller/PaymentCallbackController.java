package com.iwe3.sec.controller;

import com.iwe3.sec.common.ErrorCode;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.mapper.PaymentRecordMapper;
import com.iwe3.sec.service.IOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Tag(name = "支付回调", description = "微信 / 支付宝异步通知回调接口")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentCallbackController {

    private static final Logger log = LoggerFactory.getLogger(PaymentCallbackController.class);

    private final IOrdersService ordersService;
    private final OrdersMapper ordersMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    public PaymentCallbackController(IOrdersService ordersService,
                                     OrdersMapper ordersMapper,
                                     PaymentRecordMapper paymentRecordMapper) {
        this.ordersService = ordersService;
        this.ordersMapper = ordersMapper;
        this.paymentRecordMapper = paymentRecordMapper;
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/callback/wechat")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> wechatCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到微信支付回调: {}", callbackData);
        return processPaymentCallback("WECHAT", callbackData);
    }

    @Operation(summary = "支付宝支付回调")
    @PostMapping("/callback/alipay")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> alipayCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到支付宝支付回调: {}", callbackData);
        return processPaymentCallback("ALIPAY", callbackData);
    }

    private Result<String> processPaymentCallback(String channel, Map<String, Object> callbackData) {
        String transactionId = (String) callbackData.get("transaction_id");
        String orderNo = (String) callbackData.get("order_no");
        String idempotencyKey = (String) callbackData.get("idempotency_key");
        String payStatus = (String) callbackData.get("pay_status");

        if (orderNo == null || transactionId == null) {
            return Result.error("回调参数不完整");
        }
        if ("FAIL".equals(payStatus)) {
            return Result.error("支付失败");
        }

        OrdersEntity order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getPayStatus() == 2) {
            return Result.success("SUCCESS");
        }

        // 幂等校验
        if (idempotencyKey != null) {
            PaymentRecordEntity existing = paymentRecordMapper.selectByIdempotencyKey(idempotencyKey);
            if (existing != null && existing.getStatus() == 2) {
                return Result.success("SUCCESS");
            }
        }

        try {
            ordersService.payOrder(order.getId(), channel, order.getPayableAmount(),
                    null, idempotencyKey != null ? idempotencyKey : transactionId, 0L);

            PaymentRecordEntity record = paymentRecordMapper.selectByIdempotencyKey(
                idempotencyKey != null ? idempotencyKey : transactionId);
            if (record != null) {
                record.setTransactionId(transactionId);
                record.setNotifyTime(new Date());
                paymentRecordMapper.update(record);
            }
            return Result.success("SUCCESS");
        } catch (BusinessException e) {
            if (e.getCode() == ErrorCode.ORDER_ALREADY_PAID) {
                return Result.success("SUCCESS");
            }
            log.error("支付回调处理失败: orderNo={}, error={}", orderNo, e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}
