package com.iwe3.sec.service;

import com.iwe3.sec.entity.SysUserEntity;

/**
 * 认证业务接口
 */
public interface IAuthService {

    /** 登录 */
    String login(String username, String password);

    /** 根据用户名查询用户 */
    SysUserEntity getByUsername(String username);
}
