package com.iwe3.sec.service;

import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.common.PageResult;

/**
 * payment_record 表的业务接口
 */
public interface IPaymentRecordService {

    /** 分页查询列表 */
    PageResult<PaymentRecordEntity> list(PaymentRecordEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    PaymentRecordEntity getById(Long id);

    /** 新增 */
    boolean add(PaymentRecordEntity entity);

    /** 修改 */
    boolean update(PaymentRecordEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
