package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StockCheckDishEntity;
import com.iwe3.sec.mapper.StockCheckDishMapper;
import com.iwe3.sec.service.IStockCheckDishService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * stock_check_dish 表的业务实现类
 */
@Service
public class StockCheckDishServiceImpl implements IStockCheckDishService {

    private final StockCheckDishMapper stockCheckDishMapper;

    public StockCheckDishServiceImpl(StockCheckDishMapper stockCheckDishMapper) {
        this.stockCheckDishMapper = stockCheckDishMapper;
    }

    @Override
    public PageResult<StockCheckDishEntity> list(StockCheckDishEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StockCheckDishEntity> list = stockCheckDishMapper.selectList(query);
        PageInfo<StockCheckDishEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StockCheckDishEntity getById(Long id) {
        return stockCheckDishMapper.selectById(id);
    }

    @Override
    public boolean add(StockCheckDishEntity entity) {
        return stockCheckDishMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StockCheckDishEntity entity) {
        return stockCheckDishMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return stockCheckDishMapper.deleteById(id) > 0;
    }
}
