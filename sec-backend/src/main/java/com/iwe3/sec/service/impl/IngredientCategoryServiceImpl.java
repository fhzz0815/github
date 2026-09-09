package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.IngredientCategoryEntity;
import com.iwe3.sec.mapper.IngredientCategoryMapper;
import com.iwe3.sec.service.IIngredientCategoryService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * ingredient_category 表的业务实现类
 */
@Service
public class IngredientCategoryServiceImpl implements IIngredientCategoryService {

    private final IngredientCategoryMapper ingredientCategoryMapper;

    public IngredientCategoryServiceImpl(IngredientCategoryMapper ingredientCategoryMapper) {
        this.ingredientCategoryMapper = ingredientCategoryMapper;
    }

    @Override
    public PageResult<IngredientCategoryEntity> list(IngredientCategoryEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<IngredientCategoryEntity> list = ingredientCategoryMapper.selectList(query);
        PageInfo<IngredientCategoryEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public IngredientCategoryEntity getById(Long id) {
        return ingredientCategoryMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(IngredientCategoryEntity entity) {
        return ingredientCategoryMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(IngredientCategoryEntity entity) {
        return ingredientCategoryMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return ingredientCategoryMapper.deleteById(id) > 0;
    }
}
