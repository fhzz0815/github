package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishTasteEntity;
import com.iwe3.sec.mapper.DishTasteMapper;
import com.iwe3.sec.service.IDishTasteService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_taste 表的业务实现类
 */
@Service
public class DishTasteServiceImpl implements IDishTasteService {

    private final DishTasteMapper dishTasteMapper;

    public DishTasteServiceImpl(DishTasteMapper dishTasteMapper) {
        this.dishTasteMapper = dishTasteMapper;
    }

    @Override
    public PageResult<DishTasteEntity> list(DishTasteEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishTasteEntity> list = dishTasteMapper.selectList(query);
        PageInfo<DishTasteEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public DishTasteEntity getById(Long id) {
        return dishTasteMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(DishTasteEntity entity) {
        return dishTasteMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(DishTasteEntity entity) {
        return dishTasteMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return dishTasteMapper.deleteById(id) > 0;
    }
}
