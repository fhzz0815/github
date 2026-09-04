package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish 表的业务接口
 */
public interface IDishService {

    /** 分页查询列表 */
    PageResult<DishEntity> list(DishEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishEntity getById(Long id);

    /** 新增 */
    boolean add(DishEntity entity);

    /** 修改 */
    boolean update(DishEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
