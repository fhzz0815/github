package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishSpecService;
import com.iwe3.sec.entity.DishSpecEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_spec 表的表现层控制器
 */
@Tag(name = "菜品规格", description = "菜品规格的增删改查")
@RestController
@RequestMapping("/api/v1/dishSpecs")
public class DishSpecController {

    private final IDishSpecService dishSpecService;

    public DishSpecController(IDishSpecService dishSpecService) {
        this.dishSpecService = dishSpecService;
    }

    @Operation(summary = "分页查询菜品规格管理列表")
    @GetMapping
    public Result<PageResult<DishSpecEntity>> list(DishSpecEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishSpecService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品规格管理详情")
    @GetMapping("/{id}")
    public Result<DishSpecEntity> getById(@PathVariable Long id) {
        return Result.success(dishSpecService.getById(id));
    }

    @Operation(summary = "新增菜品规格管理")
    @PostMapping
    public Result<Void> add(@RequestBody DishSpecEntity entity) {
        dishSpecService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品规格管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishSpecEntity entity) {
        entity.setId(id);
        dishSpecService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品规格管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishSpecService.remove(id);
        return Result.success();
    }
}
