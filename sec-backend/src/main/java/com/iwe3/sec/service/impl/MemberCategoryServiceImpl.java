package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberCategoryEntity;
import com.iwe3.sec.mapper.MemberCategoryMapper;
import com.iwe3.sec.service.IMemberCategoryService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_category 表的业务实现类
 */
@Service
public class MemberCategoryServiceImpl implements IMemberCategoryService {

    private final MemberCategoryMapper memberCategoryMapper;

    public MemberCategoryServiceImpl(MemberCategoryMapper memberCategoryMapper) {
        this.memberCategoryMapper = memberCategoryMapper;
    }

    @Override
    public PageResult<MemberCategoryEntity> list(MemberCategoryEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberCategoryEntity> list = memberCategoryMapper.selectList(query);
        PageInfo<MemberCategoryEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public MemberCategoryEntity getById(Long id) {
        return memberCategoryMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(MemberCategoryEntity entity) {
        return memberCategoryMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(MemberCategoryEntity entity) {
        return memberCategoryMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return memberCategoryMapper.deleteById(id) > 0;
    }
}
