package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * ingredient_stock 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientStockEntity {

    // 原料库存ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 原料ID
    private Long ingredientId;
    // 当前库存数量
    private java.math.BigDecimal stockQuantity;
    // 预警阈值
    private java.math.BigDecimal warnThreshold;
    // 计量单位
    private String unit;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
}
