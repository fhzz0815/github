package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import com.iwe3.sec.service.IAuthService;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证业务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    // 员工数据访问接口
    private final SysUserMapper sysUserMapper;
    // JWT工具类，用来生成和解析令牌
    private final JwtUtil jwtUtil;
    // bcrypt加密工具，兼容bcrypt加密的密码
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String login(String username, String password) {
        SysUserEntity user = getByUsername(username);
        if (user == null) {
            throw new BusinessException(1002, "账号不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(1003, "账号已被禁用");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(1005, "账号已被停用");
        }
        // 数据库密码使用MD5存储，这里把前端传的明文密码做MD5后比对
        String md5Password = SecureUtil.md5(password);
        boolean md5Match = md5Password.equalsIgnoreCase(user.getPassword());
        // 同时兼容bcrypt加密的密码
        boolean bcryptMatch = user.getPassword() != null
                && user.getPassword().startsWith("$2")
                && passwordEncoder.matches(password, user.getPassword());
        if (!md5Match && !bcryptMatch) {
            throw new BusinessException(1004, "密码错误");
        }
        return jwtUtil.generateAccessToken(user.getId(), user.getUsername());
    }

    @Override
    public SysUserEntity getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }
}
