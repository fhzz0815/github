package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ICartService;
import com.iwe3.sec.entity.CartEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * cart 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<CartEntity>> list(CartEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(cartService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<CartEntity> getById(@PathVariable Long id) {
        return Result.success(cartService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody CartEntity entity) {
        cartService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CartEntity entity) {
        entity.setId(id);
        cartService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(id);
        return Result.success();
    }
}
