package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.mapper.OrderStatusLogMapper;
import com.iwe3.sec.service.IOrderStatusLogService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * order_status_log 表的业务实现类
 */
@Service
public class OrderStatusLogServiceImpl implements IOrderStatusLogService {

    private final OrderStatusLogMapper orderStatusLogMapper;

    public OrderStatusLogServiceImpl(OrderStatusLogMapper orderStatusLogMapper) {
        this.orderStatusLogMapper = orderStatusLogMapper;
    }

    @Override
    public PageResult<OrderStatusLogEntity> list(OrderStatusLogEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<OrderStatusLogEntity> list = orderStatusLogMapper.selectList(query);
        PageInfo<OrderStatusLogEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public OrderStatusLogEntity getById(Long id) {
        return orderStatusLogMapper.selectById(id);
    }

    @Override
    public boolean add(OrderStatusLogEntity entity) {
        return orderStatusLogMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrderStatusLogEntity entity) {
        return orderStatusLogMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return orderStatusLogMapper.deleteById(id) > 0;
    }
}
