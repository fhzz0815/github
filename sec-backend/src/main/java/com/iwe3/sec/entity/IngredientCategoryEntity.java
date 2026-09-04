package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * ingredient_category 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCategoryEntity {

    // 原料类别ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 类别名称
    private String categoryName;
    // 排序号
    private Integer sort;
    // 创建时间
    private java.util.Date createTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
