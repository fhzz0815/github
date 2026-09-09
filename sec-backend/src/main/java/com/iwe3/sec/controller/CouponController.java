package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ICouponService;
import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * coupon 表的表现层控制器
 */
@Tag(name = "优惠券管理", description = "优惠券管理的增删改查")
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final ICouponService couponService;

    public CouponController(ICouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "分页查询优惠券管理列表")
    @GetMapping
    public Result<PageResult<CouponEntity>> list(CouponEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(couponService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询优惠券管理详情")
    @GetMapping("/{id}")
    public Result<CouponEntity> getById(@PathVariable Long id) {
        return Result.success(couponService.getById(id));
    }

    @Operation(summary = "新增优惠券管理")
    @PostMapping
    public Result<Void> add(@RequestBody CouponEntity entity) {
        couponService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改优惠券管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CouponEntity entity) {
        entity.setId(id);
        couponService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除优惠券管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        couponService.remove(id);
        return Result.success();
    }
}
