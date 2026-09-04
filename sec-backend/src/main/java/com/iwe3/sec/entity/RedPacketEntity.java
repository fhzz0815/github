package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * red_packet 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedPacketEntity {

    // 红包ID
    private Long id;
    // 红包编号
    private String redPacketNo;
    // 客户ID
    private Long memberId;
    // 所属门店ID
    private Long storeId;
    // 红包金额(元)
    private java.math.BigDecimal amount;
    // 来源 RECHARGE_REWARD充值返利 ACTIVITY活动
    private String source;
    // 状态 1未使用 2已使用 3已过期
    private Integer status;
    // 过期时间
    private java.util.Date expireTime;
    // 使用时间
    private java.util.Date useTime;
    // 使用订单ID
    private Long orderId;
    // 创建时间
    private java.util.Date createTime;
}
