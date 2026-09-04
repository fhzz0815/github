package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.mapper.StockCheckIngredientMapper;
import com.iwe3.sec.service.IStockCheckIngredientService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * stock_check_ingredient 表的业务实现类
 */
@Service
public class StockCheckIngredientServiceImpl implements IStockCheckIngredientService {

    private final StockCheckIngredientMapper stockCheckIngredientMapper;

    public StockCheckIngredientServiceImpl(StockCheckIngredientMapper stockCheckIngredientMapper) {
        this.stockCheckIngredientMapper = stockCheckIngredientMapper;
    }

    @Override
    public PageResult<StockCheckIngredientEntity> list(StockCheckIngredientEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StockCheckIngredientEntity> list = stockCheckIngredientMapper.selectList(query);
        PageInfo<StockCheckIngredientEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StockCheckIngredientEntity getById(Long id) {
        return stockCheckIngredientMapper.selectById(id);
    }

    @Override
    public boolean add(StockCheckIngredientEntity entity) {
        return stockCheckIngredientMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StockCheckIngredientEntity entity) {
        return stockCheckIngredientMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return stockCheckIngredientMapper.deleteById(id) > 0;
    }
}
