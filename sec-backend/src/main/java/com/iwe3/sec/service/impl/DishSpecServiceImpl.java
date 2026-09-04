package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishSpecEntity;
import com.iwe3.sec.mapper.DishSpecMapper;
import com.iwe3.sec.service.IDishSpecService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_spec 表的业务实现类
 */
@Service
public class DishSpecServiceImpl implements IDishSpecService {

    private final DishSpecMapper dishSpecMapper;

    public DishSpecServiceImpl(DishSpecMapper dishSpecMapper) {
        this.dishSpecMapper = dishSpecMapper;
    }

    @Override
    public PageResult<DishSpecEntity> list(DishSpecEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishSpecEntity> list = dishSpecMapper.selectList(query);
        PageInfo<DishSpecEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishSpecEntity getById(Long id) {
        return dishSpecMapper.selectById(id);
    }

    @Override
    public boolean add(DishSpecEntity entity) {
        return dishSpecMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishSpecEntity entity) {
        return dishSpecMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return dishSpecMapper.deleteById(id) > 0;
    }
}
