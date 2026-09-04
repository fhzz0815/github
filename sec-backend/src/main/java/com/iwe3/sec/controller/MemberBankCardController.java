package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberBankCardService;
import com.iwe3.sec.entity.MemberBankCardEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_bank_card 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberBankCards")
public class MemberBankCardController {

    private final IMemberBankCardService memberBankCardService;

    public MemberBankCardController(IMemberBankCardService memberBankCardService) {
        this.memberBankCardService = memberBankCardService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberBankCardEntity>> list(MemberBankCardEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberBankCardService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberBankCardEntity> getById(@PathVariable Long id) {
        return Result.success(memberBankCardService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberBankCardEntity entity) {
        memberBankCardService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberBankCardEntity entity) {
        entity.setId(id);
        memberBankCardService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberBankCardService.remove(id);
        return Result.success();
    }
}
