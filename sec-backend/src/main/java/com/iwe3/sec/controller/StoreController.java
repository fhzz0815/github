package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStoreService;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * store 表的表现层控制器
 */
@Tag(name = "门店管理", description = "门店的增删改查")
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final IStoreService storeService;

    public StoreController(IStoreService storeService) {
        this.storeService = storeService;
    }

    @Operation(summary = "分页查询门店列表")
    @GetMapping
    public Result<PageResult<StoreEntity>> list(StoreEntity query,
                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
                                                   @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(storeService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询门店详情")
    @GetMapping("/{id}")
    public Result<StoreEntity> getById(@Parameter(description = "门店ID") @PathVariable Long id) {
        return Result.success(storeService.getById(id));
    }

    @Operation(summary = "新增门店")
    @PostMapping
    public Result<Void> add(@RequestBody StoreEntity entity) {
        storeService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改门店")
    @PutMapping("/{id}")
    public Result<Void> update(@Parameter(description = "门店ID") @PathVariable Long id, @RequestBody StoreEntity entity) {
        entity.setId(id);
        storeService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除门店")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@Parameter(description = "门店ID") @PathVariable Long id) {
        storeService.remove(id);
        return Result.success();
    }
}
