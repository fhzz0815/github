package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.mapper.DiningTableMapper;
import com.iwe3.sec.service.IDiningTableService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.util.List;

/**
 * dining_table 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店台桌，总店长可看全部
 */
@Service
public class DiningTableServiceImpl implements IDiningTableService {

    private final DiningTableMapper diningTableMapper;
    private final PermissionChecker permissionChecker;

    public DiningTableServiceImpl(DiningTableMapper diningTableMapper, PermissionChecker permissionChecker) {
        this.diningTableMapper = diningTableMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<DiningTableEntity> list(DiningTableEntity query, Integer page, Integer size) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(403, "无门店归属，无法查看台桌");
            }
            query.setStoreId(myStoreId);
        }
        PageHelper.startPage(page, size);
        List<DiningTableEntity> list = diningTableMapper.selectList(query);
        PageInfo<DiningTableEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DiningTableEntity getById(Long id) {
        DiningTableEntity d = diningTableMapper.selectById(id);
        if (d == null) {
            return null;
        }
        assertInOwnStore(d.getStoreId());
        return d;
    }

    @Override
    public boolean add(DiningTableEntity entity) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            entity.setStoreId(permissionChecker.currentStoreId());
        }
        return diningTableMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DiningTableEntity entity) {
        if (entity.getId() != null) {
            DiningTableEntity existing = diningTableMapper.selectById(entity.getId());
            if (existing != null) {
                assertInOwnStore(existing.getStoreId());
            }
        }
        return diningTableMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        DiningTableEntity existing = diningTableMapper.selectById(id);
        if (existing != null) {
            assertInOwnStore(existing.getStoreId());
        }
        return diningTableMapper.deleteById(id) > 0;
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
