package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IReceiptTemplateService;
import com.iwe3.sec.entity.ReceiptTemplateEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * receipt_template 表的表现层控制器
 */
@Tag(name = "小票模板", description = "小票模板的增删改查")
@RestController
@RequestMapping("/api/v1/receiptTemplates")
public class ReceiptTemplateController {

    private final IReceiptTemplateService receiptTemplateService;

    public ReceiptTemplateController(IReceiptTemplateService receiptTemplateService) {
        this.receiptTemplateService = receiptTemplateService;
    }

    @Operation(summary = "分页查询小票模板管理列表")
    @GetMapping
    public Result<PageResult<ReceiptTemplateEntity>> list(ReceiptTemplateEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(receiptTemplateService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询小票模板管理详情")
    @GetMapping("/{id}")
    public Result<ReceiptTemplateEntity> getById(@PathVariable Long id) {
        return Result.success(receiptTemplateService.getById(id));
    }

    @Operation(summary = "新增小票模板管理")
    @PostMapping
    public Result<Void> add(@RequestBody ReceiptTemplateEntity entity) {
        receiptTemplateService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改小票模板管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ReceiptTemplateEntity entity) {
        entity.setId(id);
        receiptTemplateService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除小票模板管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        receiptTemplateService.remove(id);
        return Result.success();
    }
}
