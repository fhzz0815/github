package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishIngredientRelEntity;
import com.iwe3.sec.mapper.DishIngredientRelMapper;
import com.iwe3.sec.service.IDishIngredientRelService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_ingredient_rel 表的业务实现类
 */
@Service
public class DishIngredientRelServiceImpl implements IDishIngredientRelService {

    private final DishIngredientRelMapper dishIngredientRelMapper;

    public DishIngredientRelServiceImpl(DishIngredientRelMapper dishIngredientRelMapper) {
        this.dishIngredientRelMapper = dishIngredientRelMapper;
    }

    @Override
    public PageResult<DishIngredientRelEntity> list(DishIngredientRelEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishIngredientRelEntity> list = dishIngredientRelMapper.selectList(query);
        PageInfo<DishIngredientRelEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishIngredientRelEntity getById(Long id) {
        return dishIngredientRelMapper.selectById(id);
    }

    @Override
    public boolean add(DishIngredientRelEntity entity) {
        return dishIngredientRelMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishIngredientRelEntity entity) {
        return dishIngredientRelMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return dishIngredientRelMapper.deleteById(id) > 0;
    }
}
