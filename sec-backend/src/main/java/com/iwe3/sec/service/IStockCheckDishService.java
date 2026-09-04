package com.iwe3.sec.service;

import com.iwe3.sec.entity.StockCheckDishEntity;
import com.iwe3.sec.common.PageResult;

/**
 * stock_check_dish 表的业务接口
 */
public interface IStockCheckDishService {

    /** 分页查询列表 */
    PageResult<StockCheckDishEntity> list(StockCheckDishEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StockCheckDishEntity getById(Long id);

    /** 新增 */
    boolean add(StockCheckDishEntity entity);

    /** 修改 */
    boolean update(StockCheckDishEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
