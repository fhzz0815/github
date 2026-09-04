package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IIngredientStockService;
import com.iwe3.sec.entity.IngredientStockEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient_stock 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/ingredientStocks")
public class IngredientStockController {

    private final IIngredientStockService ingredientStockService;

    public IngredientStockController(IIngredientStockService ingredientStockService) {
        this.ingredientStockService = ingredientStockService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<IngredientStockEntity>> list(IngredientStockEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ingredientStockService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<IngredientStockEntity> getById(@PathVariable Long id) {
        return Result.success(ingredientStockService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody IngredientStockEntity entity) {
        ingredientStockService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody IngredientStockEntity entity) {
        entity.setId(id);
        ingredientStockService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ingredientStockService.remove(id);
        return Result.success();
    }
}
