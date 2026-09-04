package com.iwe3.sec.service;

import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.common.PageResult;

/**
 * sys_permission 表的业务接口
 */
public interface ISysPermissionService {

    /** 分页查询列表 */
    PageResult<SysPermissionEntity> list(SysPermissionEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    SysPermissionEntity getById(Long id);

    /** 新增 */
    boolean add(SysPermissionEntity entity);

    /** 修改 */
    boolean update(SysPermissionEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
