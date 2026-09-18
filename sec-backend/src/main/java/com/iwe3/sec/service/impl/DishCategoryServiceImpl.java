package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.mapper.DishCategoryMapper;
import com.iwe3.sec.service.IDishCategoryService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.cache.CacheHelper;
import com.iwe3.sec.common.cache.CacheKey;

import java.util.List;

/**
 * dish_category 表的业务实现类
 */
@Service
public class DishCategoryServiceImpl implements IDishCategoryService {

    private final DishCategoryMapper dishCategoryMapper;
    private final CacheHelper cacheHelper;

    public DishCategoryServiceImpl(DishCategoryMapper dishCategoryMapper,
                                   CacheHelper cacheHelper) {
        this.dishCategoryMapper = dishCategoryMapper;
        this.cacheHelper = cacheHelper;
    }

    @Override
    public PageResult<DishCategoryEntity> list(DishCategoryEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishCategoryEntity> list = dishCategoryMapper.selectList(query);
        PageInfo<DishCategoryEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishCategoryEntity getById(Long id) {
        return cacheHelper.getOrLoad(CacheKey.PREFIX_DISH_CATEGORY + id, CacheKey.TTL_CATEGORY, DishCategoryEntity.class, () -> {
            return dishCategoryMapper.selectById(id);
        });
    }

    @Override
    public boolean add(DishCategoryEntity entity) {
        boolean result = dishCategoryMapper.insert(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_DISH_CATEGORY + entity.getId());
        }
        return result;
    }

    @Override
    public boolean update(DishCategoryEntity entity) {
        boolean result = dishCategoryMapper.update(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_DISH_CATEGORY + entity.getId());
        }
        return result;
    }

    @Override
    public boolean remove(Long id) {
        boolean result = dishCategoryMapper.deleteById(id) > 0;
        if (result) {
            cacheHelper.delete(CacheKey.PREFIX_DISH_CATEGORY + id);
        }
        return result;
    }
}
