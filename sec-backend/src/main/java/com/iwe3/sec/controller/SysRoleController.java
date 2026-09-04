package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ISysRoleService;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * sys_role 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/sysRoles")
public class SysRoleController {

    private final ISysRoleService sysRoleService;

    public SysRoleController(ISysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<SysRoleEntity>> list(SysRoleEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(sysRoleService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<SysRoleEntity> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody SysRoleEntity entity) {
        sysRoleService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRoleEntity entity) {
        entity.setId(id);
        sysRoleService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysRoleService.remove(id);
        return Result.success();
    }
}
