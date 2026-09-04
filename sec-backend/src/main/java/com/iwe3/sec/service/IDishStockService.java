package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishStockEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_stock 表的业务接口
 */
public interface IDishStockService {

    /** 分页查询列表 */
    PageResult<DishStockEntity> list(DishStockEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishStockEntity getById(Long id);

    /** 新增 */
    boolean add(DishStockEntity entity);

    /** 修改 */
    boolean update(DishStockEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
