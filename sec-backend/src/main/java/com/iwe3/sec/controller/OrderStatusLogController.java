package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IOrderStatusLogService;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * order_status_log 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/orderStatusLogs")
public class OrderStatusLogController {

    private final IOrderStatusLogService orderStatusLogService;

    public OrderStatusLogController(IOrderStatusLogService orderStatusLogService) {
        this.orderStatusLogService = orderStatusLogService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<OrderStatusLogEntity>> list(OrderStatusLogEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderStatusLogService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<OrderStatusLogEntity> getById(@PathVariable Long id) {
        return Result.success(orderStatusLogService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody OrderStatusLogEntity entity) {
        orderStatusLogService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody OrderStatusLogEntity entity) {
        entity.setId(id);
        orderStatusLogService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        orderStatusLogService.remove(id);
        return Result.success();
    }
}
