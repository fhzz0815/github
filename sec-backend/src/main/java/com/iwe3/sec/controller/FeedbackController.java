package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IFeedbackService;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * feedback 表的表现层控制器
 */
@Tag(name = "意见反馈", description = "意见反馈的增删改查")
@RestController
@RequestMapping("/api/v1/feedbacks")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "分页查询意见反馈管理列表")
    @GetMapping
    public Result<PageResult<FeedbackEntity>> list(FeedbackEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(feedbackService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询意见反馈管理详情")
    @GetMapping("/{id}")
    public Result<FeedbackEntity> getById(@PathVariable Long id) {
        return Result.success(feedbackService.getById(id));
    }

    @Operation(summary = "新增意见反馈管理")
    @PostMapping
    public Result<Void> add(@RequestBody FeedbackEntity entity) {
        feedbackService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改意见反馈管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FeedbackEntity entity) {
        entity.setId(id);
        feedbackService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除意见反馈管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        feedbackService.remove(id);
        return Result.success();
    }
}
