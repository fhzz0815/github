package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member 表的业务接口
 */
public interface IMemberService {

    /** 分页查询列表 */
    PageResult<MemberEntity> list(MemberEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberEntity getById(Long id);

    /** 新增 */
    boolean add(MemberEntity entity);

    /** 修改 */
    boolean update(MemberEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
