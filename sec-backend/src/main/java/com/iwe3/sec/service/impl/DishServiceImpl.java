package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.mapper.DishMapper;
import com.iwe3.sec.service.IDishService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.cache.CacheHelper;
import com.iwe3.sec.common.cache.CacheKey;

import java.util.List;

/**
 * dish 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店菜品，总店长可看全部
 */
@Service
public class DishServiceImpl implements IDishService {

    private final DishMapper dishMapper;
    private final PermissionChecker permissionChecker;
    private final CacheHelper cacheHelper;

    public DishServiceImpl(DishMapper dishMapper,
                           PermissionChecker permissionChecker,
                           CacheHelper cacheHelper) {
        this.dishMapper = dishMapper;
        this.permissionChecker = permissionChecker;
        this.cacheHelper = cacheHelper;
    }

    @Override
    public PageResult<DishEntity> list(DishEntity query, Integer page, Integer size) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(403, "无门店归属，无法查看菜品");
            }
            query.setStoreId(myStoreId);
        }
        PageHelper.startPage(page, size);
        List<DishEntity> list = dishMapper.selectList(query);
        PageInfo<DishEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DishEntity getById(Long id) {
        // 用缓存查菜品，缓存没命中再查数据库
        // 权限校验放在加载器里，确保不管从缓存还是数据库拿到的数据都经过权限检查
        return cacheHelper.getOrLoad(CacheKey.PREFIX_DISH + id, CacheKey.TTL_DISH, DishEntity.class, () -> {
            DishEntity d = dishMapper.selectById(id);
            if (d != null) {
                permissionChecker.assertInOwnStore(d.getStoreId());
            }
            return d;
        });
    }

    @Override
    public boolean add(DishEntity entity) {
        permissionChecker.setStoreIdIfNeeded(entity::setStoreId);
        boolean result = dishMapper.insert(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_DISH + entity.getId());
        }
        return result;
    }

    @Override
    public boolean update(DishEntity entity) {
        if (entity.getId() != null) {
            DishEntity existing = dishMapper.selectById(entity.getId());
            if (existing != null) {
                permissionChecker.assertInOwnStore(existing.getStoreId());
            }
        }
        boolean result = dishMapper.update(entity) > 0;
        if (result && entity.getId() != null) {
            cacheHelper.delete(CacheKey.PREFIX_DISH + entity.getId());
        }
        return result;
    }

    @Override
    public boolean remove(Long id) {
        DishEntity existing = dishMapper.selectById(id);
        if (existing != null) {
            permissionChecker.assertInOwnStore(existing.getStoreId());
        }
        boolean result = dishMapper.deleteById(id) > 0;
        if (result) {
            cacheHelper.delete(CacheKey.PREFIX_DISH + id);
        }
        return result;
    }
}
