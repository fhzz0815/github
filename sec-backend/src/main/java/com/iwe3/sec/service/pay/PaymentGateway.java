package com.iwe3.sec.service.pay;

import java.math.BigDecimal;

/**
 * 支付网关接口
 * 统一封装微信/支付宝的支付、查询、退款和回调验签操作
 * 开发环境使用 @Profile("dev") Mock 实现，无需真实商户号
 */
public interface PaymentGateway {

    /**
     * 统一下单（预支付）
     * @param orderNo 订单号
     * @param amount 支付金额
     * @param description 商品描述
     * @param storeId 门店ID
     * @return 支付结果（含交易号、支付链接等）
     */
    PayResult prepay(String orderNo, BigDecimal amount, String description, Long storeId);

    /**
     * 查询支付结果
     * @param transactionId 第三方交易号
     * @return 支付结果
     */
    PayResult query(String transactionId);

    /**
     * 申请退款
     * @param transactionId 第三方交易号
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return 退款结果
     */
    PayResult refund(String transactionId, BigDecimal refundAmount, String reason);

    /**
     * 验证回调签名
     * @param callbackData 回调参数（已解析为 Map）
     * @param signature 签名字符串
     * @return 验签是否通过
     */
    boolean verifyCallback(Object callbackData, String signature);

    /**
     * 获取支付渠道名称
     * @return WECHAT / ALIPAY
     */
    String getChannel();

    /**
     * 支付结果封装
     */
    class PayResult {
        private boolean success;
        private String transactionId;
        private String qrCodeUrl;
        private String payUrl;
        private String errorMessage;
        private boolean paySuccess = true;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getQrCodeUrl() { return qrCodeUrl; }
        public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
        public String getPayUrl() { return payUrl; }
        public void setPayUrl(String payUrl) { this.payUrl = payUrl; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public boolean isPaySuccess() { return paySuccess; }
        public void setPaySuccess(boolean paySuccess) { this.paySuccess = paySuccess; }
    }
}