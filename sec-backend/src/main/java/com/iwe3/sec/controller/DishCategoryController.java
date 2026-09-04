package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishCategoryService;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_category 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishCategories")
public class DishCategoryController {

    private final IDishCategoryService dishCategoryService;

    public DishCategoryController(IDishCategoryService dishCategoryService) {
        this.dishCategoryService = dishCategoryService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishCategoryEntity>> list(DishCategoryEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishCategoryService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishCategoryEntity> getById(@PathVariable Long id) {
        return Result.success(dishCategoryService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishCategoryEntity entity) {
        dishCategoryService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishCategoryEntity entity) {
        entity.setId(id);
        dishCategoryService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishCategoryService.remove(id);
        return Result.success();
    }
}
