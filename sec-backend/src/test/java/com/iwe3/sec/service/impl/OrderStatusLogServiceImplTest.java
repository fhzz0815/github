package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import com.iwe3.sec.mapper.OrderStatusLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for OrderStatusLog ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class OrderStatusLogServiceImplTest {

    @Mock
    private OrderStatusLogMapper orderStatusLogMapper;

    private OrderStatusLogServiceImpl orderStatusLogService;

    @BeforeEach
    void setUp() {
        orderStatusLogService = new OrderStatusLogServiceImpl(orderStatusLogMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        OrderStatusLogEntity entity1 = OrderStatusLogEntity.builder().id(1L).build();
        OrderStatusLogEntity entity2 = OrderStatusLogEntity.builder().id(2L).build();
        List<OrderStatusLogEntity> mockList = Arrays.asList(entity1, entity2);
        when(orderStatusLogMapper.selectList(any())).thenReturn(mockList);

        PageResult<OrderStatusLogEntity> result = orderStatusLogService.list(new OrderStatusLogEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(orderStatusLogMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<OrderStatusLogEntity> result = orderStatusLogService.list(new OrderStatusLogEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        OrderStatusLogEntity mockEntity = OrderStatusLogEntity.builder().id(1L).build();
        when(orderStatusLogMapper.selectById(1L)).thenReturn(mockEntity);

        OrderStatusLogEntity result = orderStatusLogService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(orderStatusLogMapper.selectById(999L)).thenReturn(null);

        OrderStatusLogEntity result = orderStatusLogService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        OrderStatusLogEntity entity = OrderStatusLogEntity.builder().build();
        when(orderStatusLogMapper.insert(entity)).thenReturn(1);

        boolean result = orderStatusLogService.add(entity);

        assertTrue(result);
        verify(orderStatusLogMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        OrderStatusLogEntity entity = OrderStatusLogEntity.builder().build();
        when(orderStatusLogMapper.insert(entity)).thenReturn(0);

        boolean result = orderStatusLogService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        OrderStatusLogEntity entity = OrderStatusLogEntity.builder().id(1L).build();
        when(orderStatusLogMapper.update(entity)).thenReturn(1);

        boolean result = orderStatusLogService.update(entity);

        assertTrue(result);
        verify(orderStatusLogMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        OrderStatusLogEntity entity = OrderStatusLogEntity.builder().id(1L).build();
        when(orderStatusLogMapper.update(entity)).thenReturn(0);

        boolean result = orderStatusLogService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(orderStatusLogMapper.deleteById(1L)).thenReturn(1);

        boolean result = orderStatusLogService.remove(1L);

        assertTrue(result);
        verify(orderStatusLogMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(orderStatusLogMapper.deleteById(1L)).thenReturn(0);

        boolean result = orderStatusLogService.remove(1L);

        assertFalse(result);
    }
}