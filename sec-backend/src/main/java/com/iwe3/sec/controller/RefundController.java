package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IRefundService;
import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * refund 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final IRefundService refundService;

    public RefundController(IRefundService refundService) {
        this.refundService = refundService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<RefundEntity>> list(RefundEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(refundService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<RefundEntity> getById(@PathVariable Long id) {
        return Result.success(refundService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody RefundEntity entity) {
        refundService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RefundEntity entity) {
        entity.setId(id);
        refundService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        refundService.remove(id);
        return Result.success();
    }
}
