package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishTasteService;
import com.iwe3.sec.entity.DishTasteEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_taste 表的表现层控制器
 */
@Tag(name = "菜品口味", description = "菜品口味的增删改查")
@RestController
@RequestMapping("/api/v1/dishTastes")
public class DishTasteController {

    private final IDishTasteService dishTasteService;

    public DishTasteController(IDishTasteService dishTasteService) {
        this.dishTasteService = dishTasteService;
    }

    @Operation(summary = "分页查询菜品口味管理列表")
    @GetMapping
    public Result<PageResult<DishTasteEntity>> list(DishTasteEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishTasteService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品口味管理详情")
    @GetMapping("/{id}")
    public Result<DishTasteEntity> getById(@PathVariable Long id) {
        return Result.success(dishTasteService.getById(id));
    }

    @Operation(summary = "新增菜品口味管理")
    @PostMapping
    public Result<Void> add(@RequestBody DishTasteEntity entity) {
        dishTasteService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品口味管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishTasteEntity entity) {
        entity.setId(id);
        dishTasteService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品口味管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishTasteService.remove(id);
        return Result.success();
    }
}
