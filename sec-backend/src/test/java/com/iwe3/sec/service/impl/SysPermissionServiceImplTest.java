package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.entity.SysPermissionEntity;
import com.iwe3.sec.mapper.SysPermissionMapper;
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
 * Unit test for SysPermission ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class SysPermissionServiceImplTest {

    @Mock
    private SysPermissionMapper sysPermissionMapper;

    @Mock
    private PermissionChecker permissionChecker;

    private SysPermissionServiceImpl sysPermissionService;

    @BeforeEach
    void setUp() {
        sysPermissionService = new SysPermissionServiceImpl(sysPermissionMapper, permissionChecker);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        SysPermissionEntity entity1 = SysPermissionEntity.builder().id(1L).build();
        SysPermissionEntity entity2 = SysPermissionEntity.builder().id(2L).build();
        List<SysPermissionEntity> mockList = Arrays.asList(entity1, entity2);
        when(sysPermissionMapper.selectList(any())).thenReturn(mockList);

        PageResult<SysPermissionEntity> result = sysPermissionService.list(new SysPermissionEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(sysPermissionMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<SysPermissionEntity> result = sysPermissionService.list(new SysPermissionEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        SysPermissionEntity mockEntity = SysPermissionEntity.builder().id(1L).build();
        when(sysPermissionMapper.selectById(1L)).thenReturn(mockEntity);

        SysPermissionEntity result = sysPermissionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(sysPermissionMapper.selectById(999L)).thenReturn(null);

        SysPermissionEntity result = sysPermissionService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        SysPermissionEntity entity = SysPermissionEntity.builder().build();
        when(sysPermissionMapper.insert(entity)).thenReturn(1);

        boolean result = sysPermissionService.add(entity);

        assertTrue(result);
        verify(sysPermissionMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        SysPermissionEntity entity = SysPermissionEntity.builder().build();
        when(sysPermissionMapper.insert(entity)).thenReturn(0);

        boolean result = sysPermissionService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        SysPermissionEntity entity = SysPermissionEntity.builder().id(1L).build();
        when(sysPermissionMapper.update(entity)).thenReturn(1);

        boolean result = sysPermissionService.update(entity);

        assertTrue(result);
        verify(sysPermissionMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        SysPermissionEntity entity = SysPermissionEntity.builder().id(1L).build();
        when(sysPermissionMapper.update(entity)).thenReturn(0);

        boolean result = sysPermissionService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(sysPermissionMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysPermissionService.remove(1L);

        assertTrue(result);
        verify(sysPermissionMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(sysPermissionMapper.deleteById(1L)).thenReturn(0);

        boolean result = sysPermissionService.remove(1L);

        assertFalse(result);
    }
}