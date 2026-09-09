package com.iwe3.sec.dto;

import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrdersEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 下单请求参数
 * 包含订单信息和菜品明细列表
 */
public class SubmitOrderRequest {

    @NotNull(message = "订单信息不能为空")
    @Valid
    private OrdersEntity order;

    @NotEmpty(message = "菜品明细不能为空")
    @Valid
    private List<OrderDetailEntity> details;

    public OrdersEntity getOrder() { return order; }
    public void setOrder(OrdersEntity order) { this.order = order; }
    public List<OrderDetailEntity> getDetails() { return details; }
    public void setDetails(List<OrderDetailEntity> details) { this.details = details; }
}
