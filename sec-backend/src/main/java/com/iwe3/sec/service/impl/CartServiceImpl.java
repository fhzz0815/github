package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.CartEntity;
import com.iwe3.sec.mapper.CartMapper;
import com.iwe3.sec.service.ICartService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * cart 表的业务实现类
 */
@Service
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;

    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Override
    public PageResult<CartEntity> list(CartEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<CartEntity> list = cartMapper.selectList(query);
        PageInfo<CartEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public CartEntity getById(Long id) {
        return cartMapper.selectById(id);
    }

    @Override
    public boolean add(CartEntity entity) {
        return cartMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(CartEntity entity) {
        return cartMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return cartMapper.deleteById(id) > 0;
    }
}
