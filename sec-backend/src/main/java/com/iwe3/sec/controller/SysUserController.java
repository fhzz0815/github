package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysUserService;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_user 表的表现层控制器
 */
@Tag(name = "系统用户管理", description = "系统用户管理的增删改查")
@RestController
@RequestMapping("/api/v1/sysUsers")
public class SysUserController {

    private final ISysUserService sysUserService;

    public SysUserController(ISysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Operation(summary = "分页查询系统用户管理列表")
    @GetMapping
    public Result<PageResult<SysUserEntity>> list(SysUserEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysUserService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询系统用户管理详情")
    @GetMapping("/{id}")
    public Result<SysUserEntity> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @Operation(summary = "新增系统用户管理")
    @PostMapping
    public Result<Void> add(@RequestBody SysUserEntity entity) {
        sysUserService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改系统用户管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUserEntity entity) {
        entity.setId(id);
        sysUserService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除系统用户管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysUserService.remove(id);
        return Result.success();
    }
}
