package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.mapper.DishCategoryMapper;
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
 * Unit test for DishCategory ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishCategoryServiceImplTest {

    @Mock
    private DishCategoryMapper dishCategoryMapper;

    private DishCategoryServiceImpl dishCategoryService;

    @BeforeEach
    void setUp() {
        dishCategoryService = new DishCategoryServiceImpl(dishCategoryMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishCategoryEntity entity1 = DishCategoryEntity.builder().id(1L).build();
        DishCategoryEntity entity2 = DishCategoryEntity.builder().id(2L).build();
        List<DishCategoryEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishCategoryMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishCategoryEntity> result = dishCategoryService.list(new DishCategoryEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishCategoryMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishCategoryEntity> result = dishCategoryService.list(new DishCategoryEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishCategoryEntity mockEntity = DishCategoryEntity.builder().id(1L).build();
        when(dishCategoryMapper.selectById(1L)).thenReturn(mockEntity);

        DishCategoryEntity result = dishCategoryService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishCategoryMapper.selectById(999L)).thenReturn(null);

        DishCategoryEntity result = dishCategoryService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishCategoryEntity entity = DishCategoryEntity.builder().build();
        when(dishCategoryMapper.insert(entity)).thenReturn(1);

        boolean result = dishCategoryService.add(entity);

        assertTrue(result);
        verify(dishCategoryMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishCategoryEntity entity = DishCategoryEntity.builder().build();
        when(dishCategoryMapper.insert(entity)).thenReturn(0);

        boolean result = dishCategoryService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishCategoryEntity entity = DishCategoryEntity.builder().id(1L).build();
        when(dishCategoryMapper.update(entity)).thenReturn(1);

        boolean result = dishCategoryService.update(entity);

        assertTrue(result);
        verify(dishCategoryMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishCategoryEntity entity = DishCategoryEntity.builder().id(1L).build();
        when(dishCategoryMapper.update(entity)).thenReturn(0);

        boolean result = dishCategoryService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishCategoryMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishCategoryService.remove(1L);

        assertTrue(result);
        verify(dishCategoryMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishCategoryMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishCategoryService.remove(1L);

        assertFalse(result);
    }
}