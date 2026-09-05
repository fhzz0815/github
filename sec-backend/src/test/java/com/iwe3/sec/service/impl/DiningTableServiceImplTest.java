package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.mapper.DiningTableMapper;
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
 * Unit test for DiningTable ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DiningTableServiceImplTest {

    @Mock
    private DiningTableMapper diningTableMapper;

    private DiningTableServiceImpl diningTableService;

    @BeforeEach
    void setUp() {
        diningTableService = new DiningTableServiceImpl(diningTableMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DiningTableEntity entity1 = DiningTableEntity.builder().id(1L).build();
        DiningTableEntity entity2 = DiningTableEntity.builder().id(2L).build();
        List<DiningTableEntity> mockList = Arrays.asList(entity1, entity2);
        when(diningTableMapper.selectList(any())).thenReturn(mockList);

        PageResult<DiningTableEntity> result = diningTableService.list(new DiningTableEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(diningTableMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DiningTableEntity> result = diningTableService.list(new DiningTableEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DiningTableEntity mockEntity = DiningTableEntity.builder().id(1L).build();
        when(diningTableMapper.selectById(1L)).thenReturn(mockEntity);

        DiningTableEntity result = diningTableService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(diningTableMapper.selectById(999L)).thenReturn(null);

        DiningTableEntity result = diningTableService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DiningTableEntity entity = DiningTableEntity.builder().build();
        when(diningTableMapper.insert(entity)).thenReturn(1);

        boolean result = diningTableService.add(entity);

        assertTrue(result);
        verify(diningTableMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DiningTableEntity entity = DiningTableEntity.builder().build();
        when(diningTableMapper.insert(entity)).thenReturn(0);

        boolean result = diningTableService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DiningTableEntity entity = DiningTableEntity.builder().id(1L).build();
        when(diningTableMapper.update(entity)).thenReturn(1);

        boolean result = diningTableService.update(entity);

        assertTrue(result);
        verify(diningTableMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DiningTableEntity entity = DiningTableEntity.builder().id(1L).build();
        when(diningTableMapper.update(entity)).thenReturn(0);

        boolean result = diningTableService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(diningTableMapper.deleteById(1L)).thenReturn(1);

        boolean result = diningTableService.remove(1L);

        assertTrue(result);
        verify(diningTableMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(diningTableMapper.deleteById(1L)).thenReturn(0);

        boolean result = diningTableService.remove(1L);

        assertFalse(result);
    }
}