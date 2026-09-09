package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberCategoryService;
import com.iwe3.sec.entity.MemberCategoryEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_category 表的表现层控制器
 */
@Tag(name = "会员分类", description = "会员分类的增删改查")
@RestController
@RequestMapping("/api/v1/memberCategories")
public class MemberCategoryController {

    private final IMemberCategoryService memberCategoryService;

    public MemberCategoryController(IMemberCategoryService memberCategoryService) {
        this.memberCategoryService = memberCategoryService;
    }

    @Operation(summary = "分页查询会员分类管理列表")
    @GetMapping
    public Result<PageResult<MemberCategoryEntity>> list(MemberCategoryEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberCategoryService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询会员分类管理详情")
    @GetMapping("/{id}")
    public Result<MemberCategoryEntity> getById(@PathVariable Long id) {
        return Result.success(memberCategoryService.getById(id));
    }

    @Operation(summary = "新增会员分类管理")
    @PostMapping
    public Result<Void> add(@RequestBody MemberCategoryEntity entity) {
        memberCategoryService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改会员分类管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberCategoryEntity entity) {
        entity.setId(id);
        memberCategoryService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除会员分类管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberCategoryService.remove(id);
        return Result.success();
    }
}
