package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IPaymentRecordService;
import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * payment_record 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/paymentRecords")
public class PaymentRecordController {

    private final IPaymentRecordService paymentRecordService;

    public PaymentRecordController(IPaymentRecordService paymentRecordService) {
        this.paymentRecordService = paymentRecordService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<PaymentRecordEntity>> list(PaymentRecordEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(paymentRecordService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<PaymentRecordEntity> getById(@PathVariable Long id) {
        return Result.success(paymentRecordService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody PaymentRecordEntity entity) {
        paymentRecordService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PaymentRecordEntity entity) {
        entity.setId(id);
        paymentRecordService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        paymentRecordService.remove(id);
        return Result.success();
    }
}
