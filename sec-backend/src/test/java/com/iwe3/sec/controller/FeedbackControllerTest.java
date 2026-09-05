package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.service.IFeedbackService;
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
 * Unit test for Feedback Controller
 */
@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private IFeedbackService feedbackService;

    private FeedbackController controller;

    @BeforeEach
    void setUp() {
        controller = new FeedbackController(feedbackService);
    }

    @Test
    @DisplayName("List should return page result")
    void testList_ShouldReturnPageResult() {
        FeedbackEntity entity1 = FeedbackEntity.builder().id(1L).build();
        List<FeedbackEntity> entityList = Arrays.asList(entity1);
        PageResult<FeedbackEntity> pageResult = PageResult.of(1L, 1, entityList);
        when(feedbackService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        Result<PageResult<FeedbackEntity>> result = controller.list(new FeedbackEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getList().size());
        verify(feedbackService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        FeedbackEntity mockEntity = FeedbackEntity.builder().id(1L).build();
        when(feedbackService.getById(1L)).thenReturn(mockEntity);

        Result<FeedbackEntity> result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null data")
    void testGetById_WhenNotExists_ShouldReturnNullData() {
        when(feedbackService.getById(999L)).thenReturn(null);

        Result<FeedbackEntity> result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Add should call service")
    void testAdd_ShouldCallService() {
        FeedbackEntity entity = FeedbackEntity.builder().build();

        Result<Void> result = controller.add(entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(feedbackService).add(entity);
    }

    @Test
    @DisplayName("Update should set id and call service")
    void testUpdate_ShouldSetIdAndCallService() {
        FeedbackEntity entity = FeedbackEntity.builder().build();

        Result<Void> result = controller.update(1L, entity);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals(1L, entity.getId());
        verify(feedbackService).update(entity);
    }

    @Test
    @DisplayName("Delete should call service")
    void testDelete_ShouldCallService() {
        Result<Void> result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(feedbackService).remove(1L);
    }
}