package com.iwe3.sec.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.dto.CancelOrderRequest;
import com.iwe3.sec.dto.OrderWithDetailsVO;
import com.iwe3.sec.dto.PayOrderRequest;
import com.iwe3.sec.dto.SubmitOrderRequest;
import com.iwe3.sec.dto.UpdateMakeStatusRequest;
import com.iwe3.sec.service.IOrdersService;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.common.PageResult;

import java.util.List;
import java.util.Map;

/**
 * orders 表的表现层控制器
 * 处理订单的增删改查以及业务操作（下单、支付、取消、制作状态更新等）
 */
@Tag(name = "订单管理", description = "订单的增删改查与业务操作（下单、支付、取消、制作）")
@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    private final IOrdersService ordersService;
    private final PermissionChecker permissionChecker;

    public OrdersController(IOrdersService ordersService,
                            PermissionChecker permissionChecker) {
        this.ordersService = ordersService;
        this.permissionChecker = permissionChecker;
    }

    // ========== 基础 CRUD ==========

    /** 分页查询列表 */
    @Operation(summary = "分页查询订单列表", description = "支持按订单号、门店、状态等条件筛选，支持时间范围搜索")
    @GetMapping
    public Result<PageResult<OrdersEntity>> list(@Valid OrdersEntity query,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ordersService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @Operation(summary = "根据ID查询订单详情")
    @GetMapping("/{id}")
    public Result<OrdersEntity> getById(@Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(ordersService.getById(id));
    }

    /** 新增 */
    @Operation(summary = "新增订单")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody OrdersEntity entity) {
        ordersService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @Operation(summary = "修改订单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody OrdersEntity entity) {
        entity.setId(id);
        ordersService.update(entity);
        return Result.success();
    }

    /** 删除 */
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        ordersService.remove(id);
        return Result.success();
    }

    // ========== 业务接口 ==========

    /**
     * 提交订单（下单）
     * @param request 包含订单信息和菜品明细
     * @return 订单ID
     */
    @Operation(summary = "提交订单（下单）", description = "创建订单并扣减库存，返回订单ID")
    @PostMapping("/submit")
    public Result<Long> submitOrder(@Valid @RequestBody SubmitOrderRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        Long orderId = ordersService.submitOrder(request.getOrder(), request.getDetails(), operatorId);
        return Result.success("下单成功", orderId);
    }

    /**
     * 支付订单
     * @param request 支付参数
     */
    @Operation(summary = "支付订单", description = "支持微信、支付宝、会员余额、现金等支付方式")
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@Parameter(description = "订单ID") @PathVariable Long id,
                                 @Valid @RequestBody PayOrderRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        ordersService.payOrder(id, request.getPayType(), request.getActualAmount(),
                request.getMemberPayAmount(), operatorId);
        return Result.<Void>success("支付成功", null);
    }

    /**
     * 取消订单
     * @param id 订单ID
     * @param request 取消原因
     */
    @Operation(summary = "取消订单", description = "取消指定订单，需填写取消原因")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@Parameter(description = "订单ID") @PathVariable Long id,
                                    @Valid @RequestBody CancelOrderRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        ordersService.cancelOrder(id, request.getReason(), operatorId);
        return Result.<Void>success("订单已取消", null);
    }

    /**
     * 更新菜品制作状态
     * @param id 订单ID
     * @param request 制作状态参数
     */
    @Operation(summary = "更新菜品制作状态", description = "更新单个菜品或整单的制作状态（待制作→制作中→已上齐）")
    @PostMapping("/{id}/make-status")
    public Result<Void> updateMakeStatus(@Parameter(description = "订单ID") @PathVariable Long id,
                                         @Valid @RequestBody UpdateMakeStatusRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        ordersService.updateMakeStatus(id, request.getDetailId(), request.getMakeStatus(), operatorId);
        return Result.<Void>success("制作状态已更新", null);
    }

    /**
     * 查询订单详情（含明细）
     * @param id 订单ID
     * @return 订单信息
     */
    @Operation(summary = "查询订单详情（含明细和状态日志）")
    @GetMapping("/{id}/with-details")
    public Result<OrderWithDetailsVO> getOrderWithDetails(@Parameter(description = "订单ID") @PathVariable Long id) {
        OrdersEntity order = ordersService.getOrderWithDetails(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderDetailEntity> details = ordersService.getOrderDetailsByOrderId(id);
        List<OrderStatusLogEntity> logs = ordersService.getOrderStatusLogs(id);
        OrderWithDetailsVO vo = new OrderWithDetailsVO(order, details, logs);
        return Result.success(vo);
    }

    /**
     * 查询订单状态日志
     * @param id 订单ID
     * @return 状态日志列表
     */
    @Operation(summary = "查询订单状态日志")
    @GetMapping("/{id}/status-logs")
    public Result<List<OrderStatusLogEntity>> getStatusLogs(@Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(ordersService.getOrderStatusLogs(id));
    }

    /**
     * 查询后厨制作单（当前门店待制作的订单）
     * @param storeId 门店ID，不传则自动取当前门店
     * @return 订单列表
     */
    @Operation(summary = "查询后厨制作单", description = "查询当前门店待制作的订单（状态为待制作/制作中/待配送等）")
    @GetMapping("/kitchen")
    public Result<List<OrdersEntity>> getKitchenOrders(
            @Parameter(description = "门店ID，不传则自动取当前门店")
            @RequestParam(required = false) Long storeId) {
        return Result.success(ordersService.getKitchenOrders(storeId));
    }

    /**
     * 查询销售报表（按天统计）
     * @param storeId   门店ID
     * @param beginDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @return 销售统计数据
     */
    @Operation(summary = "查询销售报表", description = "按日期范围统计销售额、订单数、折扣金额等")
    @GetMapping("/report/sales")
    public Result<Map<String, Object>> getSalesReport(
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId,
            @Parameter(description = "开始日期 yyyy-MM-dd") @RequestParam(required = false) String beginDate,
            @Parameter(description = "结束日期 yyyy-MM-dd") @RequestParam(required = false) String endDate) {
        return Result.success(ordersService.getSalesReport(storeId, beginDate, endDate));
    }

    /**
     * 查询今日概况（订单数、收入、待处理数）
     * @param storeId 门店ID
     * @return 今日概况数据
     */
    @Operation(summary = "查询今日概况", description = "今日订单数、今日收入、待处理订单数等仪表盘数据")
    @GetMapping("/report/today")
    public Result<Map<String, Object>> getTodaySummary(
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId) {
        return Result.success(ordersService.getTodaySummary(storeId));
    }
}
