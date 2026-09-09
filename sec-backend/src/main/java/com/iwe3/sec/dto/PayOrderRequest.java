package com.iwe3.sec.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 支付订单请求参数
 */
public class PayOrderRequest {

    @NotBlank(message = "支付方式不能为空")
    private String payType;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal actualAmount;

    private BigDecimal memberPayAmount;

    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }
    public BigDecimal getMemberPayAmount() { return memberPayAmount; }
    public void setMemberPayAmount(BigDecimal memberPayAmount) { this.memberPayAmount = memberPayAmount; }
}
