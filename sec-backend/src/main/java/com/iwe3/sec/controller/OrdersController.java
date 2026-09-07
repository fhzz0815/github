package com.iwe3.sec.controller;

import org.springframework.web.bind.annotation.*;
import com.iwe3.sec.common.PermissionChecker;
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
    @GetMapping
    public Result<PageResult<OrdersEntity>> list(OrdersEntity query,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(ordersService.list(query, page, size));
    }

    /** 根据ID查询详情 */
    @GetMapping("/{id}")
    public Result<OrdersEntity> getById(@PathVariable Long id) {
        return Result.success(ordersService.getById(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Void> add(@RequestBody OrdersEntity entity) {
        ordersService.add(entity);
        return Result.success();
    }

    /** 修改 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody OrdersEntity entity) {
        entity.setId(id);
        ordersService.update(entity);
        return Result.success();
    }

    /** 删除 */
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
    @PostMapping("/submit")
    public Result<Long> submitOrder(@RequestBody SubmitOrderRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        Long orderId = ordersService.submitOrder(request.getOrder(), request.getDetails(), operatorId);
        return Result.success("下单成功", orderId);
    }

    /**
     * 支付订单
     * @param request 支付参数
     */
    @PostMapping("/{id}/pay")
    public Result<Void> payOrder(@PathVariable Long id, @RequestBody PayOrderRequest request) {
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
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, @RequestBody CancelOrderRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        ordersService.cancelOrder(id, request.getReason(), operatorId);
        return Result.<Void>success("订单已取消", null);
    }

    /**
     * 更新菜品制作状态
     * @param id 订单ID
     * @param request 制作状态参数
     */
    @PostMapping("/{id}/make-status")
    public Result<Void> updateMakeStatus(@PathVariable Long id,
                                         @RequestBody UpdateMakeStatusRequest request) {
        Long operatorId = permissionChecker.currentUserId();
        ordersService.updateMakeStatus(id, request.getDetailId(), request.getMakeStatus(), operatorId);
        return Result.<Void>success("制作状态已更新", null);
    }

    /**
     * 查询订单详情（含明细）
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/{id}/with-details")
    public Result<OrderWithDetailsVO> getOrderWithDetails(@PathVariable Long id) {
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
    @GetMapping("/{id}/status-logs")
    public Result<List<OrderStatusLogEntity>> getStatusLogs(@PathVariable Long id) {
        return Result.success(ordersService.getOrderStatusLogs(id));
    }

    /**
     * 查询后厨制作单（当前门店待制作的订单）
     * @param storeId 门店ID，不传则自动取当前门店
     * @return 订单列表
     */
    @GetMapping("/kitchen")
    public Result<List<OrdersEntity>> getKitchenOrders(
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
    @GetMapping("/report/sales")
    public Result<Map<String, Object>> getSalesReport(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(ordersService.getSalesReport(storeId, beginDate, endDate));
    }

    /**
     * 查询今日概况（订单数、收入、待处理数）
     * @param storeId 门店ID
     * @return 今日概况数据
     */
    @GetMapping("/report/today")
    public Result<Map<String, Object>> getTodaySummary(
            @RequestParam(required = false) Long storeId) {
        return Result.success(ordersService.getTodaySummary(storeId));
    }

    // ========== 内部请求类 ==========

    /** 下单请求 */
    public static class SubmitOrderRequest {
        private OrdersEntity order;
        private List<OrderDetailEntity> details;

        public OrdersEntity getOrder() { return order; }
        public void setOrder(OrdersEntity order) { this.order = order; }
        public List<OrderDetailEntity> getDetails() { return details; }
        public void setDetails(List<OrderDetailEntity> details) { this.details = details; }
    }

    /** 支付请求 */
    public static class PayOrderRequest {
        private String payType;
        private java.math.BigDecimal actualAmount;
        private java.math.BigDecimal memberPayAmount;

        public String getPayType() { return payType; }
        public void setPayType(String payType) { this.payType = payType; }
        public java.math.BigDecimal getActualAmount() { return actualAmount; }
        public void setActualAmount(java.math.BigDecimal actualAmount) { this.actualAmount = actualAmount; }
        public java.math.BigDecimal getMemberPayAmount() { return memberPayAmount; }
        public void setMemberPayAmount(java.math.BigDecimal memberPayAmount) { this.memberPayAmount = memberPayAmount; }
    }

    /** 取消订单请求 */
    public static class CancelOrderRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    /** 更新制作状态请求 */
    public static class UpdateMakeStatusRequest {
        private Long detailId;
        private Integer makeStatus;

        public Long getDetailId() { return detailId; }
        public void setDetailId(Long detailId) { this.detailId = detailId; }
        public Integer getMakeStatus() { return makeStatus; }
        public void setMakeStatus(Integer makeStatus) { this.makeStatus = makeStatus; }
    }

    /** 订单详情 + 明细 + 状态日志的聚合视图 */
    public static class OrderWithDetailsVO {
        private OrdersEntity order;
        private List<OrderDetailEntity> details;
        private List<OrderStatusLogEntity> statusLogs;

        public OrderWithDetailsVO(OrdersEntity order, List<OrderDetailEntity> details,
                                  List<OrderStatusLogEntity> statusLogs) {
            this.order = order;
            this.details = details;
            this.statusLogs = statusLogs;
        }

        public OrdersEntity getOrder() { return order; }
        public List<OrderDetailEntity> getDetails() { return details; }
        public List<OrderStatusLogEntity> getStatusLogs() { return statusLogs; }
    }
}
