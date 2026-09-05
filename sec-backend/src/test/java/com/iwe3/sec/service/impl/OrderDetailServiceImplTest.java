package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.mapper.OrderDetailMapper;
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
 * Unit test for OrderDetail ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDetailMapper orderDetailMapper;

    private OrderDetailServiceImpl orderDetailService;

    @BeforeEach
    void setUp() {
        orderDetailService = new OrderDetailServiceImpl(orderDetailMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        OrderDetailEntity entity1 = OrderDetailEntity.builder().id(1L).build();
        OrderDetailEntity entity2 = OrderDetailEntity.builder().id(2L).build();
        List<OrderDetailEntity> mockList = Arrays.asList(entity1, entity2);
        when(orderDetailMapper.selectList(any())).thenReturn(mockList);

        PageResult<OrderDetailEntity> result = orderDetailService.list(new OrderDetailEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(orderDetailMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<OrderDetailEntity> result = orderDetailService.list(new OrderDetailEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        OrderDetailEntity mockEntity = OrderDetailEntity.builder().id(1L).build();
        when(orderDetailMapper.selectById(1L)).thenReturn(mockEntity);

        OrderDetailEntity result = orderDetailService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(orderDetailMapper.selectById(999L)).thenReturn(null);

        OrderDetailEntity result = orderDetailService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        OrderDetailEntity entity = OrderDetailEntity.builder().build();
        when(orderDetailMapper.insert(entity)).thenReturn(1);

        boolean result = orderDetailService.add(entity);

        assertTrue(result);
        verify(orderDetailMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        OrderDetailEntity entity = OrderDetailEntity.builder().build();
        when(orderDetailMapper.insert(entity)).thenReturn(0);

        boolean result = orderDetailService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        OrderDetailEntity entity = OrderDetailEntity.builder().id(1L).build();
        when(orderDetailMapper.update(entity)).thenReturn(1);

        boolean result = orderDetailService.update(entity);

        assertTrue(result);
        verify(orderDetailMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        OrderDetailEntity entity = OrderDetailEntity.builder().id(1L).build();
        when(orderDetailMapper.update(entity)).thenReturn(0);

        boolean result = orderDetailService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(orderDetailMapper.deleteById(1L)).thenReturn(1);

        boolean result = orderDetailService.remove(1L);

        assertTrue(result);
        verify(orderDetailMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(orderDetailMapper.deleteById(1L)).thenReturn(0);

        boolean result = orderDetailService.remove(1L);

        assertFalse(result);
    }
}