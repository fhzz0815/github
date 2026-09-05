package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.RedPacketEntity;
import com.iwe3.sec.mapper.RedPacketMapper;
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
 * Unit test for RedPacket ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class RedPacketServiceImplTest {

    @Mock
    private RedPacketMapper redPacketMapper;

    private RedPacketServiceImpl redPacketService;

    @BeforeEach
    void setUp() {
        redPacketService = new RedPacketServiceImpl(redPacketMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        RedPacketEntity entity1 = RedPacketEntity.builder().id(1L).build();
        RedPacketEntity entity2 = RedPacketEntity.builder().id(2L).build();
        List<RedPacketEntity> mockList = Arrays.asList(entity1, entity2);
        when(redPacketMapper.selectList(any())).thenReturn(mockList);

        PageResult<RedPacketEntity> result = redPacketService.list(new RedPacketEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(redPacketMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<RedPacketEntity> result = redPacketService.list(new RedPacketEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        RedPacketEntity mockEntity = RedPacketEntity.builder().id(1L).build();
        when(redPacketMapper.selectById(1L)).thenReturn(mockEntity);

        RedPacketEntity result = redPacketService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(redPacketMapper.selectById(999L)).thenReturn(null);

        RedPacketEntity result = redPacketService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        RedPacketEntity entity = RedPacketEntity.builder().build();
        when(redPacketMapper.insert(entity)).thenReturn(1);

        boolean result = redPacketService.add(entity);

        assertTrue(result);
        verify(redPacketMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        RedPacketEntity entity = RedPacketEntity.builder().build();
        when(redPacketMapper.insert(entity)).thenReturn(0);

        boolean result = redPacketService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        RedPacketEntity entity = RedPacketEntity.builder().id(1L).build();
        when(redPacketMapper.update(entity)).thenReturn(1);

        boolean result = redPacketService.update(entity);

        assertTrue(result);
        verify(redPacketMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        RedPacketEntity entity = RedPacketEntity.builder().id(1L).build();
        when(redPacketMapper.update(entity)).thenReturn(0);

        boolean result = redPacketService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(redPacketMapper.deleteById(1L)).thenReturn(1);

        boolean result = redPacketService.remove(1L);

        assertTrue(result);
        verify(redPacketMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(redPacketMapper.deleteById(1L)).thenReturn(0);

        boolean result = redPacketService.remove(1L);

        assertFalse(result);
    }
}