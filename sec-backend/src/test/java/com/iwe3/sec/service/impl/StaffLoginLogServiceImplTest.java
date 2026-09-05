package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import com.iwe3.sec.mapper.StaffLoginLogMapper;
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
 * Unit test for StaffLoginLog ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StaffLoginLogServiceImplTest {

    @Mock
    private StaffLoginLogMapper staffLoginLogMapper;

    private StaffLoginLogServiceImpl staffLoginLogService;

    @BeforeEach
    void setUp() {
        staffLoginLogService = new StaffLoginLogServiceImpl(staffLoginLogMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StaffLoginLogEntity entity1 = StaffLoginLogEntity.builder().id(1L).build();
        StaffLoginLogEntity entity2 = StaffLoginLogEntity.builder().id(2L).build();
        List<StaffLoginLogEntity> mockList = Arrays.asList(entity1, entity2);
        when(staffLoginLogMapper.selectList(any())).thenReturn(mockList);

        PageResult<StaffLoginLogEntity> result = staffLoginLogService.list(new StaffLoginLogEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(staffLoginLogMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StaffLoginLogEntity> result = staffLoginLogService.list(new StaffLoginLogEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StaffLoginLogEntity mockEntity = StaffLoginLogEntity.builder().id(1L).build();
        when(staffLoginLogMapper.selectById(1L)).thenReturn(mockEntity);

        StaffLoginLogEntity result = staffLoginLogService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(staffLoginLogMapper.selectById(999L)).thenReturn(null);

        StaffLoginLogEntity result = staffLoginLogService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().build();
        when(staffLoginLogMapper.insert(entity)).thenReturn(1);

        boolean result = staffLoginLogService.add(entity);

        assertTrue(result);
        verify(staffLoginLogMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().build();
        when(staffLoginLogMapper.insert(entity)).thenReturn(0);

        boolean result = staffLoginLogService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().id(1L).build();
        when(staffLoginLogMapper.update(entity)).thenReturn(1);

        boolean result = staffLoginLogService.update(entity);

        assertTrue(result);
        verify(staffLoginLogMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StaffLoginLogEntity entity = StaffLoginLogEntity.builder().id(1L).build();
        when(staffLoginLogMapper.update(entity)).thenReturn(0);

        boolean result = staffLoginLogService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(staffLoginLogMapper.deleteById(1L)).thenReturn(1);

        boolean result = staffLoginLogService.remove(1L);

        assertTrue(result);
        verify(staffLoginLogMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(staffLoginLogMapper.deleteById(1L)).thenReturn(0);

        boolean result = staffLoginLogService.remove(1L);

        assertFalse(result);
    }
}