package com.iwe3.sec.service;

import com.iwe3.sec.entity.StaffScheduleEntity;
import com.iwe3.sec.common.PageResult;

/**
 * staff_schedule 表的业务接口
 */
public interface IStaffScheduleService {

    /** 分页查询列表 */
    PageResult<StaffScheduleEntity> list(StaffScheduleEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StaffScheduleEntity getById(Long id);

    /** 新增 */
    boolean add(StaffScheduleEntity entity);

    /** 修改 */
    boolean update(StaffScheduleEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
