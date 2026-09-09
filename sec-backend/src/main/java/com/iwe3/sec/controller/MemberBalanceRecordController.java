package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberBalanceRecordService;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_balance_record 表的表现层控制器
 */
@Tag(name = "会员余额记录", description = "会员余额记录的增删改查")
@RestController
@RequestMapping("/api/v1/memberBalanceRecords")
public class MemberBalanceRecordController {

    private final IMemberBalanceRecordService memberBalanceRecordService;

    public MemberBalanceRecordController(IMemberBalanceRecordService memberBalanceRecordService) {
        this.memberBalanceRecordService = memberBalanceRecordService;
    }

    @Operation(summary = "分页查询会员余额记录管理列表")
    @GetMapping
    public Result<PageResult<MemberBalanceRecordEntity>> list(MemberBalanceRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberBalanceRecordService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员余额记录管理详情")
    @GetMapping("/{id}")
    public Result<MemberBalanceRecordEntity> getById(@PathVariable Long id) {
        return Result.success(memberBalanceRecordService.getById(id));
    }

    @Operation(summary = "新增会员余额记录管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberBalanceRecordEntity entity) {
        memberBalanceRecordService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员余额记录管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberBalanceRecordEntity entity) {
        entity.setId(id);
        memberBalanceRecordService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员余额记录管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberBalanceRecordService.remove(id);
        return Result.success();
    }
}
