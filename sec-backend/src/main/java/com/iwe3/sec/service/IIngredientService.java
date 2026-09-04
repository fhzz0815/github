package com.iwe3.sec.service;

import com.iwe3.sec.entity.IngredientEntity;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient 表的业务接口
 */
public interface IIngredientService {

    /** 分页查询列表 */
    PageResult<IngredientEntity> list(IngredientEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    IngredientEntity getById(Long id);

    /** 新增 */
    boolean add(IngredientEntity entity);

    /** 修改 */
    boolean update(IngredientEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
