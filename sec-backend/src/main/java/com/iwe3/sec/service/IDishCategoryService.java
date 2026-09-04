package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_category 表的业务接口
 */
public interface IDishCategoryService {

    /** 分页查询列表 */
    PageResult<DishCategoryEntity> list(DishCategoryEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishCategoryEntity getById(Long id);

    /** 新增 */
    boolean add(DishCategoryEntity entity);

    /** 修改 */
    boolean update(DishCategoryEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
