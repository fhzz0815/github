package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.mapper.MemberRechargeRecordMapper;
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
 * Unit test for MemberRechargeRecord ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberRechargeRecordServiceImplTest {

    @Mock
    private MemberRechargeRecordMapper memberRechargeRecordMapper;

    private MemberRechargeRecordServiceImpl memberRechargeRecordService;

    @BeforeEach
    void setUp() {
        memberRechargeRecordService = new MemberRechargeRecordServiceImpl(memberRechargeRecordMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberRechargeRecordEntity entity1 = MemberRechargeRecordEntity.builder().id(1L).build();
        MemberRechargeRecordEntity entity2 = MemberRechargeRecordEntity.builder().id(2L).build();
        List<MemberRechargeRecordEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberRechargeRecordMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberRechargeRecordEntity> result = memberRechargeRecordService.list(new MemberRechargeRecordEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberRechargeRecordMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberRechargeRecordEntity> result = memberRechargeRecordService.list(new MemberRechargeRecordEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberRechargeRecordEntity mockEntity = MemberRechargeRecordEntity.builder().id(1L).build();
        when(memberRechargeRecordMapper.selectById(1L)).thenReturn(mockEntity);

        MemberRechargeRecordEntity result = memberRechargeRecordService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberRechargeRecordMapper.selectById(999L)).thenReturn(null);

        MemberRechargeRecordEntity result = memberRechargeRecordService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().build();
        when(memberRechargeRecordMapper.insert(entity)).thenReturn(1);

        boolean result = memberRechargeRecordService.add(entity);

        assertTrue(result);
        verify(memberRechargeRecordMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().build();
        when(memberRechargeRecordMapper.insert(entity)).thenReturn(0);

        boolean result = memberRechargeRecordService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().id(1L).build();
        when(memberRechargeRecordMapper.update(entity)).thenReturn(1);

        boolean result = memberRechargeRecordService.update(entity);

        assertTrue(result);
        verify(memberRechargeRecordMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberRechargeRecordEntity entity = MemberRechargeRecordEntity.builder().id(1L).build();
        when(memberRechargeRecordMapper.update(entity)).thenReturn(0);

        boolean result = memberRechargeRecordService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberRechargeRecordMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberRechargeRecordService.remove(1L);

        assertTrue(result);
        verify(memberRechargeRecordMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberRechargeRecordMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberRechargeRecordService.remove(1L);

        assertFalse(result);
    }
}