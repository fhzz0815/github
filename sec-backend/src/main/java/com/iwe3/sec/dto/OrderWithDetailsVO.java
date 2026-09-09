package com.iwe3.sec.dto;

import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.entity.OrdersEntity;

import java.util.List;

/**
 * 订单详情 + 明细 + 状态日志的聚合视图
 * 用于 /api/v1/orders/{id}/with-details 接口返回
 */
public class OrderWithDetailsVO {

    private OrdersEntity order;
    private List<OrderDetailEntity> details;
    private List<OrderStatusLogEntity> statusLogs;

    public OrderWithDetailsVO(OrdersEntity order, List<OrderDetailEntity> details,
                              List<OrderStatusLogEntity> statusLogs) {
        this.order = order;
        this.details = details;
        this.statusLogs = statusLogs;
    }

    public OrdersEntity getOrder() { return order; }
    public List<OrderDetailEntity> getDetails() { return details; }
    public List<OrderStatusLogEntity> getStatusLogs() { return statusLogs; }
}
