package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStockCheckIngredientService;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * stock_check_ingredient 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/stockCheckIngredients")
public class StockCheckIngredientController {

    private final IStockCheckIngredientService stockCheckIngredientService;

    public StockCheckIngredientController(IStockCheckIngredientService stockCheckIngredientService) {
        this.stockCheckIngredientService = stockCheckIngredientService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<StockCheckIngredientEntity>> list(StockCheckIngredientEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(stockCheckIngredientService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<StockCheckIngredientEntity> getById(@PathVariable Long id) {
        return Result.success(stockCheckIngredientService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody StockCheckIngredientEntity entity) {
        stockCheckIngredientService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StockCheckIngredientEntity entity) {
        entity.setId(id);
        stockCheckIngredientService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        stockCheckIngredientService.remove(id);
        return Result.success();
    }
}
