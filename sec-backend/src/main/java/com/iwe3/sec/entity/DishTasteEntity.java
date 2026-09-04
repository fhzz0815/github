package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish_taste 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishTasteEntity {

    // 口味ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 口味名称
    private String tasteName;
    // 排序号
    private Integer sort;
    // 状态 1启用 0停用
    private Integer status;
    // 创建时间
    private java.util.Date createTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
