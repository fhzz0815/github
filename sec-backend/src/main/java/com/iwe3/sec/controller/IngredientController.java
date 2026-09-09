package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IIngredientService;
import com.iwe3.sec.entity.IngredientEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * ingredient 表的表现层控制器
 */
@Tag(name = "原料管理", description = "原料管理的增删改查")
@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    private final IIngredientService ingredientService;

    public IngredientController(IIngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "分页查询原料管理列表")
    @GetMapping
    public Result<PageResult<IngredientEntity>> list(IngredientEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ingredientService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询原料管理详情")
    @GetMapping("/{id}")
    public Result<IngredientEntity> getById(@PathVariable Long id) {
        return Result.success(ingredientService.getById(id));
    }

    @Operation(summary = "新增原料管理")
    @PostMapping
    public Result<Void> add(@RequestBody IngredientEntity entity) {
        ingredientService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改原料管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody IngredientEntity entity) {
        entity.setId(id);
        ingredientService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除原料管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ingredientService.remove(id);
        return Result.success();
    }
}
