package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.MemberCouponEntity;
import com.iwe3.sec.mapper.MemberCouponMapper;
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
 * Unit test for MemberCoupon ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class MemberCouponServiceImplTest {

    @Mock
    private MemberCouponMapper memberCouponMapper;

    private MemberCouponServiceImpl memberCouponService;

    @BeforeEach
    void setUp() {
        memberCouponService = new MemberCouponServiceImpl(memberCouponMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        MemberCouponEntity entity1 = MemberCouponEntity.builder().id(1L).build();
        MemberCouponEntity entity2 = MemberCouponEntity.builder().id(2L).build();
        List<MemberCouponEntity> mockList = Arrays.asList(entity1, entity2);
        when(memberCouponMapper.selectList(any())).thenReturn(mockList);

        PageResult<MemberCouponEntity> result = memberCouponService.list(new MemberCouponEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(memberCouponMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<MemberCouponEntity> result = memberCouponService.list(new MemberCouponEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        MemberCouponEntity mockEntity = MemberCouponEntity.builder().id(1L).build();
        when(memberCouponMapper.selectById(1L)).thenReturn(mockEntity);

        MemberCouponEntity result = memberCouponService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(memberCouponMapper.selectById(999L)).thenReturn(null);

        MemberCouponEntity result = memberCouponService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        MemberCouponEntity entity = MemberCouponEntity.builder().build();
        when(memberCouponMapper.insert(entity)).thenReturn(1);

        boolean result = memberCouponService.add(entity);

        assertTrue(result);
        verify(memberCouponMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        MemberCouponEntity entity = MemberCouponEntity.builder().build();
        when(memberCouponMapper.insert(entity)).thenReturn(0);

        boolean result = memberCouponService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        MemberCouponEntity entity = MemberCouponEntity.builder().id(1L).build();
        when(memberCouponMapper.update(entity)).thenReturn(1);

        boolean result = memberCouponService.update(entity);

        assertTrue(result);
        verify(memberCouponMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        MemberCouponEntity entity = MemberCouponEntity.builder().id(1L).build();
        when(memberCouponMapper.update(entity)).thenReturn(0);

        boolean result = memberCouponService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(memberCouponMapper.deleteById(1L)).thenReturn(1);

        boolean result = memberCouponService.remove(1L);

        assertTrue(result);
        verify(memberCouponMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(memberCouponMapper.deleteById(1L)).thenReturn(0);

        boolean result = memberCouponService.remove(1L);

        assertFalse(result);
    }
}