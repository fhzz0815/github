package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysPermissionService;
import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_permission 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/sysPermissions")
public class SysPermissionController {

    private final ISysPermissionService sysPermissionService;

    public SysPermissionController(ISysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<SysPermissionEntity>> list(SysPermissionEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysPermissionService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<SysPermissionEntity> getById(@PathVariable Long id) {
        return Result.success(sysPermissionService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody SysPermissionEntity entity) {
        sysPermissionService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPermissionEntity entity) {
        entity.setId(id);
        sysPermissionService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysPermissionService.remove(id);
        return Result.success();
    }
}
