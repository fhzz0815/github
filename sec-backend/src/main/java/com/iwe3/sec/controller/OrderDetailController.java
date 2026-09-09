package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IOrderDetailService;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * order_detail 表的表现层控制器
 */
@Tag(name = "订单明细", description = "订单明细的增删改查")
@RestController
@RequestMapping("/api/v1/orderDetails")
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    public OrderDetailController(IOrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @Operation(summary = "分页查询订单明细管理列表")
    @GetMapping
    public Result<PageResult<OrderDetailEntity>> list(OrderDetailEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderDetailService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询订单明细管理详情")
    @GetMapping("/{id}")
    public Result<OrderDetailEntity> getById(@PathVariable Long id) {
        return Result.success(orderDetailService.getById(id));
    }

    @Operation(summary = "新增订单明细管理")
    @PostMapping
    public Result<Void> add(@RequestBody OrderDetailEntity entity) {
        orderDetailService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改订单明细管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody OrderDetailEntity entity) {
        entity.setId(id);
        orderDetailService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除订单明细管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        orderDetailService.remove(id);
        return Result.success();
    }
}
