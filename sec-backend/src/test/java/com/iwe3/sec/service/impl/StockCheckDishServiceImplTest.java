package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StockCheckDishEntity;
import com.iwe3.sec.mapper.StockCheckDishMapper;
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
 * Unit test for StockCheckDish ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StockCheckDishServiceImplTest {

    @Mock
    private StockCheckDishMapper stockCheckDishMapper;

    private StockCheckDishServiceImpl stockCheckDishService;

    @BeforeEach
    void setUp() {
        stockCheckDishService = new StockCheckDishServiceImpl(stockCheckDishMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StockCheckDishEntity entity1 = StockCheckDishEntity.builder().id(1L).build();
        StockCheckDishEntity entity2 = StockCheckDishEntity.builder().id(2L).build();
        List<StockCheckDishEntity> mockList = Arrays.asList(entity1, entity2);
        when(stockCheckDishMapper.selectList(any())).thenReturn(mockList);

        PageResult<StockCheckDishEntity> result = stockCheckDishService.list(new StockCheckDishEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(stockCheckDishMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StockCheckDishEntity> result = stockCheckDishService.list(new StockCheckDishEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StockCheckDishEntity mockEntity = StockCheckDishEntity.builder().id(1L).build();
        when(stockCheckDishMapper.selectById(1L)).thenReturn(mockEntity);

        StockCheckDishEntity result = stockCheckDishService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(stockCheckDishMapper.selectById(999L)).thenReturn(null);

        StockCheckDishEntity result = stockCheckDishService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StockCheckDishEntity entity = StockCheckDishEntity.builder().build();
        when(stockCheckDishMapper.insert(entity)).thenReturn(1);

        boolean result = stockCheckDishService.add(entity);

        assertTrue(result);
        verify(stockCheckDishMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StockCheckDishEntity entity = StockCheckDishEntity.builder().build();
        when(stockCheckDishMapper.insert(entity)).thenReturn(0);

        boolean result = stockCheckDishService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StockCheckDishEntity entity = StockCheckDishEntity.builder().id(1L).build();
        when(stockCheckDishMapper.update(entity)).thenReturn(1);

        boolean result = stockCheckDishService.update(entity);

        assertTrue(result);
        verify(stockCheckDishMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StockCheckDishEntity entity = StockCheckDishEntity.builder().id(1L).build();
        when(stockCheckDishMapper.update(entity)).thenReturn(0);

        boolean result = stockCheckDishService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(stockCheckDishMapper.deleteById(1L)).thenReturn(1);

        boolean result = stockCheckDishService.remove(1L);

        assertTrue(result);
        verify(stockCheckDishMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(stockCheckDishMapper.deleteById(1L)).thenReturn(0);

        boolean result = stockCheckDishService.remove(1L);

        assertFalse(result);
    }
}