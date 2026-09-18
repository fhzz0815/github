package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import com.iwe3.sec.service.ISysUserService;
import com.iwe3.sec.common.LoginUser;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.UserDataScope;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.common.ErrorCode;
import cn.hutool.crypto.SecureUtil;

import java.util.List;
import java.util.Objects;

/**
 * sys_user 表的业务实现类
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final PermissionChecker permissionChecker;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, JwtUtil jwtUtil, PermissionChecker permissionChecker) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<SysUserEntity> list(SysUserEntity query, Integer page, Integer size) {
        // 按当前登录人的角色等级构造数据范围
        UserDataScope scope = permissionChecker.buildUserDataScope();
        PageHelper.startPage(page, size);
        List<SysUserEntity> list = sysUserMapper.selectListByDataScope(query, scope);
        // 列表脱敏：密码统一清空；非管理者对部分行做基本字段脱敏
        list.forEach(u -> {
            u.setPassword(null);
            if (permissionChecker.shouldMaskTarget(u)) {
                permissionChecker.maskBasicInfo(u);
            }
        });
        PageInfo<SysUserEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysUserEntity getById(Long id) {
        SysUserEntity entity = sysUserMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(1001, "员工不存在");
        }
        // 权限校验：是否可查看此员工详情
        permissionChecker.assertCanViewUserDetail(entity);
        entity.setPassword(null);
        // 脱敏：非管理者只看基本字段
        if (permissionChecker.shouldMaskTarget(entity)) {
            permissionChecker.maskBasicInfo(entity);
        }
        return entity;
    }

    @Override
    public boolean add(SysUserEntity entity) {
        // 用传入的 storeId+roleId 构造目标对象做权限判断
        SysUserEntity target = SysUserEntity.builder()
                .storeId(entity.getStoreId())
                .roleId(entity.getRoleId())
                .build();
        permissionChecker.assertCanManageUser(target);
        // 店长及以下新增员工：强制 storeId 为本人门店
        LoginUser me = permissionChecker.current();
        if (me.getRoleLevel() == null || me.getRoleLevel() < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            entity.setStoreId(me.getStoreId());
        }
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
        SysUserEntity existing = sysUserMapper.selectById(entity.getId());
        if (existing == null) {
            throw new BusinessException(1001, "员工不存在");
        }
        // 权限校验：能否管理此员工
        permissionChecker.assertCanManageUser(existing);
        // 防止店长把自己手下的低级员工提权到同级或更高
        if (entity.getRoleId() != null && !entity.getRoleId().equals(existing.getRoleId())) {
            SysUserEntity afterUpdate = SysUserEntity.builder()
                    .storeId(existing.getStoreId())
                    .roleId(entity.getRoleId())
                    .build();
            permissionChecker.assertCanManageUser(afterUpdate);
        }
        // 修改资料时如果带了新密码（非空），同样做MD5加密后再存，避免明文落库导致无法登录
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            entity.setPassword(SecureUtil.md5(entity.getPassword()));
        }
        return sysUserMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        // 禁止删除自己
        if (Objects.equals(permissionChecker.currentUserId(), id)) {
            throw new BusinessException(403, "不能删除自己");
        }
        SysUserEntity existing = sysUserMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(1001, "员工不存在");
        }
        // 权限校验：能否管理此员工
        permissionChecker.assertCanManageUser(existing);
        return sysUserMapper.deleteById(id) > 0;
    }

    @Override
    public String login(String username, String password) {
        SysUserEntity user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "账号不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "账号已被禁用");
        }
        // 数据库密码用MD5存储，把传入的明文密码做MD5后比对
        if (!SecureUtil.md5(password).equalsIgnoreCase(user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "密码错误");
        }
        return jwtUtil.generateAccessToken(user.getId(), user.getUsername());
    }
}
