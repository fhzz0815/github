package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DishTasteEntity;
import com.iwe3.sec.mapper.DishTasteMapper;
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
 * Unit test for DishTaste ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishTasteServiceImplTest {

    @Mock
    private DishTasteMapper dishTasteMapper;

    private DishTasteServiceImpl dishTasteService;

    @BeforeEach
    void setUp() {
        dishTasteService = new DishTasteServiceImpl(dishTasteMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishTasteEntity entity1 = DishTasteEntity.builder().id(1L).build();
        DishTasteEntity entity2 = DishTasteEntity.builder().id(2L).build();
        List<DishTasteEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishTasteMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishTasteEntity> result = dishTasteService.list(new DishTasteEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishTasteMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishTasteEntity> result = dishTasteService.list(new DishTasteEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishTasteEntity mockEntity = DishTasteEntity.builder().id(1L).build();
        when(dishTasteMapper.selectById(1L)).thenReturn(mockEntity);

        DishTasteEntity result = dishTasteService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishTasteMapper.selectById(999L)).thenReturn(null);

        DishTasteEntity result = dishTasteService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishTasteEntity entity = DishTasteEntity.builder().build();
        when(dishTasteMapper.insert(entity)).thenReturn(1);

        boolean result = dishTasteService.add(entity);

        assertTrue(result);
        verify(dishTasteMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishTasteEntity entity = DishTasteEntity.builder().build();
        when(dishTasteMapper.insert(entity)).thenReturn(0);

        boolean result = dishTasteService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishTasteEntity entity = DishTasteEntity.builder().id(1L).build();
        when(dishTasteMapper.update(entity)).thenReturn(1);

        boolean result = dishTasteService.update(entity);

        assertTrue(result);
        verify(dishTasteMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishTasteEntity entity = DishTasteEntity.builder().id(1L).build();
        when(dishTasteMapper.update(entity)).thenReturn(0);

        boolean result = dishTasteService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishTasteMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishTasteService.remove(1L);

        assertTrue(result);
        verify(dishTasteMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishTasteMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishTasteService.remove(1L);

        assertFalse(result);
    }
}