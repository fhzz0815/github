package com.iwe3.sec.service;

import com.iwe3.sec.entity.CartEntity;
import com.iwe3.sec.common.PageResult;

/**
 * cart 表的业务接口
 */
public interface ICartService {

    /** 分页查询列表 */
    PageResult<CartEntity> list(CartEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    CartEntity getById(Long id);

    /** 新增 */
    boolean add(CartEntity entity);

    /** 修改 */
    boolean update(CartEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
