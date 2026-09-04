package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.mapper.StoreMapper;
import com.iwe3.sec.service.IStoreService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * store 表的业务实现类
 */
@Service
public class StoreServiceImpl implements IStoreService {

    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    public PageResult<StoreEntity> list(StoreEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StoreEntity> list = storeMapper.selectList(query);
        PageInfo<StoreEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StoreEntity getById(Long id) {
        return storeMapper.selectById(id);
    }

    @Override
    public boolean add(StoreEntity entity) {
        return storeMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StoreEntity entity) {
        return storeMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return storeMapper.deleteById(id) > 0;
    }
}
