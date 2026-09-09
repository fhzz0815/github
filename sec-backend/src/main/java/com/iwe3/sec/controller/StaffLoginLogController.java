package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStaffLoginLogService;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * staff_login_log 表的表现层控制器
 */
@Tag(name = "员工登录日志", description = "员工登录日志的增删改查")
@RestController
@RequestMapping("/api/v1/staffLoginLogs")
public class StaffLoginLogController {

    private final IStaffLoginLogService staffLoginLogService;

    public StaffLoginLogController(IStaffLoginLogService staffLoginLogService) {
        this.staffLoginLogService = staffLoginLogService;
    }

    @Operation(summary = "分页查询员工登录日志管理列表")
    @GetMapping
    public Result<PageResult<StaffLoginLogEntity>> list(StaffLoginLogEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(staffLoginLogService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询员工登录日志管理详情")
    @GetMapping("/{id}")
    public Result<StaffLoginLogEntity> getById(@PathVariable Long id) {
        return Result.success(staffLoginLogService.getById(id));
    }

    @Operation(summary = "新增员工登录日志管理")
    @PostMapping
    public Result<Void> add(@RequestBody StaffLoginLogEntity entity) {
        staffLoginLogService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改员工登录日志管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StaffLoginLogEntity entity) {
        entity.setId(id);
        staffLoginLogService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除员工登录日志管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        staffLoginLogService.remove(id);
        return Result.success();
    }
}
