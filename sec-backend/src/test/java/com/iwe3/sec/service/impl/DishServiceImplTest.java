package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.entity.DishEntity;
import com.iwe3.sec.mapper.DishMapper;
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
 * Unit test for Dish ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    @Mock
    private DishMapper dishMapper;

    @Mock
    private PermissionChecker permissionChecker;

    private DishServiceImpl dishService;

    @BeforeEach
    void setUp() {
        dishService = new DishServiceImpl(dishMapper, permissionChecker);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishEntity entity1 = DishEntity.builder().id(1L).build();
        DishEntity entity2 = DishEntity.builder().id(2L).build();
        List<DishEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishEntity> result = dishService.list(new DishEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(permissionChecker.currentRoleLevel()).thenReturn(99);
        when(dishMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishEntity> result = dishService.list(new DishEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishEntity mockEntity = DishEntity.builder().id(1L).build();
        when(dishMapper.selectById(1L)).thenReturn(mockEntity);

        DishEntity result = dishService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishMapper.selectById(999L)).thenReturn(null);

        DishEntity result = dishService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishEntity entity = DishEntity.builder().build();
        when(dishMapper.insert(entity)).thenReturn(1);

        boolean result = dishService.add(entity);

        assertTrue(result);
        verify(dishMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishEntity entity = DishEntity.builder().build();
        when(dishMapper.insert(entity)).thenReturn(0);

        boolean result = dishService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishEntity entity = DishEntity.builder().id(1L).build();
        when(dishMapper.update(entity)).thenReturn(1);

        boolean result = dishService.update(entity);

        assertTrue(result);
        verify(dishMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishEntity entity = DishEntity.builder().id(1L).build();
        when(dishMapper.update(entity)).thenReturn(0);

        boolean result = dishService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishService.remove(1L);

        assertTrue(result);
        verify(dishMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishService.remove(1L);

        assertFalse(result);
    }
}
