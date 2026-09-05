package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_address 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressEntity {

    // 地址ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 联系人姓名
    private String contactName;
    // 联系人电话
    private String contactPhone;
    // 省份
    private String province;
    // 城市
    private String city;
    // 区县
    private String district;
    // 详细地址
    private String detailAddress;
    // 经度
    private java.math.BigDecimal longitude;
    // 纬度
    private java.math.BigDecimal latitude;
    // 地址标签 家/公司
    private String tag;
    // 是否默认 1是 0否
    private Integer isDefault;
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
