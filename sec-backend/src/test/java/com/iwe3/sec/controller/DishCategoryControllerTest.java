package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.DishCategoryEntity;
import com.iwe3.sec.service.IDishCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for DishCategory Controller
 */
@ExtendWith(MockitoExtension.class)
class DishCategoryControllerTest {

    @Mock
    private IDishCategoryService dishCategoryService;

    private DishCategoryController controller;

    @BeforeEach
    void setUp() {
        controller = new DishCategoryController(dishCategoryService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        DishCategoryEntity entity1 = DishCategoryEntity.builder().id(1L).build();
        List<DishCategoryEntity> entityList = Arrays.asList(entity1);
        PageResult<DishCategoryEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(dishCategoryService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<DishCategoryEntity>> result = controller.list(new DishCategoryEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(dishCategoryService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishCategoryEntity mockEntity = DishCategoryEntity.builder().id(1L).build();
        when(dishCategoryService.getById(1L)).thenReturn(mockEntity);

        Result<DishCategoryEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(dishCategoryService.getById(999L)).thenReturn(null);

        Result<DishCategoryEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        DishCategoryEntity entity = DishCategoryEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(dishCategoryService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        DishCategoryEntity entity = DishCategoryEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(dishCategoryService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(dishCategoryService).remove(1L);
    }
}