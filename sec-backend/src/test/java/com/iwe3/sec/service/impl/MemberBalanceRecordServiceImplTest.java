package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.mapper.MemberBalanceRecordMapper;
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
 * Unit test for MemberBalanceRecord ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberBalanceRecordServiceImplTest {

    @Mock
    private MemberBalanceRecordMapper memberBalanceRecordMapper;

    private MemberBalanceRecordServiceImpl memberBalanceRecordService;

    @BeforeEach
    void setUp() {
        memberBalanceRecordService = new MemberBalanceRecordServiceImpl(memberBalanceRecordMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberBalanceRecordEntity entity1 = MemberBalanceRecordEntity.builder().id(1L).build();
        MemberBalanceRecordEntity entity2 = MemberBalanceRecordEntity.builder().id(2L).build();
        List<MemberBalanceRecordEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberBalanceRecordMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberBalanceRecordEntity> result = memberBalanceRecordService.list(new MemberBalanceRecordEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberBalanceRecordMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberBalanceRecordEntity> result = memberBalanceRecordService.list(new MemberBalanceRecordEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberBalanceRecordEntity mockEntity = MemberBalanceRecordEntity.builder().id(1L).build();
        when(memberBalanceRecordMapper.selectById(1L)).thenReturn(mockEntity);

        MemberBalanceRecordEntity result = memberBalanceRecordService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberBalanceRecordMapper.selectById(999L)).thenReturn(null);

        MemberBalanceRecordEntity result = memberBalanceRecordService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberBalanceRecordEntity entity = MemberBalanceRecordEntity.builder().build();
        when(memberBalanceRecordMapper.insert(entity)).thenReturn(1);

        boolean result = memberBalanceRecordService.add(entity);

        assertTrue(result);
        verify(memberBalanceRecordMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberBalanceRecordEntity entity = MemberBalanceRecordEntity.builder().build();
        when(memberBalanceRecordMapper.insert(entity)).thenReturn(0);

        boolean result = memberBalanceRecordService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberBalanceRecordEntity entity = MemberBalanceRecordEntity.builder().id(1L).build();
        when(memberBalanceRecordMapper.update(entity)).thenReturn(1);

        boolean result = memberBalanceRecordService.update(entity);

        assertTrue(result);
        verify(memberBalanceRecordMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberBalanceRecordEntity entity = MemberBalanceRecordEntity.builder().id(1L).build();
        when(memberBalanceRecordMapper.update(entity)).thenReturn(0);

        boolean result = memberBalanceRecordService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberBalanceRecordMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberBalanceRecordService.remove(1L);

        assertTrue(result);
        verify(memberBalanceRecordMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberBalanceRecordMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberBalanceRecordService.remove(1L);

        assertFalse(result);
    }
}