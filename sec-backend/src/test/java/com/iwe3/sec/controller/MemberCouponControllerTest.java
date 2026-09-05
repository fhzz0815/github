package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.service.IMemberCouponService;
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
 * Unit test for MemberCoupon Controller
 */
@ExtendWith(MockitoExtension.class)
class MemberCouponControllerTest {

    @Mock
    private IMemberCouponService memberCouponService;

    private MemberCouponController controller;

    @BeforeEach
    void setUp() {
        controller = new MemberCouponController(memberCouponService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        MemberCouponEntity entity1 = MemberCouponEntity.builder().id(1L).build();
        List<MemberCouponEntity> entityList = Arrays.asList(entity1);
        PageResult<MemberCouponEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(memberCouponService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<MemberCouponEntity>> result = controller.list(new MemberCouponEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(memberCouponService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberCouponEntity mockEntity = MemberCouponEntity.builder().id(1L).build();
        when(memberCouponService.getById(1L)).thenReturn(mockEntity);

        Result<MemberCouponEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(memberCouponService.getById(999L)).thenReturn(null);

        Result<MemberCouponEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        MemberCouponEntity entity = MemberCouponEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(memberCouponService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        MemberCouponEntity entity = MemberCouponEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(memberCouponService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(memberCouponService).remove(1L);
    }
}