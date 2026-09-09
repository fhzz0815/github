package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDiningTableService;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dining_table 表的表现层控制器
 */
@Tag(name = "桌台管理", description = "桌台管理的增删改查")
@RestController
@RequestMapping("/api/v1/diningTables")
public class DiningTableController {

    private final IDiningTableService diningTableService;

    public DiningTableController(IDiningTableService diningTableService) {
        this.diningTableService = diningTableService;
    }

    @Operation(summary = "分页查询桌台管理列表")
    @GetMapping
    public Result<PageResult<DiningTableEntity>> list(DiningTableEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(diningTableService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询桌台管理详情")
    @GetMapping("/{id}")
    public Result<DiningTableEntity> getById(@PathVariable Long id) {
        return Result.success(diningTableService.getById(id));
    }

    @Operation(summary = "新增桌台管理")
    @PostMapping
    public Result<Void> add(@RequestBody DiningTableEntity entity) {
        diningTableService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改桌台管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DiningTableEntity entity) {
        entity.setId(id);
        diningTableService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除桌台管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        diningTableService.remove(id);
        return Result.success();
    }
}
