package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishCategoryService;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_category 表的表现层控制器
 */
@Tag(name = "菜品分类", description = "菜品分类的增删改查")
@RestController
@RequestMapping("/api/v1/dishCategories")
public class DishCategoryController {

    private final IDishCategoryService dishCategoryService;

    public DishCategoryController(IDishCategoryService dishCategoryService) {
        this.dishCategoryService = dishCategoryService;
    }

    @Operation(summary = "分页查询菜品分类管理列表")
    @GetMapping
    public Result<PageResult<DishCategoryEntity>> list(DishCategoryEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishCategoryService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品分类管理详情")
    @GetMapping("/{id}")
    public Result<DishCategoryEntity> getById(@PathVariable Long id) {
        return Result.success(dishCategoryService.getById(id));
    }

    @Operation(summary = "新增菜品分类管理")
    @PostMapping
    public Result<Void> add(@RequestBody DishCategoryEntity entity) {
        dishCategoryService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品分类管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishCategoryEntity entity) {
        entity.setId(id);
        dishCategoryService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品分类管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishCategoryService.remove(id);
        return Result.success();
    }
}
