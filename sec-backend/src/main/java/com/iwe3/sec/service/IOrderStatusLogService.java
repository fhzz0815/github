package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.common.PageResult;

/**
 * order_status_log 表的业务接口
 */
public interface IOrderStatusLogService {

    /** 分页查询列表 */
    PageResult<OrderStatusLogEntity> list(OrderStatusLogEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    OrderStatusLogEntity getById(Long id);

    /** 新增 */
    boolean add(OrderStatusLogEntity entity);

    /** 修改 */
    boolean update(OrderStatusLogEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
