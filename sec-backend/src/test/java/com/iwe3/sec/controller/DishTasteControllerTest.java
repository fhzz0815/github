package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.DishTasteEntity;
import com.iwe3.sec.service.IDishTasteService;
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
 * Unit test for DishTaste Controller
 */
@ExtendWith(MockitoExtension.class)
class DishTasteControllerTest {

    @Mock
    private IDishTasteService dishTasteService;

    private DishTasteController controller;

    @BeforeEach
    void setUp() {
        controller = new DishTasteController(dishTasteService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        DishTasteEntity entity1 = DishTasteEntity.builder().id(1L).build();
        List<DishTasteEntity> entityList = Arrays.asList(entity1);
        PageResult<DishTasteEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(dishTasteService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<DishTasteEntity>> result = controller.list(new DishTasteEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(dishTasteService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DishTasteEntity mockEntity = DishTasteEntity.builder().id(1L).build();
        when(dishTasteService.getById(1L)).thenReturn(mockEntity);

        Result<DishTasteEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(dishTasteService.getById(999L)).thenReturn(null);

        Result<DishTasteEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        DishTasteEntity entity = DishTasteEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(dishTasteService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        DishTasteEntity entity = DishTasteEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(dishTasteService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(dishTasteService).remove(1L);
    }
}