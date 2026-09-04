package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_balance_record 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBalanceRecordEntity {

    // 记录ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 变动金额（正=收入 负=支出）
    private java.math.BigDecimal changeAmount;
    // 变动后余额
    private java.math.BigDecimal balanceAfter;
    // 变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付
    private String changeType;
    // 来源类型 ORDER订单 RECHARGE充值单等
    private String sourceType;
    // 来源ID
    private Long sourceId;
    // 备注
    private String remark;
    // 创建时间
    private java.util.Date createTime;
}
