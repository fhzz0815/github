package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.entity.IngredientStockEntity;
import com.iwe3.sec.mapper.StockCheckIngredientMapper;
import com.iwe3.sec.mapper.IngredientStockMapper;
import com.iwe3.sec.service.IStockCheckIngredientService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * stock_check_ingredient 表的业务实现类
 */
@Service
public class StockCheckIngredientServiceImpl implements IStockCheckIngredientService {

    private static final Logger log = LoggerFactory.getLogger(StockCheckIngredientServiceImpl.class);

    private final StockCheckIngredientMapper stockCheckIngredientMapper;
    private final IngredientStockMapper ingredientStockMapper;
    private final PermissionChecker permissionChecker;

    public StockCheckIngredientServiceImpl(StockCheckIngredientMapper stockCheckIngredientMapper,
                                           IngredientStockMapper ingredientStockMapper,
                                           PermissionChecker permissionChecker) {
        this.stockCheckIngredientMapper = stockCheckIngredientMapper;
        this.ingredientStockMapper = ingredientStockMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<StockCheckIngredientEntity> list(StockCheckIngredientEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<StockCheckIngredientEntity> list = stockCheckIngredientMapper.selectList(query);
        PageInfo<StockCheckIngredientEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public StockCheckIngredientEntity getById(Long id) {
        return stockCheckIngredientMapper.selectById(id);
    }

    @Override
    public boolean add(StockCheckIngredientEntity entity) {
        return stockCheckIngredientMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(StockCheckIngredientEntity entity) {
        return stockCheckIngredientMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return stockCheckIngredientMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, boolean approved, String auditRemark) {
        // 1. 校验权限：只有总店长可以审核
        permissionChecker.assertGeneralManager();

        // 2. 查询盘点单详情
        StockCheckIngredientEntity check = stockCheckIngredientMapper.selectById(id);
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

        int affected = stockCheckIngredientMapper.updateStatus(id, targetStatus, auditorId, now, auditRemark);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "审核失败，请稍后重试");
        }

        // 4. 审核通过时，同步更新原料库存
        if (approved) {
            IngredientStockEntity ingredientStock = ingredientStockMapper.selectByStoreAndIngredient(
                    check.getStoreId(), check.getIngredientId());
            if (ingredientStock != null) {
                ingredientStock.setStockQuantity(check.getStockAfter());
                ingredientStockMapper.update(ingredientStock);
                log.info("盘点审核通过，原料库存已同步：storeId={}, ingredientId={}, stockAfter={}",
                        check.getStoreId(), check.getIngredientId(), check.getStockAfter());
            } else {
                IngredientStockEntity newStock = IngredientStockEntity.builder()
                        .storeId(check.getStoreId())
                        .ingredientId(check.getIngredientId())
                        .stockQuantity(check.getStockAfter())
                        .build();
                ingredientStockMapper.insert(newStock);
                log.info("盘点审核通过，原料库存记录已新增：storeId={}, ingredientId={}, stockAfter={}",
                        check.getStoreId(), check.getIngredientId(), check.getStockAfter());
            }
        }

        log.info("原料盘点审核完成：id={}, approved={}, auditorId={}", id, approved, auditorId);
    }
}
