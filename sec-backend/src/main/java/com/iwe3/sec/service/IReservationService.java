package com.iwe3.sec.service;

import com.iwe3.sec.entity.ReservationEntity;
import com.iwe3.sec.common.PageResult;

/**
 * reservation 表的业务接口
 */
public interface IReservationService {

    /** 分页查询列表 */
    PageResult<ReservationEntity> list(ReservationEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    ReservationEntity getById(Long id);

    /** 新增 */
    boolean add(ReservationEntity entity);

    /** 修改 */
    boolean update(ReservationEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
