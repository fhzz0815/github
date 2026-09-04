package com.iwe3.sec.service;

import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.common.PageResult;

/**
 * staff_login_log 表的业务接口
 */
public interface IStaffLoginLogService {

    /** 分页查询列表 */
    PageResult<StaffLoginLogEntity> list(StaffLoginLogEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StaffLoginLogEntity getById(Long id);

    /** 新增 */
    boolean add(StaffLoginLogEntity entity);

    /** 修改 */
    boolean update(StaffLoginLogEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
