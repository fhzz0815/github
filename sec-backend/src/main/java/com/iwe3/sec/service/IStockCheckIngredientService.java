package com.iwe3.sec.service;

import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.common.PageResult;

/**
 * stock_check_ingredient 表的业务接口
 */
public interface IStockCheckIngredientService {

    /** 分页查询列表 */
    PageResult<StockCheckIngredientEntity> list(StockCheckIngredientEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StockCheckIngredientEntity getById(Long id);

    /** 新增 */
    boolean add(StockCheckIngredientEntity entity);

    /** 修改 */
    boolean update(StockCheckIngredientEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
