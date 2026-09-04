package com.iwe3.sec.service;

import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.common.PageResult;

/**
 * refund 表的业务接口
 */
public interface IRefundService {

    /** 分页查询列表 */
    PageResult<RefundEntity> list(RefundEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    RefundEntity getById(Long id);

    /** 新增 */
    boolean add(RefundEntity entity);

    /** 修改 */
    boolean update(RefundEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
