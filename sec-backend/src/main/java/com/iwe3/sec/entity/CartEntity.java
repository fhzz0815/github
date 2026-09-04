package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * cart 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {

    // 购物车ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 门店ID
    private Long storeId;
    // 菜品ID
    private Long dishId;
    // 菜品名称快照
    private String dishName;
    // 菜品图片快照
    private String dishImage;
    // 规格快照
    private String specName;
    // 口味快照
    private String tasteName;
    // 加入时单价
    private java.math.BigDecimal price;
    // 数量
    private Integer quantity;
    // 加入时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
}
