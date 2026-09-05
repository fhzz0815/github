package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StaffScheduleEntity;
import com.iwe3.sec.mapper.StaffScheduleMapper;
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
 * Unit test for StaffSchedule ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StaffScheduleServiceImplTest {

    @Mock
    private StaffScheduleMapper staffScheduleMapper;

    private StaffScheduleServiceImpl staffScheduleService;

    @BeforeEach
    void setUp() {
        staffScheduleService = new StaffScheduleServiceImpl(staffScheduleMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StaffScheduleEntity entity1 = StaffScheduleEntity.builder().id(1L).build();
        StaffScheduleEntity entity2 = StaffScheduleEntity.builder().id(2L).build();
        List<StaffScheduleEntity> mockList = Arrays.asList(entity1, entity2);
        when(staffScheduleMapper.selectList(any())).thenReturn(mockList);

        PageResult<StaffScheduleEntity> result = staffScheduleService.list(new StaffScheduleEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(staffScheduleMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StaffScheduleEntity> result = staffScheduleService.list(new StaffScheduleEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StaffScheduleEntity mockEntity = StaffScheduleEntity.builder().id(1L).build();
        when(staffScheduleMapper.selectById(1L)).thenReturn(mockEntity);

        StaffScheduleEntity result = staffScheduleService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(staffScheduleMapper.selectById(999L)).thenReturn(null);

        StaffScheduleEntity result = staffScheduleService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StaffScheduleEntity entity = StaffScheduleEntity.builder().build();
        when(staffScheduleMapper.insert(entity)).thenReturn(1);

        boolean result = staffScheduleService.add(entity);

        assertTrue(result);
        verify(staffScheduleMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StaffScheduleEntity entity = StaffScheduleEntity.builder().build();
        when(staffScheduleMapper.insert(entity)).thenReturn(0);

        boolean result = staffScheduleService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StaffScheduleEntity entity = StaffScheduleEntity.builder().id(1L).build();
        when(staffScheduleMapper.update(entity)).thenReturn(1);

        boolean result = staffScheduleService.update(entity);

        assertTrue(result);
        verify(staffScheduleMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StaffScheduleEntity entity = StaffScheduleEntity.builder().id(1L).build();
        when(staffScheduleMapper.update(entity)).thenReturn(0);

        boolean result = staffScheduleService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(staffScheduleMapper.deleteById(1L)).thenReturn(1);

        boolean result = staffScheduleService.remove(1L);

        assertTrue(result);
        verify(staffScheduleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(staffScheduleMapper.deleteById(1L)).thenReturn(0);

        boolean result = staffScheduleService.remove(1L);

        assertFalse(result);
    }
}