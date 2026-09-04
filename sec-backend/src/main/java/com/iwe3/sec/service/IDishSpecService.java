package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishSpecEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_spec 表的业务接口
 */
public interface IDishSpecService {

    /** 分页查询列表 */
    PageResult<DishSpecEntity> list(DishSpecEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishSpecEntity getById(Long id);

    /** 新增 */
    boolean add(DishSpecEntity entity);

    /** 修改 */
    boolean update(DishSpecEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
