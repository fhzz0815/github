package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysPermissionService;
import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_permission 表的表现层控制器
 */
@Tag(name = "系统权限管理", description = "系统权限管理的增删改查")
@RestController
@RequestMapping("/api/v1/sysPermissions")
public class SysPermissionController {

    private final ISysPermissionService sysPermissionService;

    public SysPermissionController(ISysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

    @Operation(summary = "分页查询系统权限管理列表")
    @GetMapping
    public Result<PageResult<SysPermissionEntity>> list(SysPermissionEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysPermissionService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询系统权限管理详情")
    @GetMapping("/{id}")
    public Result<SysPermissionEntity> getById(@PathVariable Long id) {
        return Result.success(sysPermissionService.getById(id));
    }

    @Operation(summary = "新增系统权限管理")
    @PostMapping
    public Result<Void> add(@RequestBody SysPermissionEntity entity) {
        sysPermissionService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改系统权限管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPermissionEntity entity) {
        entity.setId(id);
        sysPermissionService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除系统权限管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysPermissionService.remove(id);
        return Result.success();
    }
}
