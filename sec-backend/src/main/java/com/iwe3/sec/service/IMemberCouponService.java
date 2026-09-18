package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_coupon 表的业务接口
 */
public interface IMemberCouponService {

    /** 分页查询列表 */
    PageResult<MemberCouponEntity> list(MemberCouponEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberCouponEntity getById(Long id);

    /** 新增 */
    boolean add(MemberCouponEntity entity);

    /** 修改 */
    boolean update(MemberCouponEntity entity);

    /** 删除 */
    boolean remove(Long id);

    /**
     * 会员领取优惠券（带分布式锁防超发）
     *
     * @param couponId  优惠券 ID
     * @param memberId  会员 ID
     * @param storeId   门店 ID
     * @return 会员优惠券记录 ID
     */
    Long claimCoupon(Long couponId, Long memberId, Long storeId);
}
