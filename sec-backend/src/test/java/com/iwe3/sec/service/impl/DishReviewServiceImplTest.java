package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.DishReviewEntity;
import com.iwe3.sec.mapper.DishReviewMapper;
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
 * Unit test for DishReview ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishReviewServiceImplTest {

    @Mock
    private DishReviewMapper dishReviewMapper;

    private DishReviewServiceImpl dishReviewService;

    @BeforeEach
    void setUp() {
        dishReviewService = new DishReviewServiceImpl(dishReviewMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishReviewEntity entity1 = DishReviewEntity.builder().id(1L).build();
        DishReviewEntity entity2 = DishReviewEntity.builder().id(2L).build();
        List<DishReviewEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishReviewMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishReviewEntity> result = dishReviewService.list(new DishReviewEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishReviewMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishReviewEntity> result = dishReviewService.list(new DishReviewEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishReviewEntity mockEntity = DishReviewEntity.builder().id(1L).build();
        when(dishReviewMapper.selectById(1L)).thenReturn(mockEntity);

        DishReviewEntity result = dishReviewService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(dishReviewMapper.selectById(999L)).thenReturn(null);

        DishReviewEntity result = dishReviewService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishReviewEntity entity = DishReviewEntity.builder().build();
        when(dishReviewMapper.insert(entity)).thenReturn(1);

        boolean result = dishReviewService.add(entity);

        assertTrue(result);
        verify(dishReviewMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishReviewEntity entity = DishReviewEntity.builder().build();
        when(dishReviewMapper.insert(entity)).thenReturn(0);

        boolean result = dishReviewService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishReviewEntity entity = DishReviewEntity.builder().id(1L).build();
        when(dishReviewMapper.update(entity)).thenReturn(1);

        boolean result = dishReviewService.update(entity);

        assertTrue(result);
        verify(dishReviewMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishReviewEntity entity = DishReviewEntity.builder().id(1L).build();
        when(dishReviewMapper.update(entity)).thenReturn(0);

        boolean result = dishReviewService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishReviewMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishReviewService.remove(1L);

        assertTrue(result);
        verify(dishReviewMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishReviewMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishReviewService.remove(1L);

        assertFalse(result);
    }
}