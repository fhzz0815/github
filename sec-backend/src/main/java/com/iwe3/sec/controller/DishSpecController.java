package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDishSpecService;
import com.iwe3.sec.entity.DishSpecEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dish_spec 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/dishSpecs")
public class DishSpecController {

    private final IDishSpecService dishSpecService;

    public DishSpecController(IDishSpecService dishSpecService) {
        this.dishSpecService = dishSpecService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DishSpecEntity>> list(DishSpecEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(dishSpecService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DishSpecEntity> getById(@PathVariable Long id) {
        return Result.success(dishSpecService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DishSpecEntity entity) {
        dishSpecService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishSpecEntity entity) {
        entity.setId(id);
        dishSpecService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dishSpecService.remove(id);
        return Result.success();
    }
}
