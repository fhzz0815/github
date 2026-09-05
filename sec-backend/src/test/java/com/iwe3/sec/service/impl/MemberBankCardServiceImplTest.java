package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberBankCardEntity;
import com.iwe3.sec.mapper.MemberBankCardMapper;
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
 * Unit test for MemberBankCard ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberBankCardServiceImplTest {

    @Mock
    private MemberBankCardMapper memberBankCardMapper;

    private MemberBankCardServiceImpl memberBankCardService;

    @BeforeEach
    void setUp() {
        memberBankCardService = new MemberBankCardServiceImpl(memberBankCardMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberBankCardEntity entity1 = MemberBankCardEntity.builder().id(1L).build();
        MemberBankCardEntity entity2 = MemberBankCardEntity.builder().id(2L).build();
        List<MemberBankCardEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberBankCardMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberBankCardEntity> result = memberBankCardService.list(new MemberBankCardEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberBankCardMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberBankCardEntity> result = memberBankCardService.list(new MemberBankCardEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberBankCardEntity mockEntity = MemberBankCardEntity.builder().id(1L).build();
        when(memberBankCardMapper.selectById(1L)).thenReturn(mockEntity);

        MemberBankCardEntity result = memberBankCardService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberBankCardMapper.selectById(999L)).thenReturn(null);

        MemberBankCardEntity result = memberBankCardService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberBankCardEntity entity = MemberBankCardEntity.builder().build();
        when(memberBankCardMapper.insert(entity)).thenReturn(1);

        boolean result = memberBankCardService.add(entity);

        assertTrue(result);
        verify(memberBankCardMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberBankCardEntity entity = MemberBankCardEntity.builder().build();
        when(memberBankCardMapper.insert(entity)).thenReturn(0);

        boolean result = memberBankCardService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberBankCardEntity entity = MemberBankCardEntity.builder().id(1L).build();
        when(memberBankCardMapper.update(entity)).thenReturn(1);

        boolean result = memberBankCardService.update(entity);

        assertTrue(result);
        verify(memberBankCardMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberBankCardEntity entity = MemberBankCardEntity.builder().id(1L).build();
        when(memberBankCardMapper.update(entity)).thenReturn(0);

        boolean result = memberBankCardService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberBankCardMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberBankCardService.remove(1L);

        assertTrue(result);
        verify(memberBankCardMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberBankCardMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberBankCardService.remove(1L);

        assertFalse(result);
    }
}