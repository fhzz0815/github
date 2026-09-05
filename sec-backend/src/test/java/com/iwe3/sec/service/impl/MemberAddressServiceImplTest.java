package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberAddressEntity;
import com.iwe3.sec.mapper.MemberAddressMapper;
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
 * Unit test for MemberAddress ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberAddressServiceImplTest {

    @Mock
    private MemberAddressMapper memberAddressMapper;

    private MemberAddressServiceImpl memberAddressService;

    @BeforeEach
    void setUp() {
        memberAddressService = new MemberAddressServiceImpl(memberAddressMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberAddressEntity entity1 = MemberAddressEntity.builder().id(1L).build();
        MemberAddressEntity entity2 = MemberAddressEntity.builder().id(2L).build();
        List<MemberAddressEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberAddressMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberAddressEntity> result = memberAddressService.list(new MemberAddressEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberAddressMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberAddressEntity> result = memberAddressService.list(new MemberAddressEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberAddressEntity mockEntity = MemberAddressEntity.builder().id(1L).build();
        when(memberAddressMapper.selectById(1L)).thenReturn(mockEntity);

        MemberAddressEntity result = memberAddressService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberAddressMapper.selectById(999L)).thenReturn(null);

        MemberAddressEntity result = memberAddressService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberAddressEntity entity = MemberAddressEntity.builder().build();
        when(memberAddressMapper.insert(entity)).thenReturn(1);

        boolean result = memberAddressService.add(entity);

        assertTrue(result);
        verify(memberAddressMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberAddressEntity entity = MemberAddressEntity.builder().build();
        when(memberAddressMapper.insert(entity)).thenReturn(0);

        boolean result = memberAddressService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberAddressEntity entity = MemberAddressEntity.builder().id(1L).build();
        when(memberAddressMapper.update(entity)).thenReturn(1);

        boolean result = memberAddressService.update(entity);

        assertTrue(result);
        verify(memberAddressMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberAddressEntity entity = MemberAddressEntity.builder().id(1L).build();
        when(memberAddressMapper.update(entity)).thenReturn(0);

        boolean result = memberAddressService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberAddressMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberAddressService.remove(1L);

        assertTrue(result);
        verify(memberAddressMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberAddressMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberAddressService.remove(1L);

        assertFalse(result);
    }
}