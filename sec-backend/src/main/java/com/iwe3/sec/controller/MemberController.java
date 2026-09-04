package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberService;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final IMemberService memberService;

    public MemberController(IMemberService memberService) {
        this.memberService = memberService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberEntity>> list(MemberEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberEntity> getById(@PathVariable Long id) {
        return Result.success(memberService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberEntity entity) {
        memberService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberEntity entity) {
        entity.setId(id);
        memberService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberService.remove(id);
        return Result.success();
    }
}
