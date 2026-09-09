package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberService;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member 表的表现层控制器
 */
@Tag(name = "会员管理", description = "会员管理的增删改查")
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final IMemberService memberService;

    public MemberController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "分页查询会员管理列表")
    @GetMapping
    public Result<PageResult<MemberEntity>> list(MemberEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员管理详情")
    @GetMapping("/{id}")
    public Result<MemberEntity> getById(@PathVariable Long id) {
        return Result.success(memberService.getById(id));
    }

    @Operation(summary = "新增会员管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberEntity entity) {
        memberService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberEntity entity) {
        entity.setId(id);
        memberService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberService.remove(id);
        return Result.success();
    }
}
