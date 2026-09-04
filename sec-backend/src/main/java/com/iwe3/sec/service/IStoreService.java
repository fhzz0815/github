package com.iwe3.sec.service;

import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.common.PageResult;

/**
 * store 表的业务接口
 */
public interface IStoreService {

    /** 分页查询列表 */
    PageResult<StoreEntity> list(StoreEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StoreEntity getById(Long id);

    /** 新增 */
    boolean add(StoreEntity entity);

    /** 修改 */
    boolean update(StoreEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
