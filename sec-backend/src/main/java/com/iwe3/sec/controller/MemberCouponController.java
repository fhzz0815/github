package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberCouponService;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_coupon 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberCoupons")
public class MemberCouponController {

    private final IMemberCouponService memberCouponService;

    public MemberCouponController(IMemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberCouponEntity>> list(MemberCouponEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberCouponService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberCouponEntity> getById(@PathVariable Long id) {
        return Result.success(memberCouponService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberCouponEntity entity) {
        memberCouponService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberCouponEntity entity) {
        entity.setId(id);
        memberCouponService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberCouponService.remove(id);
        return Result.success();
    }
}
