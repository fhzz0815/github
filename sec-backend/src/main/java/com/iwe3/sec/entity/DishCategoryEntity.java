package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish_category 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishCategoryEntity {

    // 分类ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 分类名称
    private String categoryName;
    // 排序号
    private Integer sort;
    // 状态 1启用 0停用
    private Integer status;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
