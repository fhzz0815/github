package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.QueueEntity;
import com.iwe3.sec.mapper.QueueMapper;
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
 * Unit test for Queue ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class QueueServiceImplTest {

    @Mock
    private QueueMapper queueMapper;

    private QueueServiceImpl queueService;

    @BeforeEach
    void setUp() {
        queueService = new QueueServiceImpl(queueMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        QueueEntity entity1 = QueueEntity.builder().id(1L).build();
        QueueEntity entity2 = QueueEntity.builder().id(2L).build();
        List<QueueEntity> mockList = Arrays.asList(entity1, entity2);
        when(queueMapper.selectList(any())).thenReturn(mockList);

        PageResult<QueueEntity> result = queueService.list(new QueueEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(queueMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<QueueEntity> result = queueService.list(new QueueEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        QueueEntity mockEntity = QueueEntity.builder().id(1L).build();
        when(queueMapper.selectById(1L)).thenReturn(mockEntity);

        QueueEntity result = queueService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(queueMapper.selectById(999L)).thenReturn(null);

        QueueEntity result = queueService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        QueueEntity entity = QueueEntity.builder().build();
        when(queueMapper.insert(entity)).thenReturn(1);

        boolean result = queueService.add(entity);

        assertTrue(result);
        verify(queueMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        QueueEntity entity = QueueEntity.builder().build();
        when(queueMapper.insert(entity)).thenReturn(0);

        boolean result = queueService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        QueueEntity entity = QueueEntity.builder().id(1L).build();
        when(queueMapper.update(entity)).thenReturn(1);

        boolean result = queueService.update(entity);

        assertTrue(result);
        verify(queueMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        QueueEntity entity = QueueEntity.builder().id(1L).build();
        when(queueMapper.update(entity)).thenReturn(0);

        boolean result = queueService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(queueMapper.deleteById(1L)).thenReturn(1);

        boolean result = queueService.remove(1L);

        assertTrue(result);
        verify(queueMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(queueMapper.deleteById(1L)).thenReturn(0);

        boolean result = queueService.remove(1L);

        assertFalse(result);
    }
}