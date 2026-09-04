package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberBalanceRecordService;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_balance_record 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberBalanceRecords")
public class MemberBalanceRecordController {

    private final IMemberBalanceRecordService memberBalanceRecordService;

    public MemberBalanceRecordController(IMemberBalanceRecordService memberBalanceRecordService) {
        this.memberBalanceRecordService = memberBalanceRecordService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberBalanceRecordEntity>> list(MemberBalanceRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberBalanceRecordService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberBalanceRecordEntity> getById(@PathVariable Long id) {
        return Result.success(memberBalanceRecordService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberBalanceRecordEntity entity) {
        memberBalanceRecordService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberBalanceRecordEntity entity) {
        entity.setId(id);
        memberBalanceRecordService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberBalanceRecordService.remove(id);
        return Result.success();
    }
}
