package com.iwe3.sec.dto;

/**
 * 取消订单请求参数
 */
public class CancelOrderRequest {

    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
