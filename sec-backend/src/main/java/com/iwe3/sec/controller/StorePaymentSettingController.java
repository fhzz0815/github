package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.service.IStorePaymentSettingService;
import com.iwe3.sec.entity.StorePaymentSettingEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

/**
 * store_payment_setting 表的表现层控制器
 */
@Tag(name = "门店支付设置", description = "门店支付设置的增删改查")
@RestController
@RequestMapping("/api/v1/storePaymentSettings")
public class StorePaymentSettingController {

    private final IStorePaymentSettingService storePaymentSettingService;

    public StorePaymentSettingController(IStorePaymentSettingService storePaymentSettingService) {
        this.storePaymentSettingService = storePaymentSettingService;
    }

    @Operation(summary = "分页查询门店支付设置管理列表")
    @GetMapping
    public Result<PageResult<StorePaymentSettingEntity>> list(StorePaymentSettingEntity query,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(storePaymentSettingService.list(query, page, size));
    }

    @Operation(summary = "根据ID查询门店支付设置管理详情")
    @GetMapping("/{id}")
    public Result<StorePaymentSettingEntity> getById(@PathVariable Long id) {
        return Result.success(storePaymentSettingService.getById(id));
    }

    @Operation(summary = "新增门店支付设置管理")
    @PostMapping
    public Result<Void> add(@RequestBody StorePaymentSettingEntity entity) {
        storePaymentSettingService.add(entity);
        return Result.success();
    }

    @Operation(summary = "修改门店支付设置管理")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody StorePaymentSettingEntity entity) {
        entity.setId(id);
        storePaymentSettingService.update(entity);
        return Result.success();
    }

    @Operation(summary = "删除门店支付设置管理")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        storePaymentSettingService.remove(id);
        return Result.success();
    }
}
