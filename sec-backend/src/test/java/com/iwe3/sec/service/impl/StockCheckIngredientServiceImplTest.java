package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import com.iwe3.sec.mapper.StockCheckIngredientMapper;
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
 * Unit test for StockCheckIngredient ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StockCheckIngredientServiceImplTest {

    @Mock
    private StockCheckIngredientMapper stockCheckIngredientMapper;

    private StockCheckIngredientServiceImpl stockCheckIngredientService;

    @BeforeEach
    void setUp() {
        stockCheckIngredientService = new StockCheckIngredientServiceImpl(stockCheckIngredientMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StockCheckIngredientEntity entity1 = StockCheckIngredientEntity.builder().id(1L).build();
        StockCheckIngredientEntity entity2 = StockCheckIngredientEntity.builder().id(2L).build();
        List<StockCheckIngredientEntity> mockList = Arrays.asList(entity1, entity2);
        when(stockCheckIngredientMapper.selectList(any())).thenReturn(mockList);

        PageResult<StockCheckIngredientEntity> result = stockCheckIngredientService.list(new StockCheckIngredientEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(stockCheckIngredientMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StockCheckIngredientEntity> result = stockCheckIngredientService.list(new StockCheckIngredientEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StockCheckIngredientEntity mockEntity = StockCheckIngredientEntity.builder().id(1L).build();
        when(stockCheckIngredientMapper.selectById(1L)).thenReturn(mockEntity);

        StockCheckIngredientEntity result = stockCheckIngredientService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(stockCheckIngredientMapper.selectById(999L)).thenReturn(null);

        StockCheckIngredientEntity result = stockCheckIngredientService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StockCheckIngredientEntity entity = StockCheckIngredientEntity.builder().build();
        when(stockCheckIngredientMapper.insert(entity)).thenReturn(1);

        boolean result = stockCheckIngredientService.add(entity);

        assertTrue(result);
        verify(stockCheckIngredientMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StockCheckIngredientEntity entity = StockCheckIngredientEntity.builder().build();
        when(stockCheckIngredientMapper.insert(entity)).thenReturn(0);

        boolean result = stockCheckIngredientService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StockCheckIngredientEntity entity = StockCheckIngredientEntity.builder().id(1L).build();
        when(stockCheckIngredientMapper.update(entity)).thenReturn(1);

        boolean result = stockCheckIngredientService.update(entity);

        assertTrue(result);
        verify(stockCheckIngredientMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StockCheckIngredientEntity entity = StockCheckIngredientEntity.builder().id(1L).build();
        when(stockCheckIngredientMapper.update(entity)).thenReturn(0);

        boolean result = stockCheckIngredientService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(stockCheckIngredientMapper.deleteById(1L)).thenReturn(1);

        boolean result = stockCheckIngredientService.remove(1L);

        assertTrue(result);
        verify(stockCheckIngredientMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(stockCheckIngredientMapper.deleteById(1L)).thenReturn(0);

        boolean result = stockCheckIngredientService.remove(1L);

        assertFalse(result);
    }
}