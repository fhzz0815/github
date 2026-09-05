package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.RedPacketEntity;
import com.iwe3.sec.service.IRedPacketService;
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
 * Unit test for RedPacket Controller
 */
@ExtendWith(MockitoExtension.class)
class RedPacketControllerTest {

    @Mock
    private IRedPacketService redPacketService;

    private RedPacketController controller;

    @BeforeEach
    void setUp() {
        controller = new RedPacketController(redPacketService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        RedPacketEntity entity1 = RedPacketEntity.builder().id(1L).build();
        List<RedPacketEntity> entityList = Arrays.asList(entity1);
        PageResult<RedPacketEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(redPacketService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<RedPacketEntity>> result = controller.list(new RedPacketEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(redPacketService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        RedPacketEntity mockEntity = RedPacketEntity.builder().id(1L).build();
        when(redPacketService.getById(1L)).thenReturn(mockEntity);

        Result<RedPacketEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(redPacketService.getById(999L)).thenReturn(null);

        Result<RedPacketEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        RedPacketEntity entity = RedPacketEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(redPacketService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        RedPacketEntity entity = RedPacketEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(redPacketService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(redPacketService).remove(1L);
    }
}