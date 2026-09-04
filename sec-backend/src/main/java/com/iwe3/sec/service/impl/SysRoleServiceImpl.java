package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.mapper.SysRoleMapper;
import com.iwe3.sec.service.ISysRoleService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * sys_role 表的业务实现类
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public PageResult<SysRoleEntity> list(SysRoleEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<SysRoleEntity> list = sysRoleMapper.selectList(query);
        PageInfo<SysRoleEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public SysRoleEntity getById(Long id) {
        return sysRoleMapper.selectById(id);
    }

    @Override
    public boolean add(SysRoleEntity entity) {
        return sysRoleMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysRoleEntity entity) {
        return sysRoleMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return sysRoleMapper.deleteById(id) > 0;
    }
}
