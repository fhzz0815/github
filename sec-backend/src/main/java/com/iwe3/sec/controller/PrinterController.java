package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IPrinterService;
import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * printer 表的表现层控制器
 */
@Tag(name = "打印机管理", description = "打印机管理的增删改查")
@RestController
@RequestMapping("/api/v1/printers")
public class PrinterController {

    private final IPrinterService printerService;

    public PrinterController(IPrinterService printerService) {
        this.printerService = printerService;
    }

    @Operation(summary = "分页查询打印机管理列表")
    @GetMapping
    public Result<PageResult<PrinterEntity>> list(PrinterEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(printerService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询打印机管理详情")
    @GetMapping("/{id}")
    public Result<PrinterEntity> getById(@PathVariable Long id) {
        return Result.success(printerService.getById(id));
    }

    @Operation(summary = "新增打印机管理")
    @PostMapping
    public Result<Void> add(@RequestBody PrinterEntity entity) {
        printerService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改打印机管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PrinterEntity entity) {
        entity.setId(id);
        printerService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除打印机管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        printerService.remove(id);
        return Result.success();
    }
}
