package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_recharge_record 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRechargeRecordEntity {

    // 充值记录ID
    private Long id;
    // 充值单号
    private String rechargeNo;
    // 客户ID
    private Long memberId;
    // 充值门店ID
    private Long storeId;
    // 充值金额(元)
    private java.math.BigDecimal rechargeAmount;
    // 赠送金额(元)
    private java.math.BigDecimal giftAmount;
    // 支付方式 WECHAT/ALIPAY/CASH
    private String payType;
    // 状态 1成功 0失败
    private Integer status;
    // 操作收银员工ID
    private Long operatorId;
    // 充值时间
    private java.util.Date rechargeTime;
    // 备注
    private String remark;
}
