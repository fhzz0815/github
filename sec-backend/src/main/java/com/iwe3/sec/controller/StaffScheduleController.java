package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStaffScheduleService;
import com.iwe3.sec.entity.StaffScheduleEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * staff_schedule 表的表现层控制器
 */
@Tag(name = "员工排班管理", description = "员工排班管理的增删改查")
@RestController
@RequestMapping("/api/v1/staffSchedules")
public class StaffScheduleController {

    private final IStaffScheduleService staffScheduleService;

    public StaffScheduleController(IStaffScheduleService staffScheduleService) {
        this.staffScheduleService = staffScheduleService;
    }

    @Operation(summary = "分页查询员工排班管理列表")
    @GetMapping
    public Result<PageResult<StaffScheduleEntity>> list(StaffScheduleEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(staffScheduleService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询员工排班管理详情")
    @GetMapping("/{id}")
    public Result<StaffScheduleEntity> getById(@PathVariable Long id) {
        return Result.success(staffScheduleService.getById(id));
    }

    @Operation(summary = "新增员工排班管理")
    @PostMapping
    public Result<Void> add(@RequestBody StaffScheduleEntity entity) {
        staffScheduleService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改员工排班管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StaffScheduleEntity entity) {
        entity.setId(id);
        staffScheduleService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除员工排班管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        staffScheduleService.remove(id);
        return Result.success();
    }
}
