package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.mapper.FeedbackMapper;
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
 * Unit test for Feedback ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackMapper feedbackMapper;

    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceImpl(feedbackMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        FeedbackEntity entity1 = FeedbackEntity.builder().id(1L).build();
        FeedbackEntity entity2 = FeedbackEntity.builder().id(2L).build();
        List<FeedbackEntity> mockList = Arrays.asList(entity1, entity2);
        when(feedbackMapper.selectList(any())).thenReturn(mockList);

        PageResult<FeedbackEntity> result = feedbackService.list(new FeedbackEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(feedbackMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<FeedbackEntity> result = feedbackService.list(new FeedbackEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        FeedbackEntity mockEntity = FeedbackEntity.builder().id(1L).build();
        when(feedbackMapper.selectById(1L)).thenReturn(mockEntity);

        FeedbackEntity result = feedbackService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(feedbackMapper.selectById(999L)).thenReturn(null);

        FeedbackEntity result = feedbackService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        FeedbackEntity entity = FeedbackEntity.builder().build();
        when(feedbackMapper.insert(entity)).thenReturn(1);

        boolean result = feedbackService.add(entity);

        assertTrue(result);
        verify(feedbackMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        FeedbackEntity entity = FeedbackEntity.builder().build();
        when(feedbackMapper.insert(entity)).thenReturn(0);

        boolean result = feedbackService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        FeedbackEntity entity = FeedbackEntity.builder().id(1L).build();
        when(feedbackMapper.update(entity)).thenReturn(1);

        boolean result = feedbackService.update(entity);

        assertTrue(result);
        verify(feedbackMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        FeedbackEntity entity = FeedbackEntity.builder().id(1L).build();
        when(feedbackMapper.update(entity)).thenReturn(0);

        boolean result = feedbackService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(feedbackMapper.deleteById(1L)).thenReturn(1);

        boolean result = feedbackService.remove(1L);

        assertTrue(result);
        verify(feedbackMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(feedbackMapper.deleteById(1L)).thenReturn(0);

        boolean result = feedbackService.remove(1L);

        assertFalse(result);
    }
}