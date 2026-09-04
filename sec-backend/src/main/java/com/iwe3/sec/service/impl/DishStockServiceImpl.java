package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishStockEntity;
import com.iwe3.sec.mapper.DishStockMapper;
import com.iwe3.sec.service.IDishStockService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_stock 表的业务实现类
 */
@Service
public class DishStockServiceImpl implements IDishStockService {

    private final DishStockMapper dishStockMapper;

    public DishStockServiceImpl(DishStockMapper dishStockMapper) {
        this.dishStockMapper = dishStockMapper;
    }

    @Override
    public PageResult<DishStockEntity> list(DishStockEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishStockEntity> list = dishStockMapper.selectList(query);
        PageInfo<DishStockEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishStockEntity getById(Long id) {
        return dishStockMapper.selectById(id);
    }

    @Override
    public boolean add(DishStockEntity entity) {
        return dishStockMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishStockEntity entity) {
        return dishStockMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return dishStockMapper.deleteById(id) > 0;
    }
}
