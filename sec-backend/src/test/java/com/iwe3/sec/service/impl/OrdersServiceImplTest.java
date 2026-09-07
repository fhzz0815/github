package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.mapper.OrderStatusLogMapper;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.mapper.PaymentRecordMapper;
import com.iwe3.sec.mapper.DishStockMapper;
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
 * Unit test for Orders ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class OrdersServiceImplTest {

    @Mock
    private OrdersMapper ordersMapper;

    @Mock
    private OrderDetailMapper orderDetailMapper;

    @Mock
    private OrderStatusLogMapper orderStatusLogMapper;

    @Mock
    private PaymentRecordMapper paymentRecordMapper;

    @Mock
    private DishStockMapper dishStockMapper;

    @Mock
    private PermissionChecker permissionChecker;

    private OrdersServiceImpl ordersService;

    @BeforeEach
    void setUp() {
        ordersService = new OrdersServiceImpl(ordersMapper, orderDetailMapper, orderStatusLogMapper,
                paymentRecordMapper, dishStockMapper, permissionChecker);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        OrdersEntity entity1 = OrdersEntity.builder().id(1L).build();
        OrdersEntity entity2 = OrdersEntity.builder().id(2L).build();
        List<OrdersEntity> mockList = Arrays.asList(entity1, entity2);
        when(ordersMapper.selectList(any())).thenReturn(mockList);

        PageResult<OrdersEntity> result = ordersService.list(new OrdersEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(ordersMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<OrdersEntity> result = ordersService.list(new OrdersEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        when(permissionChecker.currentRoleLevel()).thenReturn(99);
        OrdersEntity mockEntity = OrdersEntity.builder().id(1L).build();
        when(ordersMapper.selectById(1L)).thenReturn(mockEntity);

        OrdersEntity result = ordersService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(ordersMapper.selectById(999L)).thenReturn(null);

        OrdersEntity result = ordersService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        OrdersEntity entity = OrdersEntity.builder().build();
        when(ordersMapper.insert(entity)).thenReturn(1);

        boolean result = ordersService.add(entity);

        assertTrue(result);
        verify(ordersMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        OrdersEntity entity = OrdersEntity.builder().build();
        when(ordersMapper.insert(entity)).thenReturn(0);

        boolean result = ordersService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        OrdersEntity entity = OrdersEntity.builder().id(1L).build();
        when(ordersMapper.update(entity)).thenReturn(1);

        boolean result = ordersService.update(entity);

        assertTrue(result);
        verify(ordersMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        OrdersEntity entity = OrdersEntity.builder().id(1L).build();
        when(ordersMapper.update(entity)).thenReturn(0);

        boolean result = ordersService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(ordersMapper.deleteById(1L)).thenReturn(1);

        boolean result = ordersService.remove(1L);

        assertTrue(result);
        verify(ordersMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(ordersMapper.deleteById(1L)).thenReturn(0);

        boolean result = ordersService.remove(1L);

        assertFalse(result);
    }
}
