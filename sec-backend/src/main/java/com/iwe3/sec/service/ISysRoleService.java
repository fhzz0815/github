package com.iwe3.sec.service;

import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.common.PageResult;

/**
 * sys_role 表的业务接口
 */
public interface ISysRoleService {

    /** 分页查询列表 */
    PageResult<SysRoleEntity> list(SysRoleEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    SysRoleEntity getById(Long id);

    /** 新增 */
    boolean add(SysRoleEntity entity);

    /** 修改 */
    boolean update(SysRoleEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
