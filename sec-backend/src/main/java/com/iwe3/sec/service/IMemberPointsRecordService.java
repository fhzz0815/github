package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberPointsRecordEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_points_record 表的业务接口
 */
public interface IMemberPointsRecordService {

    /** 分页查询列表 */
    PageResult<MemberPointsRecordEntity> list(MemberPointsRecordEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberPointsRecordEntity getById(Long id);

    /** 新增 */
    boolean add(MemberPointsRecordEntity entity);

    /** 修改 */
    boolean update(MemberPointsRecordEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
