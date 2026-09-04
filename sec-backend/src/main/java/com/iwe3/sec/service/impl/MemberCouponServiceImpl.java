package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.mapper.MemberCouponMapper;
import com.iwe3.sec.service.IMemberCouponService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_coupon 表的业务实现类
 */
@Service
public class MemberCouponServiceImpl implements IMemberCouponService {

    private final MemberCouponMapper memberCouponMapper;

    public MemberCouponServiceImpl(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
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
}
