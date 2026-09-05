package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishEntity {

    // 菜品ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 菜品分类ID
    private Long categoryId;
    // 菜品名称
    private String dishName;
    // 菜品编号
    private String dishNo;
    // 菜品图片URL
    private String image;
    // 菜品描述
    private String description;
    // 单价(元)
    private java.math.BigDecimal price;
    // 划线原价(元)，用于展示促销
    private java.math.BigDecimal originPrice;
    // 售卖单位(份/杯/例等)
    private String unit;
    // 默认口味ID
    private Long tasteId;
    // 默认规格ID
    private Long specId;
    // 是否推荐 1是 0否
    private Integer isRecommend;
    // 是否新品 1是 0否
    private Integer isNew;
    // 是否售罄 1是 0否
    private Integer isSoldOut;
    // 累计销量（销售排行用）
    private Integer saleCount;
    // 排序号
    private Integer sort;
    // 状态 1上架 0下架
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
