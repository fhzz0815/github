package com.iwe3.sec.service;

import java.math.BigDecimal;

/**
 * 会员钱包服务接口
 * 统一管理会员余额与积分的增减操作，确保主档数值与流水表在同一个事务内保持一致
 * 业务方（比如下单、充值、退款）应该调用这里的方法，而不是直接改 member 表的余额/积分字段
 */
public interface IMemberWalletService {

    /**
     * 充值：增加余额并写入余额流水
     *
     * @param memberId      会员ID
     * @param amount        充值金额
     * @param giftAmount    赠送金额（没有就传 0）
     * @param sourceType    来源类型（RECHARGE=充值、REFUND=退款、等等）
     * @param sourceId      来源业务ID（比如充值记录ID、退款单ID）
     * @param remark        备注
     * @return 操作后的余额
     */
    BigDecimal rechargeBalance(Long memberId, BigDecimal amount, BigDecimal giftAmount,
                               String sourceType, Long sourceId, String remark);

    /**
     * 消费扣减余额（比如会员卡支付）
     *
     * @param memberId    会员ID
     * @param amount      扣减金额
     * @param sourceType  来源类型（ORDER_PAY=订单支付）
     * @param sourceId    来源业务ID（比如订单ID）
     * @param remark      备注
     * @return 操作后的余额，如果余额不够返回 null
     */
    BigDecimal deductBalance(Long memberId, BigDecimal amount,
                             String sourceType, Long sourceId, String remark);

    /**
     * 增加积分（消费送积分、签到等）
     *
     * @param memberId    会员ID
     * @param points      增加积分数
     * @param sourceType  来源类型（ORDER_EARN=消费获得、SIGN_IN=签到）
     * @param sourceId    来源业务ID
     * @param remark      备注
     * @return 操作后的可用积分
     */
    Integer earnPoints(Long memberId, Integer points,
                       String sourceType, Long sourceId, String remark);

    /**
     * 扣减积分（积分兑换、抵扣等）
     *
     * @param memberId    会员ID
     * @param points      扣减积分数
     * @param sourceType  来源类型（REDEEM=兑换、DEDUCT=抵扣）
     * @param sourceId    来源业务ID
     * @param remark      备注
     * @return 操作后的可用积分，如果积分不够返回 null
     */
    Integer deductPoints(Long memberId, Integer points,
                         String sourceType, Long sourceId, String remark);
}
