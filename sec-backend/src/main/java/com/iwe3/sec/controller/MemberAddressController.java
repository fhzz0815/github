package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberAddressService;
import com.iwe3.sec.entity.MemberAddressEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_address 表的表现层控制器
 */
@Tag(name = "会员地址", description = "会员地址的增删改查")
@RestController
@RequestMapping("/api/v1/memberAddresses")
public class MemberAddressController {

    private final IMemberAddressService memberAddressService;

    public MemberAddressController(IMemberAddressService memberAddressService) {
        this.memberAddressService = memberAddressService;
    }

    @Operation(summary = "分页查询会员地址管理列表")
    @GetMapping
    public Result<PageResult<MemberAddressEntity>> list(MemberAddressEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberAddressService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员地址管理详情")
    @GetMapping("/{id}")
    public Result<MemberAddressEntity> getById(@PathVariable Long id) {
        return Result.success(memberAddressService.getById(id));
    }

    @Operation(summary = "新增会员地址管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberAddressEntity entity) {
        memberAddressService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员地址管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberAddressEntity entity) {
        entity.setId(id);
        memberAddressService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员地址管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberAddressService.remove(id);
        return Result.success();
    }
}
