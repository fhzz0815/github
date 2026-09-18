package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.mapper.MemberCouponMapper;
import com.iwe3.sec.mapper.CouponMapper;
import com.iwe3.sec.service.IMemberCouponService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;
import com.iwe3.sec.common.lock.DistributedLockTemplate;

import java.util.Date;
import java.util.List;

/**
 * member_coupon 表的业务实现类
 */
@Service
public class MemberCouponServiceImpl implements IMemberCouponService {

    private final MemberCouponMapper memberCouponMapper;
    private final CouponMapper couponMapper;
    private final DistributedLockTemplate distributedLockTemplate;

    public MemberCouponServiceImpl(MemberCouponMapper memberCouponMapper,
                                   CouponMapper couponMapper,
                                   DistributedLockTemplate distributedLockTemplate) {
        this.memberCouponMapper = memberCouponMapper;
        this.couponMapper = couponMapper;
        this.distributedLockTemplate = distributedLockTemplate;
    }

    @Override
    public PageResult<MemberCouponEntity> list(MemberCouponEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberCouponEntity> list = memberCouponMapper.selectList(query);
        PageInfo<MemberCouponEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberCouponEntity getById(Long id) {
        return memberCouponMapper.selectById(id);
    }

    @Override
    public boolean add(MemberCouponEntity entity) {
        return memberCouponMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberCouponEntity entity) {
        return memberCouponMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberCouponMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long claimCoupon(Long couponId, Long memberId, Long storeId) {
        // 分布式锁：按优惠券粒度锁，防止同一张券并发超发
        String lockKey = "sr:lock:coupon:" + couponId;
        try {
            return distributedLockTemplate.tryLock(lockKey, () -> {
                // 1. 查询优惠券信息
                CouponEntity coupon = couponMapper.selectById(couponId);
                if (coupon == null) {
                    throw new BusinessException(ErrorCode.COUPON_NOT_FOUND, "优惠券不存在");
                }
                if (coupon.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.COUPON_EXPIRED, "优惠券已下架");
                }

                // 2. 校验有效期
                Date now = new Date();
                if (coupon.getValidStart() != null && now.before(coupon.getValidStart())) {
                    throw new BusinessException(ErrorCode.COUPON_NOT_STARTED, "优惠券还未到领取时间");
                }
                if (coupon.getValidEnd() != null && now.after(coupon.getValidEnd())) {
                    throw new BusinessException(ErrorCode.COUPON_EXPIRED, "优惠券已过期");
                }

                // 3. 校验每人限领
                if (coupon.getPerUserLimit() != null && coupon.getPerUserLimit() > 0) {
                    MemberCouponEntity query = MemberCouponEntity.builder()
                            .couponId(couponId)
                            .memberId(memberId)
                            .build();
                    List<MemberCouponEntity> existingList = memberCouponMapper.selectList(query);
                    if (existingList != null && existingList.size() >= coupon.getPerUserLimit()) {
                        throw new BusinessException(ErrorCode.COUPON_LIMIT_REACHED,
                                "每人限领 " + coupon.getPerUserLimit() + " 张，您已领取 " + existingList.size() + " 张");
                    }
                }

                // 4. 原子递增已发放数量（防超发）
                int affected = couponMapper.incrementIssuedCount(couponId);
                if (affected == 0) {
                    throw new BusinessException(ErrorCode.COUPON_SOLD_OUT, "优惠券已被领完");
                }

                // 5. 计算有效期
                Date validStart = coupon.getValidStart();
                Date validEnd = coupon.getValidEnd();
                if (coupon.getValidType() == 2 && coupon.getValidDays() != null) {
                    // 领取后 N 天有效
                    validStart = now;
                    validEnd = new Date(now.getTime() + (long) coupon.getValidDays() * 24 * 60 * 60 * 1000);
                }

                // 6. 插入会员优惠券记录（状态为 1=未使用）
                MemberCouponEntity memberCoupon = MemberCouponEntity.builder()
                        .couponId(couponId)
                        .memberId(memberId)
                        .storeId(storeId)
                        .status(1) // 未使用（1=未使用，2=已使用，3=已过期）
                        .receiveTime(now)
                        .expireTime(validEnd)
                        .build();
                memberCouponMapper.insert(memberCoupon);

                return memberCoupon.getId();
            });
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "领取优惠券失败，请稍后重试");
        }
    }
}
