package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StorePaymentSettingEntity;
import com.iwe3.sec.mapper.StorePaymentSettingMapper;
import com.iwe3.sec.service.IStorePaymentSettingService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * store_payment_setting 表的业务实现类
 */
@Service
public class StorePaymentSettingServiceImpl implements IStorePaymentSettingService {

    private final StorePaymentSettingMapper storePaymentSettingMapper;

    public StorePaymentSettingServiceImpl(StorePaymentSettingMapper storePaymentSettingMapper) {
        this.storePaymentSettingMapper = storePaymentSettingMapper;
    }

    @Override
    public PageResult<StorePaymentSettingEntity> list(StorePaymentSettingEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StorePaymentSettingEntity> list = storePaymentSettingMapper.selectList(query);
        PageInfo<StorePaymentSettingEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public StorePaymentSettingEntity getById(Long id) {
        return storePaymentSettingMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(StorePaymentSettingEntity entity) {
        return storePaymentSettingMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(StorePaymentSettingEntity entity) {
        return storePaymentSettingMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return storePaymentSettingMapper.deleteById(id) > 0;
    }
}
