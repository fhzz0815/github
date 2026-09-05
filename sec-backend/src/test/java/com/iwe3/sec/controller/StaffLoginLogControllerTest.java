package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.service.IStaffLoginLogService;
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
 * Unit test for StaffLoginLog Controller
 */
@ExtendWith(MockitoExtension.class)
class StaffLoginLogControllerTest {

    @Mock
    private IStaffLoginLogService staffLoginLogService;

    private StaffLoginLogController controller;

    @BeforeEach
    void setUp() {
        controller = new StaffLoginLogController(staffLoginLogService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        StaffLoginLogEntity entity1 = StaffLoginLogEntity.builder().id(1L).build();
        List<StaffLoginLogEntity> entityList = Arrays.asList(entity1);
        PageResult<StaffLoginLogEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(staffLoginLogService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<StaffLoginLogEntity>> result = controller.list(new StaffLoginLogEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(staffLoginLogService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StaffLoginLogEntity mockEntity = StaffLoginLogEntity.builder().id(1L).build();
        when(staffLoginLogService.getById(1L)).thenReturn(mockEntity);

        Result<StaffLoginLogEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(staffLoginLogService.getById(999L)).thenReturn(null);

        Result<StaffLoginLogEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(staffLoginLogService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(staffLoginLogService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(staffLoginLogService).remove(1L);
    }
}