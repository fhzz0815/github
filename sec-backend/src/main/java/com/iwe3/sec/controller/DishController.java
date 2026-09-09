package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishService;
import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish 表的表现层控制器
 */
@Tag(name = "菜品管理", description = "菜品的增删改查")
@RestController
@RequestMapping("/api/v1/dishes")
public class DishController {

    private final IDishService dishService;

    public DishController(IDishService dishService) {
        this.dishService = dishService;
    }

    @Operation(summary = "分页查询菜品列表")
    @GetMapping
    public Result<PageResult<DishEntity>> list(DishEntity query,
                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                                                   @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品详情")
    @GetMapping("/{id}")
    public Result<DishEntity> getById(@Parameter(description = "菜品ID") @PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @Operation(summary = "新增菜品")
    @PostMapping
    public Result<Void> add(@RequestBody DishEntity entity) {
        dishService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "菜品ID") @PathVariable Long id, @RequestBody DishEntity entity) {
        entity.setId(id);
        dishService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@Parameter(description = "菜品ID") @PathVariable Long id) {
        dishService.remove(id);
        return Result.success();
    }
}
