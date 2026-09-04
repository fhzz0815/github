package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish_ingredient_rel 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredientRelEntity {

    // 关联ID
    private Long id;
    // 菜品ID
    private Long dishId;
    // 原料ID
    private Long ingredientId;
    // 用量
    private java.math.BigDecimal quantity;
    // 单位
    private String unit;
    // 创建时间
    private java.util.Date createTime;
}
