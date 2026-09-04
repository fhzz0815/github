package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysRolePermissionService;
import com.iwe3.sec.entity.SysRolePermissionEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_role_permission 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/sysRolePermissions")
public class SysRolePermissionController {

    private final ISysRolePermissionService sysRolePermissionService;

    public SysRolePermissionController(ISysRolePermissionService sysRolePermissionService) {
        this.sysRolePermissionService = sysRolePermissionService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<SysRolePermissionEntity>> list(SysRolePermissionEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysRolePermissionService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<SysRolePermissionEntity> getById(@PathVariable Long id) {
        return Result.success(sysRolePermissionService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody SysRolePermissionEntity entity) {
        sysRolePermissionService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRolePermissionEntity entity) {
        entity.setId(id);
        sysRolePermissionService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysRolePermissionService.remove(id);
        return Result.success();
    }
}
