package com.iwe3.sec.service;

import com.iwe3.sec.entity.TableTypeEntity;
import com.iwe3.sec.common.PageResult;

/**
 * table_type 表的业务接口
 */
public interface ITableTypeService {

    /** 分页查询列表 */
    PageResult<TableTypeEntity> list(TableTypeEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    TableTypeEntity getById(Long id);

    /** 新增 */
    boolean add(TableTypeEntity entity);

    /** 修改 */
    boolean update(TableTypeEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
