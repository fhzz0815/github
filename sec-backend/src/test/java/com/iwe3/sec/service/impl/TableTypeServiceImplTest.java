package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.TableTypeEntity;
import com.iwe3.sec.mapper.TableTypeMapper;
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
 * Unit test for TableType ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class TableTypeServiceImplTest {

    @Mock
    private TableTypeMapper tableTypeMapper;

    private TableTypeServiceImpl tableTypeService;

    @BeforeEach
    void setUp() {
        tableTypeService = new TableTypeServiceImpl(tableTypeMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        TableTypeEntity entity1 = TableTypeEntity.builder().id(1L).build();
        TableTypeEntity entity2 = TableTypeEntity.builder().id(2L).build();
        List<TableTypeEntity> mockList = Arrays.asList(entity1, entity2);
        when(tableTypeMapper.selectList(any())).thenReturn(mockList);

        PageResult<TableTypeEntity> result = tableTypeService.list(new TableTypeEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(tableTypeMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<TableTypeEntity> result = tableTypeService.list(new TableTypeEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        TableTypeEntity mockEntity = TableTypeEntity.builder().id(1L).build();
        when(tableTypeMapper.selectById(1L)).thenReturn(mockEntity);

        TableTypeEntity result = tableTypeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(tableTypeMapper.selectById(999L)).thenReturn(null);

        TableTypeEntity result = tableTypeService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        TableTypeEntity entity = TableTypeEntity.builder().build();
        when(tableTypeMapper.insert(entity)).thenReturn(1);

        boolean result = tableTypeService.add(entity);

        assertTrue(result);
        verify(tableTypeMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        TableTypeEntity entity = TableTypeEntity.builder().build();
        when(tableTypeMapper.insert(entity)).thenReturn(0);

        boolean result = tableTypeService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        TableTypeEntity entity = TableTypeEntity.builder().id(1L).build();
        when(tableTypeMapper.update(entity)).thenReturn(1);

        boolean result = tableTypeService.update(entity);

        assertTrue(result);
        verify(tableTypeMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        TableTypeEntity entity = TableTypeEntity.builder().id(1L).build();
        when(tableTypeMapper.update(entity)).thenReturn(0);

        boolean result = tableTypeService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(tableTypeMapper.deleteById(1L)).thenReturn(1);

        boolean result = tableTypeService.remove(1L);

        assertTrue(result);
        verify(tableTypeMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(tableTypeMapper.deleteById(1L)).thenReturn(0);

        boolean result = tableTypeService.remove(1L);

        assertFalse(result);
    }
}