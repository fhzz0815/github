package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.common.PageResult;

/**
 * orders 表的业务接口
 */
public interface IOrdersService {

    /** 分页查询列表 */
    PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    OrdersEntity getById(Long id);

    /** 新增 */
    boolean add(OrdersEntity entity);

    /** 修改 */
    boolean update(OrdersEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
