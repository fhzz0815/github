package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishReviewEntity;
import com.iwe3.sec.mapper.DishReviewMapper;
import com.iwe3.sec.service.IDishReviewService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dish_review 表的业务实现类
 */
@Service
public class DishReviewServiceImpl implements IDishReviewService {

    private final DishReviewMapper dishReviewMapper;

    public DishReviewServiceImpl(DishReviewMapper dishReviewMapper) {
        this.dishReviewMapper = dishReviewMapper;
    }

    @Override
    public PageResult<DishReviewEntity> list(DishReviewEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DishReviewEntity> list = dishReviewMapper.selectList(query);
        PageInfo<DishReviewEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishReviewEntity getById(Long id) {
        return dishReviewMapper.selectById(id);
    }

    @Override
    public boolean add(DishReviewEntity entity) {
        return dishReviewMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishReviewEntity entity) {
        return dishReviewMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return dishReviewMapper.deleteById(id) > 0;
    }
}
