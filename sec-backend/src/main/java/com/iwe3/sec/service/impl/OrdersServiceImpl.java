package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * orders 表的业务实现类
 */
@Service
public class OrdersServiceImpl implements IOrdersService {

    private final OrdersMapper ordersMapper;

    public OrdersServiceImpl(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    @Override
    public PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<OrdersEntity> list = ordersMapper.selectList(query);
        PageInfo<OrdersEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public OrdersEntity getById(Long id) {
        return ordersMapper.selectById(id);
    }

    @Override
    public boolean add(OrdersEntity entity) {
        return ordersMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrdersEntity entity) {
        return ordersMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return ordersMapper.deleteById(id) > 0;
    }
}
