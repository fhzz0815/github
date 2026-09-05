package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.ReceiptTemplateEntity;
import com.iwe3.sec.mapper.ReceiptTemplateMapper;
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
 * Unit test for ReceiptTemplate ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ReceiptTemplateServiceImplTest {

    @Mock
    private ReceiptTemplateMapper receiptTemplateMapper;

    private ReceiptTemplateServiceImpl receiptTemplateService;

    @BeforeEach
    void setUp() {
        receiptTemplateService = new ReceiptTemplateServiceImpl(receiptTemplateMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        ReceiptTemplateEntity entity1 = ReceiptTemplateEntity.builder().id(1L).build();
        ReceiptTemplateEntity entity2 = ReceiptTemplateEntity.builder().id(2L).build();
        List<ReceiptTemplateEntity> mockList = Arrays.asList(entity1, entity2);
        when(receiptTemplateMapper.selectList(any())).thenReturn(mockList);

        PageResult<ReceiptTemplateEntity> result = receiptTemplateService.list(new ReceiptTemplateEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(receiptTemplateMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<ReceiptTemplateEntity> result = receiptTemplateService.list(new ReceiptTemplateEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        ReceiptTemplateEntity mockEntity = ReceiptTemplateEntity.builder().id(1L).build();
        when(receiptTemplateMapper.selectById(1L)).thenReturn(mockEntity);

        ReceiptTemplateEntity result = receiptTemplateService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(receiptTemplateMapper.selectById(999L)).thenReturn(null);

        ReceiptTemplateEntity result = receiptTemplateService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        ReceiptTemplateEntity entity = ReceiptTemplateEntity.builder().build();
        when(receiptTemplateMapper.insert(entity)).thenReturn(1);

        boolean result = receiptTemplateService.add(entity);

        assertTrue(result);
        verify(receiptTemplateMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        ReceiptTemplateEntity entity = ReceiptTemplateEntity.builder().build();
        when(receiptTemplateMapper.insert(entity)).thenReturn(0);

        boolean result = receiptTemplateService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        ReceiptTemplateEntity entity = ReceiptTemplateEntity.builder().id(1L).build();
        when(receiptTemplateMapper.update(entity)).thenReturn(1);

        boolean result = receiptTemplateService.update(entity);

        assertTrue(result);
        verify(receiptTemplateMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        ReceiptTemplateEntity entity = ReceiptTemplateEntity.builder().id(1L).build();
        when(receiptTemplateMapper.update(entity)).thenReturn(0);

        boolean result = receiptTemplateService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(receiptTemplateMapper.deleteById(1L)).thenReturn(1);

        boolean result = receiptTemplateService.remove(1L);

        assertTrue(result);
        verify(receiptTemplateMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(receiptTemplateMapper.deleteById(1L)).thenReturn(0);

        boolean result = receiptTemplateService.remove(1L);

        assertFalse(result);
    }
}