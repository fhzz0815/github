package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * coupon 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {

    // 优惠券ID
    private Long id;
    // 门店ID(NULL为通用券)
    private Long storeId;
    // 优惠券名称
    private String couponName;
    // 类型 1满减 2折扣 3立减
    private Integer couponType;
    // 使用门槛金额(元)
    private java.math.BigDecimal thresholdAmount;
    // 减免金额(元)(满减/立减)
    private java.math.BigDecimal discountAmount;
    // 折扣率(折扣券)
    private java.math.BigDecimal discountRate;
    // 发行总量(0不限量)
    private Integer totalCount;
    // 已发放数量
    private Integer issuedCount;
    // 每人限领数量
    private Integer perUserLimit;
    // 有效期类型 1固定时间 2领取后N天
    private Integer validType;
    // 生效开始时间
    private java.util.Date validStart;
    // 生效结束时间
    private java.util.Date validEnd;
    // 领取后有效天数
    private Integer validDays;
    // 使用范围 1全场 2指定分类
    private Integer useScope;
    // 适用菜品分类ID（use_scope=2时）
    private Long scopeCategoryId;
    // 状态 1草稿 2已发布 3已结束
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
