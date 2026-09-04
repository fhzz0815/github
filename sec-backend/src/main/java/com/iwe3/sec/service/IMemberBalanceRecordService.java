package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_balance_record 表的业务接口
 */
public interface IMemberBalanceRecordService {

    /** 分页查询列表 */
    PageResult<MemberBalanceRecordEntity> list(MemberBalanceRecordEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberBalanceRecordEntity getById(Long id);

    /** 新增 */
    boolean add(MemberBalanceRecordEntity entity);

    /** 修改 */
    boolean update(MemberBalanceRecordEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
