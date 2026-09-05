package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.mapper.RefundMapper;
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
 * Unit test for Refund ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

    @Mock
    private RefundMapper refundMapper;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(refundMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        RefundEntity entity1 = RefundEntity.builder().id(1L).build();
        RefundEntity entity2 = RefundEntity.builder().id(2L).build();
        List<RefundEntity> mockList = Arrays.asList(entity1, entity2);
        when(refundMapper.selectList(any())).thenReturn(mockList);

        PageResult<RefundEntity> result = refundService.list(new RefundEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(refundMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<RefundEntity> result = refundService.list(new RefundEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        RefundEntity mockEntity = RefundEntity.builder().id(1L).build();
        when(refundMapper.selectById(1L)).thenReturn(mockEntity);

        RefundEntity result = refundService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(refundMapper.selectById(999L)).thenReturn(null);

        RefundEntity result = refundService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        RefundEntity entity = RefundEntity.builder().build();
        when(refundMapper.insert(entity)).thenReturn(1);

        boolean result = refundService.add(entity);

        assertTrue(result);
        verify(refundMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        RefundEntity entity = RefundEntity.builder().build();
        when(refundMapper.insert(entity)).thenReturn(0);

        boolean result = refundService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        RefundEntity entity = RefundEntity.builder().id(1L).build();
        when(refundMapper.update(entity)).thenReturn(1);

        boolean result = refundService.update(entity);

        assertTrue(result);
        verify(refundMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        RefundEntity entity = RefundEntity.builder().id(1L).build();
        when(refundMapper.update(entity)).thenReturn(0);

        boolean result = refundService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(refundMapper.deleteById(1L)).thenReturn(1);

        boolean result = refundService.remove(1L);

        assertTrue(result);
        verify(refundMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(refundMapper.deleteById(1L)).thenReturn(0);

        boolean result = refundService.remove(1L);

        assertFalse(result);
    }
}