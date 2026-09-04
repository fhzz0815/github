package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishService;
import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishes")
public class DishController {

    private final IDishService dishService;

    public DishController(IDishService dishService) {
        this.dishService = dishService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishEntity>> list(DishEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishEntity> getById(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishEntity entity) {
        dishService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishEntity entity) {
        entity.setId(id);
        dishService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishService.remove(id);
        return Result.success();
    }
}
