package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStockCheckIngredientService;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

import java.util.Map;

/**
 * stock_check_ingredient 表的表现层控制器
 */
@Tag(name = "原料盘点", description = "原料盘点的增删改查与审核")
@RestController
@RequestMapping("/api/v1/stockCheckIngredients")
public class StockCheckIngredientController {

    private final IStockCheckIngredientService stockCheckIngredientService;

    public StockCheckIngredientController(IStockCheckIngredientService stockCheckIngredientService) {
        this.stockCheckIngredientService = stockCheckIngredientService;
    }

    @Operation(summary = "分页查询食材盘点管理列表")
    @GetMapping
    public Result<PageResult<StockCheckIngredientEntity>> list(StockCheckIngredientEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(stockCheckIngredientService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询食材盘点管理详情")
    @GetMapping("/{id}")
    public Result<StockCheckIngredientEntity> getById(@PathVariable Long id) {
        return Result.success(stockCheckIngredientService.getById(id));
    }

    @Operation(summary = "新增食材盘点管理")
    @PostMapping
    public Result<Void> add(@RequestBody StockCheckIngredientEntity entity) {
        stockCheckIngredientService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改食材盘点管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StockCheckIngredientEntity entity) {
        entity.setId(id);
        stockCheckIngredientService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除食材盘点管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        stockCheckIngredientService.remove(id);
        return Result.success();
    }

    @Operation(summary = "审核食材盘点单（总店长专用）")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String auditRemark = (String) body.get("auditRemark");
        stockCheckIngredientService.approve(id, approved, auditRemark);
        return Result.success();
    }
}
