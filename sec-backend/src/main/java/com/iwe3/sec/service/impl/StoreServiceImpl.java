package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.mapper.StoreMapper;
import com.iwe3.sec.service.IStoreService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.cache.CacheHelper;
import com.iwe3.sec.common.cache.CacheKey;

import java.util.List;

/**
 * store 表的业务实现类
 */
@Service
public class StoreServiceImpl implements IStoreService {

    private final StoreMapper storeMapper;
    private final PermissionChecker permissionChecker;
    private final CacheHelper cacheHelper;

    public StoreServiceImpl(StoreMapper storeMapper,
                            PermissionChecker permissionChecker,
                            CacheHelper cacheHelper) {
        this.storeMapper = storeMapper;
        this.permissionChecker = permissionChecker;
        this.cacheHelper = cacheHelper;
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
        // 用缓存查门店信息，缓存没命中再查数据库
        // 权限校验放在加载器里，确保不管从缓存还是数据库拿到的数据都经过权限检查
        return cacheHelper.getOrLoad(CacheKey.PREFIX_STORE + id, CacheKey.TTL_STORE, StoreEntity.class, () -> {
            StoreEntity store = storeMapper.selectById(id);
            if (store != null) {
                permissionChecker.assertInOwnStore(store.getId());
            }
            return store;
        });
    }

    @Override
    public boolean add(StoreEntity entity) {
        boolean result = storeMapper.insert(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_STORE + entity.getId());
        }
        return result;
    }

    @Override
    public boolean update(StoreEntity entity) {
        boolean result = storeMapper.update(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_STORE + entity.getId());
        }
        return result;
    }

    @Override
    public boolean remove(Long id) {
        boolean result = storeMapper.deleteById(id) > 0;
        if (result) {
            cacheHelper.delete(CacheKey.PREFIX_STORE + id);
        }
        return result;
    }
}
