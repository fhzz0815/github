package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IRefundService;
import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

import java.util.Map;

/**
 * refund 表的表现层控制器
 */
@Tag(name = "退款管理", description = "退款管理的增删改查与审核处理")
@RestController
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final IRefundService refundService;

    public RefundController(IRefundService refundService) {
        this.refundService = refundService;
    }

    @Operation(summary = "分页查询退款管理列表")
    @GetMapping
    public Result<PageResult<RefundEntity>> list(RefundEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(refundService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询退款管理详情")
    @GetMapping("/{id}")
    public Result<RefundEntity> getById(@PathVariable Long id) {
        return Result.success(refundService.getById(id));
    }

    @Operation(summary = "新增退款管理")
    @PostMapping
    public Result<Void> add(@RequestBody RefundEntity entity) {
        refundService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改退款管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RefundEntity entity) {
        entity.setId(id);
        refundService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除退款管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        refundService.remove(id);
        return Result.success();
    }

    @Operation(summary = "提交退款申请（退菜/整单退）")
    @PostMapping("/submit")
    public Result<Long> submitRefund(@RequestBody RefundEntity entity) {
        Long refundId = refundService.submitRefund(entity);
        return Result.success(refundId);
    }

    @Operation(summary = "处理退款申请（审核通过或驳回）")
    @PutMapping("/{id}/process")
    public Result<Void> processRefund(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String auditRemark = (String) body.get("auditRemark");
        refundService.processRefund(id, approved, auditRemark);
        return Result.success();
    }
}
