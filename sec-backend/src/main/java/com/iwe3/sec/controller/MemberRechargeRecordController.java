package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberRechargeRecordService;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_recharge_record 表的表现层控制器
 */
@Tag(name = "会员充值记录", description = "会员充值记录的增删改查")
@RestController
@RequestMapping("/api/v1/memberRechargeRecords")
public class MemberRechargeRecordController {

    private final IMemberRechargeRecordService memberRechargeRecordService;

    public MemberRechargeRecordController(IMemberRechargeRecordService memberRechargeRecordService) {
        this.memberRechargeRecordService = memberRechargeRecordService;
    }

    @Operation(summary = "分页查询会员充值记录管理列表")
    @GetMapping
    public Result<PageResult<MemberRechargeRecordEntity>> list(MemberRechargeRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberRechargeRecordService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员充值记录管理详情")
    @GetMapping("/{id}")
    public Result<MemberRechargeRecordEntity> getById(@PathVariable Long id) {
        return Result.success(memberRechargeRecordService.getById(id));
    }

    @Operation(summary = "新增会员充值记录管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberRechargeRecordEntity entity) {
        memberRechargeRecordService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员充值记录管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberRechargeRecordEntity entity) {
        entity.setId(id);
        memberRechargeRecordService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员充值记录管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberRechargeRecordService.remove(id);
        return Result.success();
    }
}
