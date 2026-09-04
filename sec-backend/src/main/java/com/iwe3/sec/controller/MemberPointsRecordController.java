package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberPointsRecordService;
import com.iwe3.sec.entity.MemberPointsRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_points_record 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberPointsRecords")
public class MemberPointsRecordController {

    private final IMemberPointsRecordService memberPointsRecordService;

    public MemberPointsRecordController(IMemberPointsRecordService memberPointsRecordService) {
        this.memberPointsRecordService = memberPointsRecordService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberPointsRecordEntity>> list(MemberPointsRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberPointsRecordService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberPointsRecordEntity> getById(@PathVariable Long id) {
        return Result.success(memberPointsRecordService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberPointsRecordEntity entity) {
        memberPointsRecordService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberPointsRecordEntity entity) {
        entity.setId(id);
        memberPointsRecordService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberPointsRecordService.remove(id);
        return Result.success();
    }
}
