package com.iwe3.sec.service;

import com.iwe3.sec.entity.StorePaymentSettingEntity;
import com.iwe3.sec.common.PageResult;

/**
 * store_payment_setting 表的业务接口
 */
public interface IStorePaymentSettingService {

    /** 分页查询列表 */
    PageResult<StorePaymentSettingEntity> list(StorePaymentSettingEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    StorePaymentSettingEntity getById(Long id);

    /** 新增 */
    boolean add(StorePaymentSettingEntity entity);

    /** 修改 */
    boolean update(StorePaymentSettingEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
