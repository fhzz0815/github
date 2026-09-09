package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.mapper.DishCategoryMapper;
import com.iwe3.sec.service.IDishCategoryService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_category 表的业务实现类
 */
@Service
public class DishCategoryServiceImpl implements IDishCategoryService {

    private final DishCategoryMapper dishCategoryMapper;

    public DishCategoryServiceImpl(DishCategoryMapper dishCategoryMapper) {
        this.dishCategoryMapper = dishCategoryMapper;
    }

    @Override
    public PageResult<DishCategoryEntity> list(DishCategoryEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishCategoryEntity> list = dishCategoryMapper.selectList(query);
        PageInfo<DishCategoryEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public DishCategoryEntity getById(Long id) {
        return dishCategoryMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(DishCategoryEntity entity) {
        return dishCategoryMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(DishCategoryEntity entity) {
        return dishCategoryMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return dishCategoryMapper.deleteById(id) > 0;
    }
}
