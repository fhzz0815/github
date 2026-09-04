package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.mapper.StaffLoginLogMapper;
import com.iwe3.sec.service.IStaffLoginLogService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * staff_login_log 表的业务实现类
 */
@Service
public class StaffLoginLogServiceImpl implements IStaffLoginLogService {

    private final StaffLoginLogMapper staffLoginLogMapper;

    public StaffLoginLogServiceImpl(StaffLoginLogMapper staffLoginLogMapper) {
        this.staffLoginLogMapper = staffLoginLogMapper;
    }

    @Override
    public PageResult<StaffLoginLogEntity> list(StaffLoginLogEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StaffLoginLogEntity> list = staffLoginLogMapper.selectList(query);
        PageInfo<StaffLoginLogEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StaffLoginLogEntity getById(Long id) {
        return staffLoginLogMapper.selectById(id);
    }

    @Override
    public boolean add(StaffLoginLogEntity entity) {
        return staffLoginLogMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StaffLoginLogEntity entity) {
        return staffLoginLogMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return staffLoginLogMapper.deleteById(id) > 0;
    }
}
