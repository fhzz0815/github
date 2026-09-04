package com.iwe3.sec.service;

import com.iwe3.sec.entity.IngredientStockEntity;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient_stock 表的业务接口
 */
public interface IIngredientStockService {

    /** 分页查询列表 */
    PageResult<IngredientStockEntity> list(IngredientStockEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    IngredientStockEntity getById(Long id);

    /** 新增 */
    boolean add(IngredientStockEntity entity);

    /** 修改 */
    boolean update(IngredientStockEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
