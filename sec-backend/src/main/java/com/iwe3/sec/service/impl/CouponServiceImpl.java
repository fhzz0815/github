package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.mapper.CouponMapper;
import com.iwe3.sec.service.ICouponService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * coupon 表的业务实现类
 */
@Service
public class CouponServiceImpl implements ICouponService {

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    public PageResult<CouponEntity> list(CouponEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CouponEntity> list = couponMapper.selectList(query);
        PageInfo<CouponEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public CouponEntity getById(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(CouponEntity entity) {
        return couponMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(CouponEntity entity) {
        return couponMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return couponMapper.deleteById(id) > 0;
    }
}
