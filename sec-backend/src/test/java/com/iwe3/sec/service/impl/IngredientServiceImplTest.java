package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.IngredientEntity;
import com.iwe3.sec.mapper.IngredientMapper;
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
 * Unit test for Ingredient ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientMapper ingredientMapper;

    private IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        ingredientService = new IngredientServiceImpl(ingredientMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        IngredientEntity entity1 = IngredientEntity.builder().id(1L).build();
        IngredientEntity entity2 = IngredientEntity.builder().id(2L).build();
        List<IngredientEntity> mockList = Arrays.asList(entity1, entity2);
        when(ingredientMapper.selectList(any())).thenReturn(mockList);

        PageResult<IngredientEntity> result = ingredientService.list(new IngredientEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(ingredientMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<IngredientEntity> result = ingredientService.list(new IngredientEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        IngredientEntity mockEntity = IngredientEntity.builder().id(1L).build();
        when(ingredientMapper.selectById(1L)).thenReturn(mockEntity);

        IngredientEntity result = ingredientService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(ingredientMapper.selectById(999L)).thenReturn(null);

        IngredientEntity result = ingredientService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        IngredientEntity entity = IngredientEntity.builder().build();
        when(ingredientMapper.insert(entity)).thenReturn(1);

        boolean result = ingredientService.add(entity);

        assertTrue(result);
        verify(ingredientMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        IngredientEntity entity = IngredientEntity.builder().build();
        when(ingredientMapper.insert(entity)).thenReturn(0);

        boolean result = ingredientService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        IngredientEntity entity = IngredientEntity.builder().id(1L).build();
        when(ingredientMapper.update(entity)).thenReturn(1);

        boolean result = ingredientService.update(entity);

        assertTrue(result);
        verify(ingredientMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        IngredientEntity entity = IngredientEntity.builder().id(1L).build();
        when(ingredientMapper.update(entity)).thenReturn(0);

        boolean result = ingredientService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(ingredientMapper.deleteById(1L)).thenReturn(1);

        boolean result = ingredientService.remove(1L);

        assertTrue(result);
        verify(ingredientMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(ingredientMapper.deleteById(1L)).thenReturn(0);

        boolean result = ingredientService.remove(1L);

        assertFalse(result);
    }
}