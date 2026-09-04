package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IDiningTableService;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * dining_table 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/diningTables")
public class DiningTableController {

    private final IDiningTableService diningTableService;

    public DiningTableController(IDiningTableService diningTableService) {
        this.diningTableService = diningTableService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<DiningTableEntity>> list(DiningTableEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(diningTableService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<DiningTableEntity> getById(@PathVariable Long id) {
        return Result.success(diningTableService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody DiningTableEntity entity) {
        diningTableService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DiningTableEntity entity) {
        entity.setId(id);
        diningTableService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        diningTableService.remove(id);
        return Result.success();
    }
}
