package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IMemberCategoryService;
import com.iwe3.sec.entity.MemberCategoryEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * member_category 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/memberCategories")
public class MemberCategoryController {

    private final IMemberCategoryService memberCategoryService;

    public MemberCategoryController(IMemberCategoryService memberCategoryService) {
        this.memberCategoryService = memberCategoryService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<MemberCategoryEntity>> list(MemberCategoryEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(memberCategoryService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<MemberCategoryEntity> getById(@PathVariable Long id) {
        return Result.success(memberCategoryService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody MemberCategoryEntity entity) {
        memberCategoryService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody MemberCategoryEntity entity) {
        entity.setId(id);
        memberCategoryService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        memberCategoryService.remove(id);
        return Result.success();
    }
}
