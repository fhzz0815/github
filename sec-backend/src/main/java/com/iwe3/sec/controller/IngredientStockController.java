package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IIngredientStockService;
import com.iwe3.sec.entity.IngredientStockEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient_stock 表的表现层控制器
 */
@Tag(name = "原料库存", description = "原料库存的增删改查")
@RestController
@RequestMapping("/api/v1/ingredientStocks")
public class IngredientStockController {

    private final IIngredientStockService ingredientStockService;

    public IngredientStockController(IIngredientStockService ingredientStockService) {
        this.ingredientStockService = ingredientStockService;
    }

    @Operation(summary = "分页查询食材库存管理列表")
    @GetMapping
    public Result<PageResult<IngredientStockEntity>> list(IngredientStockEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ingredientStockService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询食材库存管理详情")
    @GetMapping("/{id}")
    public Result<IngredientStockEntity> getById(@PathVariable Long id) {
        return Result.success(ingredientStockService.getById(id));
    }

    @Operation(summary = "新增食材库存管理")
    @PostMapping
    public Result<Void> add(@RequestBody IngredientStockEntity entity) {
        ingredientStockService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改食材库存管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody IngredientStockEntity entity) {
        entity.setId(id);
        ingredientStockService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除食材库存管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ingredientStockService.remove(id);
        return Result.success();
    }
}
