package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.common.PageResult;

/**
 * order_detail 表的业务接口
 */
public interface IOrderDetailService {

    /** 分页查询列表 */
    PageResult<OrderDetailEntity> list(OrderDetailEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    OrderDetailEntity getById(Long id);

    /** 新增 */
    boolean add(OrderDetailEntity entity);

    /** 修改 */
    boolean update(OrderDetailEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
