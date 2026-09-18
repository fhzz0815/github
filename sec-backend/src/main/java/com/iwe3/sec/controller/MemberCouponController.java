package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberCouponService;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

import java.util.Map;

/**
 * member_coupon 表的表现层控制器
 */
@Tag(name = "会员优惠券", description = "会员优惠券的增删改查、领券")
@RestController
@RequestMapping("/api/v1/memberCoupons")
public class MemberCouponController {

    private final IMemberCouponService memberCouponService;

    public MemberCouponController(IMemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    @Operation(summary = "分页查询会员优惠券管理列表")
    @GetMapping
    public Result<PageResult<MemberCouponEntity>> list(MemberCouponEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberCouponService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员优惠券管理详情")
    @GetMapping("/{id}")
    public Result<MemberCouponEntity> getById(@PathVariable Long id) {
        return Result.success(memberCouponService.getById(id));
    }

    @Operation(summary = "新增会员优惠券管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberCouponEntity entity) {
        memberCouponService.add(entity);
        return Result.success();
    }

    @Operation(summary = "会员领取优惠券（带分布式锁防超发）")
    @PostMapping("/claim")
    public Result<Long> claimCoupon(@RequestBody Map<String, Object> params) {
        Long couponId = Long.valueOf(params.get("couponId").toString());
        Long memberId = Long.valueOf(params.get("memberId").toString());
        Long storeId = params.get("storeId") != null
                ? Long.valueOf(params.get("storeId").toString()) : null;
        Long id = memberCouponService.claimCoupon(couponId, memberId, storeId);
        return Result.success(id);
    }

    @Operation(summary = "修改会员优惠券管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberCouponEntity entity) {
        entity.setId(id);
        memberCouponService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员优惠券管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberCouponService.remove(id);
        return Result.success();
    }
}
