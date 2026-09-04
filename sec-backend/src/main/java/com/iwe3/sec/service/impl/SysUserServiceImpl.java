package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import com.iwe3.sec.service.ISysUserService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import cn.hutool.crypto.SecureUtil;

import java.util.List;

/**
 * sys_user 表的业务实现类
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public PageResult<SysUserEntity> list(SysUserEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<SysUserEntity> list = sysUserMapper.selectList(query);
        PageInfo<SysUserEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysUserEntity getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    public boolean add(SysUserEntity entity) {
        // 新增员工时，把明文密码做MD5加密后再存库；没填密码则给默认密码123456
        String rawPwd = entity.getPassword();
        if (rawPwd == null || rawPwd.isEmpty()) {
            rawPwd = "123456";
        }
        entity.setPassword(SecureUtil.md5(rawPwd));
        return sysUserMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysUserEntity entity) {
        return sysUserMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return sysUserMapper.deleteById(id) > 0;
    }

    @Override
    public String login(String username, String password) {
        SysUserEntity user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(1002, "账号不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(1003, "账号已被禁用");
        }
        // 数据库密码用MD5存储，把传入的明文密码做MD5后比对
        if (!SecureUtil.md5(password).equalsIgnoreCase(user.getPassword())) {
            throw new BusinessException(1004, "密码错误");
        }
        return jwtUtil.generateAccessToken(user.getId(), user.getUsername());
    }
}
