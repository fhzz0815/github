package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_coupon 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCouponEntity {

    // 用户券ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 优惠券ID
    private Long couponId;
    // 适用门店ID
    private Long storeId;
    // 状态 1未使用 2已使用 3已过期
    private Integer status;
    // 领取时间
    private java.util.Date receiveTime;
    // 过期时间
    private java.util.Date expireTime;
    // 使用时间
    private java.util.Date useTime;
    // 使用订单ID
    private Long orderId;
    // 创建时间
    private java.util.Date createTime;
}
