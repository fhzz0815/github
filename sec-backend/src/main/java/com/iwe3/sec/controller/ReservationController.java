package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IReservationService;
import com.iwe3.sec.entity.ReservationEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * reservation 表的表现层控制器
 */
@Tag(name = "预约管理", description = "预约管理的增删改查")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "分页查询预约管理列表")
    @GetMapping
    public Result<PageResult<ReservationEntity>> list(ReservationEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reservationService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询预约管理详情")
    @GetMapping("/{id}")
    public Result<ReservationEntity> getById(@PathVariable Long id) {
        return Result.success(reservationService.getById(id));
    }

    @Operation(summary = "新增预约管理")
    @PostMapping
    public Result<Void> add(@RequestBody ReservationEntity entity) {
        reservationService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改预约管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReservationEntity entity) {
        entity.setId(id);
        reservationService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除预约管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        reservationService.remove(id);
        return Result.success();
    }
}
