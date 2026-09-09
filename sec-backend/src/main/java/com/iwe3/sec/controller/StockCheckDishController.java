package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStockCheckDishService;
import com.iwe3.sec.entity.StockCheckDishEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * stock_check_dish 表的表现层控制器
 */
@Tag(name = "菜品盘点", description = "菜品盘点的增删改查")
@RestController
@RequestMapping("/api/v1/stockCheckDishes")
public class StockCheckDishController {

    private final IStockCheckDishService stockCheckDishService;

    public StockCheckDishController(IStockCheckDishService stockCheckDishService) {
        this.stockCheckDishService = stockCheckDishService;
    }

    @Operation(summary = "分页查询菜品盘点管理列表")
    @GetMapping
    public Result<PageResult<StockCheckDishEntity>> list(StockCheckDishEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(stockCheckDishService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品盘点管理详情")
    @GetMapping("/{id}")
    public Result<StockCheckDishEntity> getById(@PathVariable Long id) {
        return Result.success(stockCheckDishService.getById(id));
    }

    @Operation(summary = "新增菜品盘点管理")
    @PostMapping
    public Result<Void> add(@RequestBody StockCheckDishEntity entity) {
        stockCheckDishService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品盘点管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StockCheckDishEntity entity) {
        entity.setId(id);
        stockCheckDishService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品盘点管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        stockCheckDishService.remove(id);
        return Result.success();
    }
}
