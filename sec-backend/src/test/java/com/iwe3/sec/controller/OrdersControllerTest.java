package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.dto.CancelOrderRequest;
import com.iwe3.sec.dto.OrderWithDetailsVO;
import com.iwe3.sec.dto.PayOrderRequest;
import com.iwe3.sec.dto.SubmitOrderRequest;
import com.iwe3.sec.dto.UpdateMakeStatusRequest;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.service.IOrdersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单控制器单元测试
 * 使用 Mockito 模拟 Service 层，只测试 Controller 的请求分发和参数处理
 */
@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock
    private IOrdersService ordersService;

    @Mock
    private PermissionChecker permissionChecker;

    private OrdersController controller;

    @BeforeEach
    void setUp() {
        controller = new OrdersController(ordersService, permissionChecker);
    }

    // ========== 基础 CRUD 测试 ==========

    @Test
    @DisplayName("分页查询应返回分页结果")
    void testList_ShouldReturnPageResult() {
        OrdersEntity entity1 = OrdersEntity.builder().id(1L).build();
        List<OrdersEntity> entityList = Arrays.asList(entity1);
        PageResult<OrdersEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(ordersService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<OrdersEntity>> result = controller.list(new OrdersEntity(), 1, 10);

        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(ordersService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("根据ID查询-存在时返回实体")
    void testGetById_WhenExists_ShouldReturnEntity() {
        OrdersEntity mockEntity = OrdersEntity.builder().id(1L).build();
        when(ordersService.getById(1L)).thenReturn(mockEntity);

        Result<OrdersEntity> result = controller.getById(1L);

        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("根据ID查询-不存在时返回空数据")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(ordersService.getById(999L)).thenReturn(null);

        Result<OrdersEntity> result = controller.getById(999L);

        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("新增应调用Service")
    void testAdd_ShouldCallService() {
        OrdersEntity entity = OrdersEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertEquals(0, result.getCode());
        verify(ordersService).add(entity);
    }

    @Test
    @DisplayName("修改应设置ID并调用Service")
    void testUpdate_ShouldSetIdAndCallService() {
        OrdersEntity entity = OrdersEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(ordersService).update(entity);
    }

    @Test
    @DisplayName("删除应调用Service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertEquals(0, result.getCode());
        verify(ordersService).remove(1L);
    }

    // ========== 业务接口测试 ==========

    @Test
    @DisplayName("下单应调用Service并返回订单ID")
    void testSubmitOrder_ShouldReturnOrderId() {
        // 准备测试数据
        OrdersEntity order = OrdersEntity.builder().build();
        List<OrderDetailEntity> details = Arrays.asList(
                OrderDetailEntity.builder().dishId(1L).quantity(2).dishPrice(new BigDecimal("29.90")).build()
        );
        SubmitOrderRequest request = new SubmitOrderRequest();
        request.setOrder(order);
        request.setDetails(details);

        when(permissionChecker.currentUserId()).thenReturn(100L);
        when(ordersService.submitOrder(order, details, 100L)).thenReturn(1L);

        // 执行
        Result<Long> result = controller.submitOrder(request);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals("下单成功", result.getMessage());
        assertEquals(1L, result.getData());
        verify(ordersService).submitOrder(order, details, 100L);
    }

    @Test
    @DisplayName("支付成功应返回成功消息")
    void testPayOrder_ShouldReturnSuccess() {
        // 准备
        PayOrderRequest request = new PayOrderRequest();
        request.setPayType("WECHAT");
        request.setActualAmount(new BigDecimal("59.80"));
        request.setMemberPayAmount(BigDecimal.ZERO);
        request.setIdempotencyKey("TEST_IDEMPOTENCY_KEY_001");

        when(permissionChecker.currentUserId()).thenReturn(100L);

        // 执行
        Result<Void> result = controller.payOrder(1L, request);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals("支付成功", result.getMessage());
        verify(ordersService).payOrder(1L, "WECHAT", new BigDecimal("59.80"), BigDecimal.ZERO, "TEST_IDEMPOTENCY_KEY_001", 100L);
    }

    @Test
    @DisplayName("取消订单应调用Service")
    void testCancelOrder_ShouldCallService() {
        // 准备
        CancelOrderRequest request = new CancelOrderRequest();
        request.setReason("顾客取消");

        when(permissionChecker.currentUserId()).thenReturn(100L);

        // 执行
        Result<Void> result = controller.cancelOrder(1L, request);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals("订单已取消", result.getMessage());
        verify(ordersService).cancelOrder(1L, "顾客取消", 100L);
    }

    @Test
    @DisplayName("更新制作状态应调用Service")
    void testUpdateMakeStatus_ShouldCallService() {
        // 准备
        UpdateMakeStatusRequest request = new UpdateMakeStatusRequest();
        request.setDetailId(1L);
        request.setMakeStatus(2); // 制作中

        when(permissionChecker.currentUserId()).thenReturn(100L);

        // 执行
        Result<Void> result = controller.updateMakeStatus(1L, request);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals("制作状态已更新", result.getMessage());
        verify(ordersService).updateMakeStatus(1L, 1L, 2, 100L);
    }

    @Test
    @DisplayName("查询订单详情含明细应返回聚合视图")
    void testGetOrderWithDetails_ShouldReturnAggregatedVO() {
        // 准备
        OrdersEntity order = OrdersEntity.builder().id(1L).orderNo("SO2026090912345").build();
        List<OrderDetailEntity> details = Arrays.asList(
                OrderDetailEntity.builder().id(1L).dishName("宫保鸡丁").build()
        );
        List<OrderStatusLogEntity> logs = Arrays.asList(
                OrderStatusLogEntity.builder().id(1L).remark("下单成功").build()
        );

        when(ordersService.getOrderWithDetails(1L)).thenReturn(order);
        when(ordersService.getOrderDetailsByOrderId(1L)).thenReturn(details);
        when(ordersService.getOrderStatusLogs(1L)).thenReturn(logs);

        // 执行
        Result<OrderWithDetailsVO> result = controller.getOrderWithDetails(1L);

        // 验证
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("SO2026090912345", result.getData().getOrder().getOrderNo());
        assertEquals(1, result.getData().getDetails().size());
        assertEquals(1, result.getData().getStatusLogs().size());
    }

    @Test
    @DisplayName("查询订单详情-订单不存在时返回错误")
    void testGetOrderWithDetails_WhenOrderNotExists_ShouldReturnError() {
        // 准备
        when(ordersService.getOrderWithDetails(999L)).thenReturn(null);

        // 执行
        Result<OrderWithDetailsVO> result = controller.getOrderWithDetails(999L);

        // 验证
        assertEquals(500, result.getCode());
        assertEquals("订单不存在", result.getMessage());
        // 不调用明细查询
        verify(ordersService, never()).getOrderDetailsByOrderId(any());
        verify(ordersService, never()).getOrderStatusLogs(any());
    }

    @Test
    @DisplayName("查询状态日志应返回列表")
    void testGetStatusLogs_ShouldReturnList() {
        // 准备
        List<OrderStatusLogEntity> logs = Collections.emptyList();
        when(ordersService.getOrderStatusLogs(1L)).thenReturn(logs);

        // 执行
        Result<List<OrderStatusLogEntity>> result = controller.getStatusLogs(1L);

        // 验证
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        verify(ordersService).getOrderStatusLogs(1L);
    }

    @Test
    @DisplayName("查询后厨制作单应返回列表")
    void testGetKitchenOrders_ShouldReturnList() {
        // 准备
        List<OrdersEntity> orders = Arrays.asList(OrdersEntity.builder().id(1L).build());
        when(ordersService.getKitchenOrders(1L)).thenReturn(orders);

        // 执行
        Result<List<OrdersEntity>> result = controller.getKitchenOrders(1L);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals(1, result.getData().size());
        verify(ordersService).getKitchenOrders(1L);
    }

    @Test
    @DisplayName("查询销售报表应返回统计数据")
    void testGetSalesReport_ShouldReturnMap() {
        // 准备
        when(ordersService.getSalesReport(1L, "2026-09-01", "2026-09-09"))
                .thenReturn(Collections.singletonMap("totalOrders", 10));

        // 执行
        Result<Map<String, Object>> result = controller.getSalesReport(1L, "2026-09-01", "2026-09-09");

        // 验证
        assertEquals(0, result.getCode());
        assertEquals(10, result.getData().get("totalOrders"));
        verify(ordersService).getSalesReport(1L, "2026-09-01", "2026-09-09");
    }

    @Test
    @DisplayName("查询今日概况应返回仪表盘数据")
    void testGetTodaySummary_ShouldReturnMap() {
        // 准备
        when(ordersService.getTodaySummary(1L))
                .thenReturn(Collections.singletonMap("todayOrderCount", 5));

        // 执行
        Result<Map<String, Object>> result = controller.getTodaySummary(1L);

        // 验证
        assertEquals(0, result.getCode());
        assertEquals(5, result.getData().get("todayOrderCount"));
        verify(ordersService).getTodaySummary(1L);
    }
}
