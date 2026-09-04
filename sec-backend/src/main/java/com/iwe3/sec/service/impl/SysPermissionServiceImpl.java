package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.mapper.SysPermissionMapper;
import com.iwe3.sec.service.ISysPermissionService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * sys_permission 表的业务实现类
 */
@Service
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    public SysPermissionServiceImpl(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public PageResult<SysPermissionEntity> list(SysPermissionEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<SysPermissionEntity> list = sysPermissionMapper.selectList(query);
        PageInfo<SysPermissionEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysPermissionEntity getById(Long id) {
        return sysPermissionMapper.selectById(id);
    }

    @Override
    public boolean add(SysPermissionEntity entity) {
        return sysPermissionMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysPermissionEntity entity) {
        return sysPermissionMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return sysPermissionMapper.deleteById(id) > 0;
    }
}
