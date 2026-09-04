package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ICouponService;
import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * coupon 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final ICouponService couponService;

    public CouponController(ICouponService couponService) {
        this.couponService = couponService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<CouponEntity>> list(CouponEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(couponService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<CouponEntity> getById(@PathVariable Long id) {
        return Result.success(couponService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody CouponEntity entity) {
        couponService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CouponEntity entity) {
        entity.setId(id);
        couponService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        couponService.remove(id);
        return Result.success();
    }
}
