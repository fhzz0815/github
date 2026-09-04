package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_category 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCategoryEntity {

    // 会员类别ID
    private Long id;
    // 类别名称
    private String categoryName;
    // 折扣率(0.90=9折)
    private java.math.BigDecimal discountRate;
    // 充值规则说明
    private String rechargeRule;
    // 类别描述
    private String description;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
