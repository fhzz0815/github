package com.iwe3.sec.service;

import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dining_table 表的业务接口
 */
public interface IDiningTableService {

    /** 分页查询列表 */
    PageResult<DiningTableEntity> list(DiningTableEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DiningTableEntity getById(Long id);

    /** 新增 */
    boolean add(DiningTableEntity entity);

    /** 修改 */
    boolean update(DiningTableEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
