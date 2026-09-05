package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DishIngredientRelEntity;
import com.iwe3.sec.mapper.DishIngredientRelMapper;
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
 * Unit test for DishIngredientRel ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishIngredientRelServiceImplTest {

    @Mock
    private DishIngredientRelMapper dishIngredientRelMapper;

    private DishIngredientRelServiceImpl dishIngredientRelService;

    @BeforeEach
    void setUp() {
        dishIngredientRelService = new DishIngredientRelServiceImpl(dishIngredientRelMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishIngredientRelEntity entity1 = DishIngredientRelEntity.builder().id(1L).build();
        DishIngredientRelEntity entity2 = DishIngredientRelEntity.builder().id(2L).build();
        List<DishIngredientRelEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishIngredientRelMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishIngredientRelEntity> result = dishIngredientRelService.list(new DishIngredientRelEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishIngredientRelMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishIngredientRelEntity> result = dishIngredientRelService.list(new DishIngredientRelEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishIngredientRelEntity mockEntity = DishIngredientRelEntity.builder().id(1L).build();
        when(dishIngredientRelMapper.selectById(1L)).thenReturn(mockEntity);

        DishIngredientRelEntity result = dishIngredientRelService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishIngredientRelMapper.selectById(999L)).thenReturn(null);

        DishIngredientRelEntity result = dishIngredientRelService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishIngredientRelEntity entity = DishIngredientRelEntity.builder().build();
        when(dishIngredientRelMapper.insert(entity)).thenReturn(1);

        boolean result = dishIngredientRelService.add(entity);

        assertTrue(result);
        verify(dishIngredientRelMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishIngredientRelEntity entity = DishIngredientRelEntity.builder().build();
        when(dishIngredientRelMapper.insert(entity)).thenReturn(0);

        boolean result = dishIngredientRelService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishIngredientRelEntity entity = DishIngredientRelEntity.builder().id(1L).build();
        when(dishIngredientRelMapper.update(entity)).thenReturn(1);

        boolean result = dishIngredientRelService.update(entity);

        assertTrue(result);
        verify(dishIngredientRelMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishIngredientRelEntity entity = DishIngredientRelEntity.builder().id(1L).build();
        when(dishIngredientRelMapper.update(entity)).thenReturn(0);

        boolean result = dishIngredientRelService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishIngredientRelMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishIngredientRelService.remove(1L);

        assertTrue(result);
        verify(dishIngredientRelMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishIngredientRelMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishIngredientRelService.remove(1L);

        assertFalse(result);
    }
}