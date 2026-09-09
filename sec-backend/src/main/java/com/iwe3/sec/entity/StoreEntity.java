package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * store 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreEntity {

    // 门店ID
    private Long id;
    // 门店编号
    private String storeNo;
    // 门店名称
    private String storeName;
    // 省份
    private String province;
    // 城市
    private String city;
    // 区县
    private String district;
    // 详细地址
    private String address;
    // 经度
    private java.math.BigDecimal longitude;
    // 纬度
    private java.math.BigDecimal latitude;
    // 门店电话
    private String phone;
    // 负责人
    private String contactName;
    // 营业状态 1营业 0打烊
    private Integer businessStatus;
    // 开始营业时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date openTime;
    // 结束营业时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date closeTime;
    // 是否允许堂食 1是 0否
    private Integer dineInEnabled;
    // 是否允许外卖 1是 0否
    private Integer takeoutEnabled;
    // 配送范围(km)
    private java.math.BigDecimal deliveryRadius;
    // 起送价(元)
    private java.math.BigDecimal minOrderAmount;
    // 基础配送费(元)
    private java.math.BigDecimal deliveryFee;
    // 免配送费门槛(元)
    private java.math.BigDecimal deliveryFreeThreshold;
    // 配送开始时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date deliveryStartTime;
    // 配送结束时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date deliveryEndTime;
    // 门店Logo
    private String logo;
    // 门店简介
    private String description;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    @JsonIgnore
    private Integer isDeleted;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchEndTime;
}
