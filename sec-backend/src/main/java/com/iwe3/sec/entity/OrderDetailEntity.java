package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * order_detail 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailEntity {

    // 明细ID
    private Long id;
    // 订单ID
    private Long orderId;
    // 菜品ID
    private Long dishId;
    // 菜品名称快照
    private String dishName;
    // 菜品图片快照
    private String dishImage;
    // 成交单价(元)
    private java.math.BigDecimal dishPrice;
    // 规格快照
    private String specName;
    // 口味快照
    private String tasteName;
    // 数量
    private Integer quantity;
    // 小计金额
    private java.math.BigDecimal subtotal;
    // 已退金额(元)，退菜时累加
    private java.math.BigDecimal refundAmount;
    // 制作状态 1待制作 2制作中 3已上齐 4退菜
    private Integer status;
    // 备注
    private String remark;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
