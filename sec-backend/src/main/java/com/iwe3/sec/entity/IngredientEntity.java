package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * ingredient 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientEntity {

    // 原料ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 原料类别ID
    private Long categoryId;
    // 原料名称
    private String ingredientName;
    // 计量单位
    private String unit;
    // 规格说明
    private String spec;
    // 状态 1启用 0停用
    private Integer status;
    // 创建时间
    private java.util.Date createTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
