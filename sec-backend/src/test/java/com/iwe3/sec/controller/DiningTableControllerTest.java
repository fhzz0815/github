package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.service.IDiningTableService;
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
 * Unit test for DiningTable Controller
 */
@ExtendWith(MockitoExtension.class)
class DiningTableControllerTest {

    @Mock
    private IDiningTableService diningTableService;

    private DiningTableController controller;

    @BeforeEach
    void setUp() {
        controller = new DiningTableController(diningTableService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        DiningTableEntity entity1 = DiningTableEntity.builder().id(1L).build();
        List<DiningTableEntity> entityList = Arrays.asList(entity1);
        PageResult<DiningTableEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(diningTableService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<DiningTableEntity>> result = controller.list(new DiningTableEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(diningTableService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        DiningTableEntity mockEntity = DiningTableEntity.builder().id(1L).build();
        when(diningTableService.getById(1L)).thenReturn(mockEntity);

        Result<DiningTableEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(diningTableService.getById(999L)).thenReturn(null);

        Result<DiningTableEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        DiningTableEntity entity = DiningTableEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(diningTableService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        DiningTableEntity entity = DiningTableEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(diningTableService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(diningTableService).remove(1L);
    }
}