package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IReservationService;
import com.iwe3.sec.entity.ReservationEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * reservation 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<ReservationEntity>> list(ReservationEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reservationService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<ReservationEntity> getById(@PathVariable Long id) {
        return Result.success(reservationService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody ReservationEntity entity) {
        reservationService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReservationEntity entity) {
        entity.setId(id);
        reservationService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        reservationService.remove(id);
        return Result.success();
    }
}
