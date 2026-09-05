package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.mapper.SysRoleMapper;
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
 * Unit test for SysRole ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class SysRoleServiceImplTest {

    @Mock
    private SysRoleMapper sysRoleMapper;

    @Mock
    private PermissionChecker permissionChecker;

    private SysRoleServiceImpl sysRoleService;

    @BeforeEach
    void setUp() {
        sysRoleService = new SysRoleServiceImpl(sysRoleMapper, permissionChecker);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        SysRoleEntity entity1 = SysRoleEntity.builder().id(1L).build();
        SysRoleEntity entity2 = SysRoleEntity.builder().id(2L).build();
        List<SysRoleEntity> mockList = Arrays.asList(entity1, entity2);
        when(sysRoleMapper.selectList(any())).thenReturn(mockList);

        PageResult<SysRoleEntity> result = sysRoleService.list(new SysRoleEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(sysRoleMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<SysRoleEntity> result = sysRoleService.list(new SysRoleEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        SysRoleEntity mockEntity = SysRoleEntity.builder().id(1L).build();
        when(sysRoleMapper.selectById(1L)).thenReturn(mockEntity);

        SysRoleEntity result = sysRoleService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(sysRoleMapper.selectById(999L)).thenReturn(null);

        SysRoleEntity result = sysRoleService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        SysRoleEntity entity = SysRoleEntity.builder().build();
        when(sysRoleMapper.insert(entity)).thenReturn(1);

        boolean result = sysRoleService.add(entity);

        assertTrue(result);
        verify(sysRoleMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        SysRoleEntity entity = SysRoleEntity.builder().build();
        when(sysRoleMapper.insert(entity)).thenReturn(0);

        boolean result = sysRoleService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        SysRoleEntity entity = SysRoleEntity.builder().id(1L).build();
        when(sysRoleMapper.update(entity)).thenReturn(1);

        boolean result = sysRoleService.update(entity);

        assertTrue(result);
        verify(sysRoleMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        SysRoleEntity entity = SysRoleEntity.builder().id(1L).build();
        when(sysRoleMapper.update(entity)).thenReturn(0);

        boolean result = sysRoleService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(sysRoleMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysRoleService.remove(1L);

        assertTrue(result);
        verify(sysRoleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(sysRoleMapper.deleteById(1L)).thenReturn(0);

        boolean result = sysRoleService.remove(1L);

        assertFalse(result);
    }
}