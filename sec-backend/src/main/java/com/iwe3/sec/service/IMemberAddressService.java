package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberAddressEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_address 表的业务接口
 */
public interface IMemberAddressService {

    /** 分页查询列表 */
    PageResult<MemberAddressEntity> list(MemberAddressEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberAddressEntity getById(Long id);

    /** 新增 */
    boolean add(MemberAddressEntity entity);

    /** 修改 */
    boolean update(MemberAddressEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
