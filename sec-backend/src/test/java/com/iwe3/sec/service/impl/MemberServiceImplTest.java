package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.mapper.MemberMapper;
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
 * Unit test for Member ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PermissionChecker permissionChecker;

    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberServiceImpl(memberMapper, permissionChecker);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberEntity entity1 = MemberEntity.builder().id(1L).build();
        MemberEntity entity2 = MemberEntity.builder().id(2L).build();
        List<MemberEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberEntity> result = memberService.list(new MemberEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberEntity> result = memberService.list(new MemberEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        when(permissionChecker.currentRoleLevel()).thenReturn(99);
        MemberEntity mockEntity = MemberEntity.builder().id(1L).build();
        when(memberMapper.selectById(1L)).thenReturn(mockEntity);

        MemberEntity result = memberService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberMapper.selectById(999L)).thenReturn(null);

        MemberEntity result = memberService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberEntity entity = MemberEntity.builder().build();
        when(memberMapper.insert(entity)).thenReturn(1);

        boolean result = memberService.add(entity);

        assertTrue(result);
        verify(memberMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberEntity entity = MemberEntity.builder().build();
        when(memberMapper.insert(entity)).thenReturn(0);

        boolean result = memberService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberEntity entity = MemberEntity.builder().id(1L).build();
        when(memberMapper.update(entity)).thenReturn(1);

        boolean result = memberService.update(entity);

        assertTrue(result);
        verify(memberMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberEntity entity = MemberEntity.builder().id(1L).build();
        when(memberMapper.update(entity)).thenReturn(0);

        boolean result = memberService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberService.remove(1L);

        assertTrue(result);
        verify(memberMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberService.remove(1L);

        assertFalse(result);
    }
}