package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.mapper.PrinterMapper;
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
 * Unit test for Printer ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class PrinterServiceImplTest {

    @Mock
    private PrinterMapper printerMapper;

    private PrinterServiceImpl printerService;

    @BeforeEach
    void setUp() {
        printerService = new PrinterServiceImpl(printerMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        PrinterEntity entity1 = PrinterEntity.builder().id(1L).build();
        PrinterEntity entity2 = PrinterEntity.builder().id(2L).build();
        List<PrinterEntity> mockList = Arrays.asList(entity1, entity2);
        when(printerMapper.selectList(any())).thenReturn(mockList);

        PageResult<PrinterEntity> result = printerService.list(new PrinterEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(printerMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<PrinterEntity> result = printerService.list(new PrinterEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        PrinterEntity mockEntity = PrinterEntity.builder().id(1L).build();
        when(printerMapper.selectById(1L)).thenReturn(mockEntity);

        PrinterEntity result = printerService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(printerMapper.selectById(999L)).thenReturn(null);

        PrinterEntity result = printerService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        PrinterEntity entity = PrinterEntity.builder().build();
        when(printerMapper.insert(entity)).thenReturn(1);

        boolean result = printerService.add(entity);

        assertTrue(result);
        verify(printerMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        PrinterEntity entity = PrinterEntity.builder().build();
        when(printerMapper.insert(entity)).thenReturn(0);

        boolean result = printerService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        PrinterEntity entity = PrinterEntity.builder().id(1L).build();
        when(printerMapper.update(entity)).thenReturn(1);

        boolean result = printerService.update(entity);

        assertTrue(result);
        verify(printerMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        PrinterEntity entity = PrinterEntity.builder().id(1L).build();
        when(printerMapper.update(entity)).thenReturn(0);

        boolean result = printerService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(printerMapper.deleteById(1L)).thenReturn(1);

        boolean result = printerService.remove(1L);

        assertTrue(result);
        verify(printerMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(printerMapper.deleteById(1L)).thenReturn(0);

        boolean result = printerService.remove(1L);

        assertFalse(result);
    }
}