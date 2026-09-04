package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishReviewService;
import com.iwe3.sec.entity.DishReviewEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_review 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishReviews")
public class DishReviewController {

    private final IDishReviewService dishReviewService;

    public DishReviewController(IDishReviewService dishReviewService) {
        this.dishReviewService = dishReviewService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishReviewEntity>> list(DishReviewEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishReviewService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishReviewEntity> getById(@PathVariable Long id) {
        return Result.success(dishReviewService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishReviewEntity entity) {
        dishReviewService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishReviewEntity entity) {
        entity.setId(id);
        dishReviewService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishReviewService.remove(id);
        return Result.success();
    }
}
