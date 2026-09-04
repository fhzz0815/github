package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.IngredientStockEntity;
import com.iwe3.sec.mapper.IngredientStockMapper;
import com.iwe3.sec.service.IIngredientStockService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * ingredient_stock 表的业务实现类
 */
@Service
public class IngredientStockServiceImpl implements IIngredientStockService {

    private final IngredientStockMapper ingredientStockMapper;

    public IngredientStockServiceImpl(IngredientStockMapper ingredientStockMapper) {
        this.ingredientStockMapper = ingredientStockMapper;
    }

    @Override
    public PageResult<IngredientStockEntity> list(IngredientStockEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<IngredientStockEntity> list = ingredientStockMapper.selectList(query);
        PageInfo<IngredientStockEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public IngredientStockEntity getById(Long id) {
        return ingredientStockMapper.selectById(id);
    }

    @Override
    public boolean add(IngredientStockEntity entity) {
        return ingredientStockMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(IngredientStockEntity entity) {
        return ingredientStockMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return ingredientStockMapper.deleteById(id) > 0;
    }
}
