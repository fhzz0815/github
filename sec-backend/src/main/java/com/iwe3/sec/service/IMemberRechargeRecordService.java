package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_recharge_record 表的业务接口
 */
public interface IMemberRechargeRecordService {

    /** 分页查询列表 */
    PageResult<MemberRechargeRecordEntity> list(MemberRechargeRecordEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberRechargeRecordEntity getById(Long id);

    /** 新增 */
    boolean add(MemberRechargeRecordEntity entity);

    /** 修改 */
    boolean update(MemberRechargeRecordEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
