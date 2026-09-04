package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.service.IOrderDetailService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * order_detail 表的业务实现类
 */
@Service
public class OrderDetailServiceImpl implements IOrderDetailService {

    private final OrderDetailMapper orderDetailMapper;

    public OrderDetailServiceImpl(OrderDetailMapper orderDetailMapper) {
        this.orderDetailMapper = orderDetailMapper;
    }

    @Override
    public PageResult<OrderDetailEntity> list(OrderDetailEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<OrderDetailEntity> list = orderDetailMapper.selectList(query);
        PageInfo<OrderDetailEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public OrderDetailEntity getById(Long id) {
        return orderDetailMapper.selectById(id);
    }

    @Override
    public boolean add(OrderDetailEntity entity) {
        return orderDetailMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrderDetailEntity entity) {
        return orderDetailMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return orderDetailMapper.deleteById(id) > 0;
    }
}
