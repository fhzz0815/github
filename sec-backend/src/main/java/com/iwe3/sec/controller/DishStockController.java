package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishStockService;
import com.iwe3.sec.entity.DishStockEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_stock 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishStocks")
public class DishStockController {

    private final IDishStockService dishStockService;

    public DishStockController(IDishStockService dishStockService) {
        this.dishStockService = dishStockService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishStockEntity>> list(DishStockEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishStockService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishStockEntity> getById(@PathVariable Long id) {
        return Result.success(dishStockService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishStockEntity entity) {
        dishStockService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishStockEntity entity) {
        entity.setId(id);
        dishStockService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishStockService.remove(id);
        return Result.success();
    }
}
