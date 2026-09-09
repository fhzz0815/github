package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysRoleService;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_role 表的表现层控制器
 */
@Tag(name = "系统角色管理", description = "系统角色管理的增删改查")
@RestController
@RequestMapping("/api/v1/sysRoles")
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    public SysRoleController(ISysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @Operation(summary = "分页查询系统角色管理列表")
    @GetMapping
    public Result<PageResult<SysRoleEntity>> list(SysRoleEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysRoleService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询系统角色管理详情")
    @GetMapping("/{id}")
    public Result<SysRoleEntity> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @Operation(summary = "新增系统角色管理")
    @PostMapping
    public Result<Void> add(@RequestBody SysRoleEntity entity) {
        sysRoleService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改系统角色管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRoleEntity entity) {
        entity.setId(id);
        sysRoleService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除系统角色管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysRoleService.remove(id);
        return Result.success();
    }
}
