package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberBankCardService;
import com.iwe3.sec.entity.MemberBankCardEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_bank_card 表的表现层控制器
 */
@Tag(name = "会员银行卡", description = "会员银行卡的增删改查")
@RestController
@RequestMapping("/api/v1/memberBankCards")
public class MemberBankCardController {

    private final IMemberBankCardService memberBankCardService;

    public MemberBankCardController(IMemberBankCardService memberBankCardService) {
        this.memberBankCardService = memberBankCardService;
    }

    @Operation(summary = "分页查询会员银行卡管理列表")
    @GetMapping
    public Result<PageResult<MemberBankCardEntity>> list(MemberBankCardEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberBankCardService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员银行卡管理详情")
    @GetMapping("/{id}")
    public Result<MemberBankCardEntity> getById(@PathVariable Long id) {
        return Result.success(memberBankCardService.getById(id));
    }

    @Operation(summary = "新增会员银行卡管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberBankCardEntity entity) {
        memberBankCardService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员银行卡管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberBankCardEntity entity) {
        entity.setId(id);
        memberBankCardService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员银行卡管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberBankCardService.remove(id);
        return Result.success();
    }
}
