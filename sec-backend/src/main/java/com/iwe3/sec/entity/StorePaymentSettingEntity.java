package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * store_payment_setting 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePaymentSettingEntity {

    // 主键ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金
    private String payType;
    // 是否启用 1启用 0停用
    private Integer isEnabled;
    // 创建时间
    private java.util.Date createTime;
}
