package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberBankCardEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_bank_card 表的业务接口
 */
public interface IMemberBankCardService {

    /** 分页查询列表 */
    PageResult<MemberBankCardEntity> list(MemberBankCardEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberBankCardEntity getById(Long id);

    /** 新增 */
    boolean add(MemberBankCardEntity entity);

    /** 修改 */
    boolean update(MemberBankCardEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
