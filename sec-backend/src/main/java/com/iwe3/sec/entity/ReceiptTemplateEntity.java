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
}
