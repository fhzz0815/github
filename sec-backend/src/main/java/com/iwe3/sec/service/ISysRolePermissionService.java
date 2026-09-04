package com.iwe3.sec.service;

import com.iwe3.sec.entity.SysRolePermissionEntity;
import com.iwe3.sec.common.PageResult;

/**
 * sys_role_permission 表的业务接口
 */
public interface ISysRolePermissionService {

    /** 分页查询列表 */
    PageResult<SysRolePermissionEntity> list(SysRolePermissionEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    SysRolePermissionEntity getById(Long id);

    /** 新增 */
    boolean add(SysRolePermissionEntity entity);

    /** 修改 */
    boolean update(SysRolePermissionEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
