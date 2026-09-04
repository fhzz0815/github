package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IIngredientCategoryService;
import com.iwe3.sec.entity.IngredientCategoryEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient_category 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/ingredientCategories")
public class IngredientCategoryController {

    private final IIngredientCategoryService ingredientCategoryService;

    public IngredientCategoryController(IIngredientCategoryService ingredientCategoryService) {
        this.ingredientCategoryService = ingredientCategoryService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<IngredientCategoryEntity>> list(IngredientCategoryEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ingredientCategoryService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<IngredientCategoryEntity> getById(@PathVariable Long id) {
        return Result.success(ingredientCategoryService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody IngredientCategoryEntity entity) {
        ingredientCategoryService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody IngredientCategoryEntity entity) {
        entity.setId(id);
        ingredientCategoryService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ingredientCategoryService.remove(id);
        return Result.success();
    }
}
