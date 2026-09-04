package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStorePaymentSettingService;
import com.iwe3.sec.entity.StorePaymentSettingEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * store_payment_setting 表的表现层控制器
 */
@RestController
@RequestMapping("/api/v1/storePaymentSettings")
public class StorePaymentSettingController {

    private final IStorePaymentSettingService storePaymentSettingService;

    public StorePaymentSettingController(IStorePaymentSettingService storePaymentSettingService) {
        this.storePaymentSettingService = storePaymentSettingService;
    }

    /** 分页查询列表 */
    @GetMapping
    public Result<PageResult<StorePaymentSettingEntity>> list(StorePaymentSettingEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(storePaymentSettingService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<StorePaymentSettingEntity> getById(@PathVariable Long id) {
        return Result.success(storePaymentSettingService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody StorePaymentSettingEntity entity) {
        storePaymentSettingService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StorePaymentSettingEntity entity) {
        entity.setId(id);
        storePaymentSettingService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        storePaymentSettingService.remove(id);
        return Result.success();
    }
}
