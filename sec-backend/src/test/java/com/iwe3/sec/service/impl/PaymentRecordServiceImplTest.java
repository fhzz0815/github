package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.mapper.PaymentRecordMapper;
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
 * Unit test for PaymentRecord ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class PaymentRecordServiceImplTest {

    @Mock
    private PaymentRecordMapper paymentRecordMapper;

    private PaymentRecordServiceImpl paymentRecordService;

    @BeforeEach
    void setUp() {
        paymentRecordService = new PaymentRecordServiceImpl(paymentRecordMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        PaymentRecordEntity entity1 = PaymentRecordEntity.builder().id(1L).build();
        PaymentRecordEntity entity2 = PaymentRecordEntity.builder().id(2L).build();
        List<PaymentRecordEntity> mockList = Arrays.asList(entity1, entity2);
        when(paymentRecordMapper.selectList(any())).thenReturn(mockList);

        PageResult<PaymentRecordEntity> result = paymentRecordService.list(new PaymentRecordEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(paymentRecordMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<PaymentRecordEntity> result = paymentRecordService.list(new PaymentRecordEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        PaymentRecordEntity mockEntity = PaymentRecordEntity.builder().id(1L).build();
        when(paymentRecordMapper.selectById(1L)).thenReturn(mockEntity);

        PaymentRecordEntity result = paymentRecordService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(paymentRecordMapper.selectById(999L)).thenReturn(null);

        PaymentRecordEntity result = paymentRecordService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        PaymentRecordEntity entity = PaymentRecordEntity.builder().build();
        when(paymentRecordMapper.insert(entity)).thenReturn(1);

        boolean result = paymentRecordService.add(entity);

        assertTrue(result);
        verify(paymentRecordMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        PaymentRecordEntity entity = PaymentRecordEntity.builder().build();
        when(paymentRecordMapper.insert(entity)).thenReturn(0);

        boolean result = paymentRecordService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        PaymentRecordEntity entity = PaymentRecordEntity.builder().id(1L).build();
        when(paymentRecordMapper.update(entity)).thenReturn(1);

        boolean result = paymentRecordService.update(entity);

        assertTrue(result);
        verify(paymentRecordMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        PaymentRecordEntity entity = PaymentRecordEntity.builder().id(1L).build();
        when(paymentRecordMapper.update(entity)).thenReturn(0);

        boolean result = paymentRecordService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(paymentRecordMapper.deleteById(1L)).thenReturn(1);

        boolean result = paymentRecordService.remove(1L);

        assertTrue(result);
        verify(paymentRecordMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(paymentRecordMapper.deleteById(1L)).thenReturn(0);

        boolean result = paymentRecordService.remove(1L);

        assertFalse(result);
    }
}