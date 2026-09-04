package com.iwe3.sec.service;

import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.common.PageResult;

/**
 * coupon 表的业务接口
 */
public interface ICouponService {

    /** 分页查询列表 */
    PageResult<CouponEntity> list(CouponEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    CouponEntity getById(Long id);

    /** 新增 */
    boolean add(CouponEntity entity);

    /** 修改 */
    boolean update(CouponEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
