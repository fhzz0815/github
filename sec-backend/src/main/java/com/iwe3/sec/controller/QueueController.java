package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IQueueService;
import com.iwe3.sec.entity.QueueEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * queue 表的表现层控制器
 */
@Tag(name = "排队管理", description = "排队管理的增删改查")
@RestController
@RequestMapping("/api/v1/queues")
public class QueueController {

    private final IQueueService queueService;

    public QueueController(IQueueService queueService) {
        this.queueService = queueService;
    }

    @Operation(summary = "分页查询排队管理列表")
    @GetMapping
    public Result<PageResult<QueueEntity>> list(QueueEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(queueService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询排队管理详情")
    @GetMapping("/{id}")
    public Result<QueueEntity> getById(@PathVariable Long id) {
        return Result.success(queueService.getById(id));
    }

    @Operation(summary = "新增排队管理")
    @PostMapping
    public Result<Void> add(@RequestBody QueueEntity entity) {
        queueService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改排队管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody QueueEntity entity) {
        entity.setId(id);
        queueService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除排队管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        queueService.remove(id);
        return Result.success();
    }
}
