package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysRolePermissionEntity;
import com.iwe3.sec.mapper.SysRolePermissionMapper;
import com.iwe3.sec.service.ISysRolePermissionService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * sys_role_permission 表的业务实现类
 */
@Service
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    public SysRolePermissionServiceImpl(SysRolePermissionMapper sysRolePermissionMapper) {
        this.sysRolePermissionMapper = sysRolePermissionMapper;
    }

    @Override
    public PageResult<SysRolePermissionEntity> list(SysRolePermissionEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<SysRolePermissionEntity> list = sysRolePermissionMapper.selectList(query);
        PageInfo<SysRolePermissionEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysRolePermissionEntity getById(Long id) {
        return sysRolePermissionMapper.selectById(id);
    }

    @Override
    public boolean add(SysRolePermissionEntity entity) {
        return sysRolePermissionMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysRolePermissionEntity entity) {
        return sysRolePermissionMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return sysRolePermissionMapper.deleteById(id) > 0;
    }
}
