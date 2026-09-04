package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishTasteEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_taste 表的业务接口
 */
public interface IDishTasteService {

    /** 分页查询列表 */
    PageResult<DishTasteEntity> list(DishTasteEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishTasteEntity getById(Long id);

    /** 新增 */
    boolean add(DishTasteEntity entity);

    /** 修改 */
    boolean update(DishTasteEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
