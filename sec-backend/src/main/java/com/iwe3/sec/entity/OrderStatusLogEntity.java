package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * order_status_log 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusLogEntity {

    // 日志ID
    private Long id;
    // 订单ID
    private Long orderId;
    // 原状态
    private Integer fromStatus;
    // 新状态
    private Integer toStatus;
    // 操作方 CLIENT客户端 STAFF员工 SYSTEM系统
    private String operatorType;
    // 操作者ID
    private Long operatorId;
    // 说明
    private String remark;
    // 创建时间
    private java.util.Date createTime;
}
