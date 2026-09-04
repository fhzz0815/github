package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.ITableTypeService;
import com.iwe3.sec.entity.TableTypeEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * table_type 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/tableTypes")
public class TableTypeController {

    private final ITableTypeService tableTypeService;

    public TableTypeController(ITableTypeService tableTypeService) {
        this.tableTypeService = tableTypeService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<TableTypeEntity>> list(TableTypeEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(tableTypeService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<TableTypeEntity> getById(@PathVariable Long id) {
        return Result.success(tableTypeService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody TableTypeEntity entity) {
        tableTypeService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody TableTypeEntity entity) {
        entity.setId(id);
        tableTypeService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        tableTypeService.remove(id);
        return Result.success();
    }
}
