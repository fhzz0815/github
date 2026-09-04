package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StaffScheduleEntity;
import com.iwe3.sec.mapper.StaffScheduleMapper;
import com.iwe3.sec.service.IStaffScheduleService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * staff_schedule 表的业务实现类
 */
@Service
public class StaffScheduleServiceImpl implements IStaffScheduleService {

    private final StaffScheduleMapper staffScheduleMapper;

    public StaffScheduleServiceImpl(StaffScheduleMapper staffScheduleMapper) {
        this.staffScheduleMapper = staffScheduleMapper;
    }

    @Override
    public PageResult<StaffScheduleEntity> list(StaffScheduleEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StaffScheduleEntity> list = staffScheduleMapper.selectList(query);
        PageInfo<StaffScheduleEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StaffScheduleEntity getById(Long id) {
        return staffScheduleMapper.selectById(id);
    }

    @Override
    public boolean add(StaffScheduleEntity entity) {
        return staffScheduleMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StaffScheduleEntity entity) {
        return staffScheduleMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return staffScheduleMapper.deleteById(id) > 0;
    }
}
