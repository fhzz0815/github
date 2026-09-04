package com.iwe3.sec.service;

import com.iwe3.sec.entity.MemberCategoryEntity;
import com.iwe3.sec.common.PageResult;

/**
 * member_category 表的业务接口
 */
public interface IMemberCategoryService {

    /** 分页查询列表 */
    PageResult<MemberCategoryEntity> list(MemberCategoryEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    MemberCategoryEntity getById(Long id);

    /** 新增 */
    boolean add(MemberCategoryEntity entity);

    /** 修改 */
    boolean update(MemberCategoryEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
