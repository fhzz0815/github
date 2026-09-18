package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.StockCheckDishEntity;
import com.iwe3.sec.entity.DishStockEntity;
import com.iwe3.sec.mapper.StockCheckDishMapper;
import com.iwe3.sec.mapper.DishStockMapper;
import com.iwe3.sec.service.IStockCheckDishService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * stock_check_dish 表的业务实现类
 */
@Service
public class StockCheckDishServiceImpl implements IStockCheckDishService {

    private static final Logger log = LoggerFactory.getLogger(StockCheckDishServiceImpl.class);

    private final StockCheckDishMapper stockCheckDishMapper;
    private final DishStockMapper dishStockMapper;
    private final PermissionChecker permissionChecker;

    public StockCheckDishServiceImpl(StockCheckDishMapper stockCheckDishMapper,
                                     DishStockMapper dishStockMapper,
                                     PermissionChecker permissionChecker) {
        this.stockCheckDishMapper = stockCheckDishMapper;
        this.dishStockMapper = dishStockMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<StockCheckDishEntity> list(StockCheckDishEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StockCheckDishEntity> list = stockCheckDishMapper.selectList(query);
        PageInfo<StockCheckDishEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StockCheckDishEntity getById(Long id) {
        return stockCheckDishMapper.selectById(id);
    }

    @Override
    public boolean add(StockCheckDishEntity entity) {
        return stockCheckDishMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StockCheckDishEntity entity) {
        return stockCheckDishMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return stockCheckDishMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String auditRemark) {
        // 1. 校验权限：只有总店长可以审核
        permissionChecker.assertGeneralManager();

        // 2. 查询盘点单详情
        StockCheckDishEntity check = stockCheckDishMapper.selectById(id);
        if (check == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "盘点单不存在");
        }
        if (check.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "当前状态不是「待审核」，不能操作");
        }

        // 3. 更新状态
        Integer targetStatus = approved ? 2 : 3;
        Date now = new Date();
        Long auditorId = permissionChecker.currentUserId();

        int affected = stockCheckDishMapper.updateStatus(id, targetStatus, auditorId, now, auditRemark);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "审核失败，请稍后重试");
        }

        // 4. 审核通过时，同步更新菜品库存
        if (approved) {
            // 查找门店下该菜品的库存记录
            DishStockEntity dishStock = dishStockMapper.selectByStoreAndDish(check.getStoreId(), check.getDishId());
            if (dishStock != null) {
                // 用盘点后的数量覆盖实际库存
                dishStock.setStockQuantity(check.getStockAfter());
                dishStockMapper.update(dishStock);
                log.info("盘点审核通过，菜品库存已同步：storeId={}, dishId={}, stockAfter={}",
                        check.getStoreId(), check.getDishId(), check.getStockAfter());
            } else {
                // 没有库存记录则新增一条
                DishStockEntity newStock = DishStockEntity.builder()
                        .storeId(check.getStoreId())
                        .dishId(check.getDishId())
                        .stockQuantity(check.getStockAfter())
                        .build();
                dishStockMapper.insert(newStock);
                log.info("盘点审核通过，菜品库存记录已新增：storeId={}, dishId={}, stockAfter={}",
                        check.getStoreId(), check.getDishId(), check.getStockAfter());
            }
        }

        log.info("盘点审核完成：id={}, approved={}, auditorId={}", id, approved, auditorId);
    }
}
