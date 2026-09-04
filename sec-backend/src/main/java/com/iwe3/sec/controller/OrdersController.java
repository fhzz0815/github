package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * orders 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    // 订单业务接口
    private final IOrdersService ordersService;

    // 通过构造方法把订单业务接口传进来
    public OrdersController(IOrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<OrdersEntity>> list(OrdersEntity query,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ordersService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<OrdersEntity> getById(@PathVariable Long id) {
        return Result.success(ordersService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody OrdersEntity entity) {
        ordersService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody OrdersEntity entity) {
        entity.setId(id);
        ordersService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ordersService.remove(id);
        return Result.success();
    }
}
