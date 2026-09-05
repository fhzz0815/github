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

import java.util.List;

/**
 * dish 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店菜品，总店长可看全部
 */
@Service
public class DishServiceImpl implements IDishService {

    private final DishMapper dishMapper;
    private final PermissionChecker permissionChecker;

    public DishServiceImpl(DishMapper dishMapper, PermissionChecker permissionChecker) {
        this.dishMapper = dishMapper;
        this.permissionChecker = permissionChecker;
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
        DishEntity d = dishMapper.selectById(id);
        if (d == null) {
            return null;
        }
        assertInOwnStore(d.getStoreId());
        return d;
    }

    @Override
    public boolean add(DishEntity entity) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            entity.setStoreId(permissionChecker.currentStoreId());
        }
        return dishMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DishEntity entity) {
        if (entity.getId() != null) {
            DishEntity existing = dishMapper.selectById(entity.getId());
            if (existing != null) {
                assertInOwnStore(existing.getStoreId());
            }
        }
        return dishMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        DishEntity existing = dishMapper.selectById(id);
        if (existing != null) {
            assertInOwnStore(existing.getStoreId());
        }
        return dishMapper.deleteById(id) > 0;
    }

    private void assertInOwnStore(Long targetStoreId) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level != null && level >= PermissionChecker.LEVEL_GENERAL_MANAGER) {
            return;
        }
        Long myStoreId = permissionChecker.currentStoreId();
        if (myStoreId == null || !myStoreId.equals(targetStoreId)) {
            throw new BusinessException(403, "无权限，只能操作本门店数据");
        }
    }
}
