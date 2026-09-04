package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishIngredientRelService;
import com.iwe3.sec.entity.DishIngredientRelEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_ingredient_rel 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishIngredientRels")
public class DishIngredientRelController {

    private final IDishIngredientRelService dishIngredientRelService;

    public DishIngredientRelController(IDishIngredientRelService dishIngredientRelService) {
        this.dishIngredientRelService = dishIngredientRelService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishIngredientRelEntity>> list(DishIngredientRelEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishIngredientRelService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishIngredientRelEntity> getById(@PathVariable Long id) {
        return Result.success(dishIngredientRelService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishIngredientRelEntity entity) {
        dishIngredientRelService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishIngredientRelEntity entity) {
        entity.setId(id);
        dishIngredientRelService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishIngredientRelService.remove(id);
        return Result.success();
    }
}
