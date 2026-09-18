package com.iwe3.sec.service.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * 微信支付实现（Mock 模式）
 * 开发阶段使用 @Profile("dev") 限定，无需真实微信商户号
 * 上线时替换为真实 wechatpay-java SDK 调用
 */
@Profile("dev")
@Service
public class WechatPayGatewayImpl implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(WechatPayGatewayImpl.class);

    @Override
    public PayResult prepay(String orderNo, BigDecimal amount, String description, Long storeId) {
        log.info("【微信支付-预下单】orderNo={}, amount={}, desc={}", orderNo, amount, description);
        return mockSuccessResult(orderNo);
    }

    @Override
    public PayResult query(String transactionId) {
        log.info("【微信支付-查询】transactionId={}", transactionId);
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setPaySuccess(true);
        result.setTransactionId(transactionId);
        return result;
    }

    @Override
    public PayResult refund(String transactionId, BigDecimal refundAmount, String reason) {
        log.info("【微信支付-退款】transactionId={}, amount={}, reason={}", transactionId, refundAmount, reason);
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setTransactionId(transactionId);
        return result;
    }

    @Override
    public boolean verifyCallback(Object callbackData, String signature) {
        log.info("【微信支付-验签】默认返回 true（Mock 模式）");
        return true;
    }

    @Override
    public String getChannel() {
        return "WECHAT";
    }

    private PayResult mockSuccessResult(String orderNo) {
        String mockTxnId = "WX_MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        PayResult result = new PayResult();
        result.setSuccess(true);
        result.setTransactionId(mockTxnId);
        result.setQrCodeUrl("https://pay.mock-qr.com/wechat/" + orderNo);
        result.setPayUrl("weixin://wxpay/bizpayurl?pr=" + mockTxnId);
        return result;
    }
}