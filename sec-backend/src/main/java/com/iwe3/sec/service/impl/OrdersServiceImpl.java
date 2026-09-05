package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;

import java.util.List;

/**
 * orders 表的业务实现类
 * 业务数据按门店隔离：店长及以下只能操作本门店订单，总店长可看全部
 */
@Service
public class OrdersServiceImpl implements IOrdersService {

    private final OrdersMapper ordersMapper;
    private final PermissionChecker permissionChecker;

    public OrdersServiceImpl(OrdersMapper ordersMapper, PermissionChecker permissionChecker) {
        this.ordersMapper = ordersMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size) {
        // 店长及以下：强制只看本门店
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            Long myStoreId = permissionChecker.currentStoreId();
            if (myStoreId == null) {
                throw new BusinessException(403, "无门店归属，无法查看订单");
            }
            query.setStoreId(myStoreId);
        }
        PageHelper.startPage(page, size);
        List<OrdersEntity> list = ordersMapper.selectList(query);
        PageInfo<OrdersEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public OrdersEntity getById(Long id) {
        OrdersEntity o = ordersMapper.selectById(id);
        if (o == null) {
            return null;
        }
        assertInOwnStore(o.getStoreId());
        return o;
    }

    @Override
    public boolean add(OrdersEntity entity) {
        Integer level = permissionChecker.currentRoleLevel();
        if (level == null || level < PermissionChecker.LEVEL_GENERAL_MANAGER) {
            entity.setStoreId(permissionChecker.currentStoreId());
        }
        return ordersMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(OrdersEntity entity) {
        if (entity.getId() != null) {
            OrdersEntity existing = ordersMapper.selectById(entity.getId());
            if (existing != null) {
                assertInOwnStore(existing.getStoreId());
            }
        }
        return ordersMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        OrdersEntity existing = ordersMapper.selectById(id);
        if (existing != null) {
            assertInOwnStore(existing.getStoreId());
        }
        return ordersMapper.deleteById(id) > 0;
    }

    /** 校验目标数据是否属于当前用户的门店（总店长跳过） */
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
