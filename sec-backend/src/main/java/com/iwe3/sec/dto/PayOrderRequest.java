package com.iwe3.sec.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 支付订单请求参数
 *
 * idempotencyKey（幂等键）说明：
 *   每次支付请求必须传入唯一幂等键，用于防止重复支付回调
 *   同一个幂等键重复请求时，系统会直接返回成功而不会重复扣款
 *   建议生成规则：pay_{orderId}_{uuid} 或使用支付渠道返回的交易号
 */
public class PayOrderRequest {

    @NotBlank(message = "支付方式不能为空")
    private String payType;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal actualAmount;

    private BigDecimal memberPayAmount;

    /** 幂等键：用于防止重复支付（建议使用支付渠道交易号或 UUID） */
    @NotBlank(message = "幂等键不能为空")
    private String idempotencyKey;

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
    public BigDecimal getMemberPayAmount() { return memberPayAmount; }
    public void setMemberPayAmount(BigDecimal memberPayAmount) { this.memberPayAmount = memberPayAmount; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
