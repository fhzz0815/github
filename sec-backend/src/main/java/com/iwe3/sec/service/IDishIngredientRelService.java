package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishIngredientRelEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_ingredient_rel 表的业务接口
 */
public interface IDishIngredientRelService {

    /** 分页查询列表 */
    PageResult<DishIngredientRelEntity> list(DishIngredientRelEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishIngredientRelEntity getById(Long id);

    /** 新增 */
    boolean add(DishIngredientRelEntity entity);

    /** 修改 */
    boolean update(DishIngredientRelEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
