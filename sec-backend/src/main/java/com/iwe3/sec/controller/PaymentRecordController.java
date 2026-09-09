package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IPaymentRecordService;
import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * payment_record 表的表现层控制器
 */
@Tag(name = "支付记录", description = "支付记录的增删改查")
@RestController
@RequestMapping("/api/v1/paymentRecords")
public class PaymentRecordController {

    private final IPaymentRecordService paymentRecordService;

    public PaymentRecordController(IPaymentRecordService paymentRecordService) {
        this.paymentRecordService = paymentRecordService;
    }

    @Operation(summary = "分页查询支付记录管理列表")
    @GetMapping
    public Result<PageResult<PaymentRecordEntity>> list(PaymentRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(paymentRecordService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询支付记录管理详情")
    @GetMapping("/{id}")
    public Result<PaymentRecordEntity> getById(@PathVariable Long id) {
        return Result.success(paymentRecordService.getById(id));
    }

    @Operation(summary = "新增支付记录管理")
    @PostMapping
    public Result<Void> add(@RequestBody PaymentRecordEntity entity) {
        paymentRecordService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改支付记录管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PaymentRecordEntity entity) {
        entity.setId(id);
        paymentRecordService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除支付记录管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        paymentRecordService.remove(id);
        return Result.success();
    }
}
