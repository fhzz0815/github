package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStoreService;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * store 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final IStoreService storeService;

    public StoreController(IStoreService storeService) {
        this.storeService = storeService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<StoreEntity>> list(StoreEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(storeService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<StoreEntity> getById(@PathVariable Long id) {
        return Result.success(storeService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody StoreEntity entity) {
        storeService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StoreEntity entity) {
        entity.setId(id);
        storeService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        storeService.remove(id);
        return Result.success();
    }
}
