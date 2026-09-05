package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.service.IOrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for OrderDetail Controller
 */
@ExtendWith(MockitoExtension.class)
class OrderDetailControllerTest {

    @Mock
    private IOrderDetailService orderDetailService;

    private OrderDetailController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderDetailController(orderDetailService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        OrderDetailEntity entity1 = OrderDetailEntity.builder().id(1L).build();
        List<OrderDetailEntity> entityList = Arrays.asList(entity1);
        PageResult<OrderDetailEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(orderDetailService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<OrderDetailEntity>> result = controller.list(new OrderDetailEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(orderDetailService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        OrderDetailEntity mockEntity = OrderDetailEntity.builder().id(1L).build();
        when(orderDetailService.getById(1L)).thenReturn(mockEntity);

        Result<OrderDetailEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(orderDetailService.getById(999L)).thenReturn(null);

        Result<OrderDetailEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        OrderDetailEntity entity = OrderDetailEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(orderDetailService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        OrderDetailEntity entity = OrderDetailEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(orderDetailService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(orderDetailService).remove(1L);
    }
}