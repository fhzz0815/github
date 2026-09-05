package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DishStockEntity;
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
 * Unit test for DishStock ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishStockServiceImplTest {

    @Mock
    private DishStockMapper dishStockMapper;

    private DishStockServiceImpl dishStockService;

    @BeforeEach
    void setUp() {
        dishStockService = new DishStockServiceImpl(dishStockMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishStockEntity entity1 = DishStockEntity.builder().id(1L).build();
        DishStockEntity entity2 = DishStockEntity.builder().id(2L).build();
        List<DishStockEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishStockMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishStockEntity> result = dishStockService.list(new DishStockEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishStockMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishStockEntity> result = dishStockService.list(new DishStockEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishStockEntity mockEntity = DishStockEntity.builder().id(1L).build();
        when(dishStockMapper.selectById(1L)).thenReturn(mockEntity);

        DishStockEntity result = dishStockService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishStockMapper.selectById(999L)).thenReturn(null);

        DishStockEntity result = dishStockService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishStockEntity entity = DishStockEntity.builder().build();
        when(dishStockMapper.insert(entity)).thenReturn(1);

        boolean result = dishStockService.add(entity);

        assertTrue(result);
        verify(dishStockMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishStockEntity entity = DishStockEntity.builder().build();
        when(dishStockMapper.insert(entity)).thenReturn(0);

        boolean result = dishStockService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishStockEntity entity = DishStockEntity.builder().id(1L).build();
        when(dishStockMapper.update(entity)).thenReturn(1);

        boolean result = dishStockService.update(entity);

        assertTrue(result);
        verify(dishStockMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishStockEntity entity = DishStockEntity.builder().id(1L).build();
        when(dishStockMapper.update(entity)).thenReturn(0);

        boolean result = dishStockService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishStockMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishStockService.remove(1L);

        assertTrue(result);
        verify(dishStockMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishStockMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishStockService.remove(1L);

        assertFalse(result);
    }
}