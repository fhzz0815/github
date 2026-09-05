package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * receipt_template 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptTemplateEntity {

    // 模板ID
    private Long id;
    // 类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值
    private String templateType;
    // 模板名称
    private String templateName;
    // 模板内容
    private String templateContent;
    // 是否默认 1是 0否
    private Integer isDefault;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
