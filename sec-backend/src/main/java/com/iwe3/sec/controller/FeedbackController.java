package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IFeedbackService;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * feedback 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<FeedbackEntity>> list(FeedbackEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(feedbackService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<FeedbackEntity> getById(@PathVariable Long id) {
        return Result.success(feedbackService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody FeedbackEntity entity) {
        feedbackService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FeedbackEntity entity) {
        entity.setId(id);
        feedbackService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        feedbackService.remove(id);
        return Result.success();
    }
}
