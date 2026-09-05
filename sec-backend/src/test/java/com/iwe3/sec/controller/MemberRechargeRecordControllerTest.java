package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.service.IMemberRechargeRecordService;
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
 * Unit test for MemberRechargeRecord Controller
 */
@ExtendWith(MockitoExtension.class)
class MemberRechargeRecordControllerTest {

    @Mock
    private IMemberRechargeRecordService memberRechargeRecordService;

    private MemberRechargeRecordController controller;

    @BeforeEach
    void setUp() {
        controller = new MemberRechargeRecordController(memberRechargeRecordService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        MemberRechargeRecordEntity entity1 = MemberRechargeRecordEntity.builder().id(1L).build();
        List<MemberRechargeRecordEntity> entityList = Arrays.asList(entity1);
        PageResult<MemberRechargeRecordEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(memberRechargeRecordService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<MemberRechargeRecordEntity>> result = controller.list(new MemberRechargeRecordEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(memberRechargeRecordService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberRechargeRecordEntity mockEntity = MemberRechargeRecordEntity.builder().id(1L).build();
        when(memberRechargeRecordService.getById(1L)).thenReturn(mockEntity);

        Result<MemberRechargeRecordEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(memberRechargeRecordService.getById(999L)).thenReturn(null);

        Result<MemberRechargeRecordEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(memberRechargeRecordService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(memberRechargeRecordService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(memberRechargeRecordService).remove(1L);
    }
}