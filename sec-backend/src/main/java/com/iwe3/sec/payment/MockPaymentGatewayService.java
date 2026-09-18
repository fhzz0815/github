package com.iwe3.sec.payment;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;
import com.iwe3.sec.service.pay.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 模拟支付网关服务
 * <p>
 * 开发阶段（@Profile("dev")）用于模拟微信/支付宝的支付流程。
 * 同时实现 PaymentGateway 接口，方便统一管理。
 * 上线后替换为 WechatPayGatewayImpl / AlipayGatewayImpl 真实对接。
 * <p>
 * 功能：
 * 1. 模拟支付下单（5% 随机失败率，用于测试异常流程）
 * 2. 模拟查询支付结果
 * 3. 模拟退款
 */
@Profile("dev")
@Service
public class MockPaymentGatewayService implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGatewayService.class);

    @Override
    public PayResult prepay(String orderNo, BigDecimal amount, String description, Long storeId) {
        log.info("【Mock支付-预下单】orderNo={}, amount={}, desc={}", orderNo, amount, description);

        // 模拟 5% 的支付失败概率
        if (Math.random() < 0.05) {
            log.warn("【Mock支付】模拟支付失败: orderNo={}", orderNo);
            PayResult result = new PayResult();
            result.setSuccess(false);
            result.setErrorMessage("模拟支付失败，请重试");
            return result;
        }

        return mockSuccessResult(orderNo);
    }

    @Override
    public PayResult query(String transactionId) {
        log.info("【Mock支付-查询】transactionId={}", transactionId);
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setPaySuccess(true);
        result.setTransactionId(transactionId);
        return result;
    }

    @Override
    public PayResult refund(String transactionId, BigDecimal refundAmount, String reason) {
        log.info("【Mock支付-退款】transactionId={}, amount={}, reason={}", transactionId, refundAmount, reason);
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setTransactionId(transactionId);
        return result;
    }

    @Override
    public boolean verifyCallback(Object callbackData, String signature) {
        log.info("【Mock支付-验签】默认返回 true");
        return true;
    }

    @Override
    public String getChannel() {
        return "MOCK";
    }

    /**
     * 模拟支付下单（按支付类型路由）
     */
    public MockPayResult mockPlaceOrder(String orderNo, String payType, BigDecimal amount, Long storeId) {
        PayResult result = prepay(orderNo, amount, "", storeId);
        return toMockPayResult(result, payType);
    }

    /**
     * 模拟查询支付结果
     */
    public MockPayResult mockQueryResult(String transactionId) {
        PayResult result = query(transactionId);
        return toMockPayResult(result, "MOCK");
    }

    /**
     * 模拟退款
     */
    public MockPayResult mockRefund(String transactionId, BigDecimal amount) {
        PayResult result = refund(transactionId, amount, "");
        return toMockPayResult(result, "MOCK");
    }

    private PayResult mockSuccessResult(String orderNo) {
        String mockTxnId = "MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setTransactionId(mockTxnId);
        result.setQrCodeUrl("https://pay.mock-qr.com/mock/" + orderNo);
        result.setPayUrl("mock://pay/" + mockTxnId);
        return result;
    }

    private MockPayResult toMockPayResult(PayResult result, String payType) {
        MockPayResult mockResult = new MockPayResult();
        mockResult.setSuccess(result.isSuccess());
        mockResult.setTransactionId(result.getTransactionId());
        mockResult.setQrCodeUrl(result.getQrCodeUrl());
        mockResult.setPayUrl(result.getPayUrl());
        mockResult.setPaySuccess(result.isPaySuccess());
        return mockResult;
    }

    /**
     * 模拟支付结果（保留旧代码兼容性）
     */
    public static class MockPayResult {
        private boolean success;
        private String transactionId;
        private String qrCodeUrl;
        private String payUrl;
        private boolean paySuccess = true;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getQrCodeUrl() { return qrCodeUrl; }
        public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
        public String getPayUrl() { return payUrl; }
        public void setPayUrl(String payUrl) { this.payUrl = payUrl; }
        public boolean isPaySuccess() { return paySuccess; }
        public void setPaySuccess(boolean paySuccess) { this.paySuccess = paySuccess; }
    }
}
