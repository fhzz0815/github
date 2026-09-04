package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IQueueService;
import com.iwe3.sec.entity.QueueEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * queue 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/queues")
public class QueueController {

    private final IQueueService queueService;

    public QueueController(IQueueService queueService) {
        this.queueService = queueService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<QueueEntity>> list(QueueEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(queueService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<QueueEntity> getById(@PathVariable Long id) {
        return Result.success(queueService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody QueueEntity entity) {
        queueService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody QueueEntity entity) {
        entity.setId(id);
        queueService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        queueService.remove(id);
        return Result.success();
    }
}
