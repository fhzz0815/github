package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.mapper.SysPermissionMapper;
import com.iwe3.sec.service.ISysPermissionService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.util.List;

/**
 * sys_permission 表的业务实现类
 * 权限管理仅总店长可访问，所有方法均做总店长校验
 */
@Service
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final PermissionChecker permissionChecker;

    public SysPermissionServiceImpl(SysPermissionMapper sysPermissionMapper, PermissionChecker permissionChecker) {
        this.sysPermissionMapper = sysPermissionMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<SysPermissionEntity> list(SysPermissionEntity query, Integer page, Integer size) {
        permissionChecker.assertGeneralManager();
        PageHelper.startPage(page, size);
        List<SysPermissionEntity> list = sysPermissionMapper.selectList(query);
        PageInfo<SysPermissionEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public SysPermissionEntity getById(Long id) {
        permissionChecker.assertGeneralManager();
        return sysPermissionMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(SysPermissionEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysPermissionMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(SysPermissionEntity entity) {
        permissionChecker.assertGeneralManager();
        return sysPermissionMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        permissionChecker.assertGeneralManager();
        return sysPermissionMapper.deleteById(id) > 0;
    }
}
