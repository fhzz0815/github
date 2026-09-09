package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.mapper.SysRoleMapper;
import com.iwe3.sec.service.ISysRoleService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.util.List;

/**
 * sys_role 表的业务实现类
 * 角色管理仅总店长可访问，所有方法均做总店长校验
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final PermissionChecker permissionChecker;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper, PermissionChecker permissionChecker) {
        this.sysRoleMapper = sysRoleMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<SysRoleEntity> list(SysRoleEntity query, Integer page, Integer size) {
        permissionChecker.assertGeneralManager();
        PageHelper.startPage(page, size);
        List<SysRoleEntity> list = sysRoleMapper.selectList(query);
        PageInfo<SysRoleEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public SysRoleEntity getById(Long id) {
        permissionChecker.assertGeneralManager();
        return sysRoleMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(SysRoleEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysRoleMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(SysRoleEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysRoleMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        permissionChecker.assertGeneralManager();
        return sysRoleMapper.deleteById(id) > 0;
    }
}
