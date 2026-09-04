package com.iwe3.sec.service;

import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.common.PageResult;

/**
 * sys_user 表的业务接口
 */
public interface ISysUserService {

    /** 分页查询列表 */
    PageResult<SysUserEntity> list(SysUserEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    SysUserEntity getById(Long id);

    /** 新增 */
    boolean add(SysUserEntity entity);

    /** 修改 */
    boolean update(SysUserEntity entity);

    /** 删除 */
    boolean remove(Long id);

    /** 登录 */
    String login(String username, String password);
}
