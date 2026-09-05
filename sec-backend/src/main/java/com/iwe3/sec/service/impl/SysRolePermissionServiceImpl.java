package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysRolePermissionEntity;
import com.iwe3.sec.mapper.SysRolePermissionMapper;
import com.iwe3.sec.service.ISysRolePermissionService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.util.List;

/**
 * sys_role_permission 表的业务实现类
 * 角色权限管理仅总店长可访问，所有方法均做总店长校验
 */
@Service
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final PermissionChecker permissionChecker;

    public SysRolePermissionServiceImpl(SysRolePermissionMapper sysRolePermissionMapper, PermissionChecker permissionChecker) {
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<SysRolePermissionEntity> list(SysRolePermissionEntity query, Integer page, Integer size) {
        permissionChecker.assertGeneralManager();
        PageHelper.startPage(page, size);
        List<SysRolePermissionEntity> list = sysRolePermissionMapper.selectList(query);
        PageInfo<SysRolePermissionEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysRolePermissionEntity getById(Long id) {
        permissionChecker.assertGeneralManager();
        return sysRolePermissionMapper.selectById(id);
    }

    @Override
    public boolean add(SysRolePermissionEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysRolePermissionMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysRolePermissionEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysRolePermissionMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        permissionChecker.assertGeneralManager();
        return sysRolePermissionMapper.deleteById(id) > 0;
    }
}
