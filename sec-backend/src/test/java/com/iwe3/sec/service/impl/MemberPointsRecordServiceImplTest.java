package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberPointsRecordEntity;
import com.iwe3.sec.mapper.MemberPointsRecordMapper;
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
 * Unit test for MemberPointsRecord ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberPointsRecordServiceImplTest {

    @Mock
    private MemberPointsRecordMapper memberPointsRecordMapper;

    private MemberPointsRecordServiceImpl memberPointsRecordService;

    @BeforeEach
    void setUp() {
        memberPointsRecordService = new MemberPointsRecordServiceImpl(memberPointsRecordMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberPointsRecordEntity entity1 = MemberPointsRecordEntity.builder().id(1L).build();
        MemberPointsRecordEntity entity2 = MemberPointsRecordEntity.builder().id(2L).build();
        List<MemberPointsRecordEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberPointsRecordMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberPointsRecordEntity> result = memberPointsRecordService.list(new MemberPointsRecordEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberPointsRecordMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberPointsRecordEntity> result = memberPointsRecordService.list(new MemberPointsRecordEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberPointsRecordEntity mockEntity = MemberPointsRecordEntity.builder().id(1L).build();
        when(memberPointsRecordMapper.selectById(1L)).thenReturn(mockEntity);

        MemberPointsRecordEntity result = memberPointsRecordService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberPointsRecordMapper.selectById(999L)).thenReturn(null);

        MemberPointsRecordEntity result = memberPointsRecordService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberPointsRecordEntity entity = MemberPointsRecordEntity.builder().build();
        when(memberPointsRecordMapper.insert(entity)).thenReturn(1);

        boolean result = memberPointsRecordService.add(entity);

        assertTrue(result);
        verify(memberPointsRecordMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberPointsRecordEntity entity = MemberPointsRecordEntity.builder().build();
        when(memberPointsRecordMapper.insert(entity)).thenReturn(0);

        boolean result = memberPointsRecordService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberPointsRecordEntity entity = MemberPointsRecordEntity.builder().id(1L).build();
        when(memberPointsRecordMapper.update(entity)).thenReturn(1);

        boolean result = memberPointsRecordService.update(entity);

        assertTrue(result);
        verify(memberPointsRecordMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberPointsRecordEntity entity = MemberPointsRecordEntity.builder().id(1L).build();
        when(memberPointsRecordMapper.update(entity)).thenReturn(0);

        boolean result = memberPointsRecordService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberPointsRecordMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberPointsRecordService.remove(1L);

        assertTrue(result);
        verify(memberPointsRecordMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberPointsRecordMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberPointsRecordService.remove(1L);

        assertFalse(result);
    }
}