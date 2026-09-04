package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.mapper.DishMapper;
import com.iwe3.sec.service.IDishService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish 表的业务实现类
 */
@Service
public class DishServiceImpl implements IDishService {

    private final DishMapper dishMapper;

    public DishServiceImpl(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    @Override
    public PageResult<DishEntity> list(DishEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishEntity> list = dishMapper.selectList(query);
        PageInfo<DishEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishEntity getById(Long id) {
        return dishMapper.selectById(id);
    }

    @Override
    public boolean add(DishEntity entity) {
        return dishMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishEntity entity) {
        return dishMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return dishMapper.deleteById(id) > 0;
    }
}
