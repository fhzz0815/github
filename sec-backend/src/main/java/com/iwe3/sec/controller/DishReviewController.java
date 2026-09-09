package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishReviewService;
import com.iwe3.sec.entity.DishReviewEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_review 表的表现层控制器
 */
@Tag(name = "菜品评价", description = "菜品评价的增删改查")
@RestController
@RequestMapping("/api/v1/dishReviews")
public class DishReviewController {

    private final IDishReviewService dishReviewService;

    public DishReviewController(IDishReviewService dishReviewService) {
        this.dishReviewService = dishReviewService;
    }

    @Operation(summary = "分页查询菜品评价管理列表")
    @GetMapping
    public Result<PageResult<DishReviewEntity>> list(DishReviewEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishReviewService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询菜品评价管理详情")
    @GetMapping("/{id}")
    public Result<DishReviewEntity> getById(@PathVariable Long id) {
        return Result.success(dishReviewService.getById(id));
    }

    @Operation(summary = "新增菜品评价管理")
    @PostMapping
    public Result<Void> add(@RequestBody DishReviewEntity entity) {
        dishReviewService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改菜品评价管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishReviewEntity entity) {
        entity.setId(id);
        dishReviewService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除菜品评价管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishReviewService.remove(id);
        return Result.success();
    }
}
