package com.iwe3.sec.service;

import com.iwe3.sec.entity.IngredientCategoryEntity;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient_category 表的业务接口
 */
public interface IIngredientCategoryService {

    /** 分页查询列表 */
    PageResult<IngredientCategoryEntity> list(IngredientCategoryEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    IngredientCategoryEntity getById(Long id);

    /** 新增 */
    boolean add(IngredientCategoryEntity entity);

    /** 修改 */
    boolean update(IngredientCategoryEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
