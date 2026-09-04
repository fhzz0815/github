package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberRechargeRecordService;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_recharge_record 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberRechargeRecords")
public class MemberRechargeRecordController {

    private final IMemberRechargeRecordService memberRechargeRecordService;

    public MemberRechargeRecordController(IMemberRechargeRecordService memberRechargeRecordService) {
        this.memberRechargeRecordService = memberRechargeRecordService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberRechargeRecordEntity>> list(MemberRechargeRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberRechargeRecordService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberRechargeRecordEntity> getById(@PathVariable Long id) {
        return Result.success(memberRechargeRecordService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberRechargeRecordEntity entity) {
        memberRechargeRecordService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberRechargeRecordEntity entity) {
        entity.setId(id);
        memberRechargeRecordService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberRechargeRecordService.remove(id);
        return Result.success();
    }
}
