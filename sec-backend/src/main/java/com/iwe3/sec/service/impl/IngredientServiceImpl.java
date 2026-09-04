package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.IngredientEntity;
import com.iwe3.sec.mapper.IngredientMapper;
import com.iwe3.sec.service.IIngredientService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * ingredient 表的业务实现类
 */
@Service
public class IngredientServiceImpl implements IIngredientService {

    private final IngredientMapper ingredientMapper;

    public IngredientServiceImpl(IngredientMapper ingredientMapper) {
        this.ingredientMapper = ingredientMapper;
    }

    @Override
    public PageResult<IngredientEntity> list(IngredientEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<IngredientEntity> list = ingredientMapper.selectList(query);
        PageInfo<IngredientEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public IngredientEntity getById(Long id) {
        return ingredientMapper.selectById(id);
    }

    @Override
    public boolean add(IngredientEntity entity) {
        return ingredientMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(IngredientEntity entity) {
        return ingredientMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return ingredientMapper.deleteById(id) > 0;
    }
}
