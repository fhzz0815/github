package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * payment_record 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordEntity {

    // 支付记录ID
    private Long id;
    // 支付流水号
    private String payNo;
    // 订单ID
    private Long orderId;
    // 门店ID
    private Long storeId;
    // 客户ID
    private Long memberId;
    // 支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付
    private String payType;
    // 支付渠道说明
    private String payChannel;
    // 支付金额(元)
    private java.math.BigDecimal amount;
    // 状态 1待支付 2成功 3失败 4已退款
    private Integer status;
    // 第三方交易号
    private String transactionId;
    // 幂等键（防重复支付回调，同一订单同渠道唯一）
    private String idempotencyKey;
    // 付款账号（脱敏展示）
    private String payerAccount;
    // 支付回调通知时间
    private java.util.Date notifyTime;
    // 支付时间
    private java.util.Date payTime;
    // 创建时间
    private java.util.Date createTime;
}
