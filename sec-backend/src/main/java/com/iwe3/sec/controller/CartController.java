package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ICartService;
import com.iwe3.sec.entity.CartEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * cart 表的表现层控制器
 */
@Tag(name = "购物车管理", description = "购物车管理的增删改查")
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "分页查询购物车管理列表")
    @GetMapping
    public Result<PageResult<CartEntity>> list(CartEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(cartService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询购物车管理详情")
    @GetMapping("/{id}")
    public Result<CartEntity> getById(@PathVariable Long id) {
        return Result.success(cartService.getById(id));
    }

    @Operation(summary = "新增购物车管理")
    @PostMapping
    public Result<Void> add(@RequestBody CartEntity entity) {
        cartService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改购物车管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartEntity entity) {
        entity.setId(id);
        cartService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除购物车管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(id);
        return Result.success();
    }
}
