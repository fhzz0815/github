package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IPrinterService;
import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * printer 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/printers")
public class PrinterController {

    private final IPrinterService printerService;

    public PrinterController(IPrinterService printerService) {
        this.printerService = printerService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<PrinterEntity>> list(PrinterEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(printerService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<PrinterEntity> getById(@PathVariable Long id) {
        return Result.success(printerService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody PrinterEntity entity) {
        printerService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PrinterEntity entity) {
        entity.setId(id);
        printerService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        printerService.remove(id);
        return Result.success();
    }
}
