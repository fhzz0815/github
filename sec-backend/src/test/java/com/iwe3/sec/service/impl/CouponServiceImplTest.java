package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.CouponEntity;
import com.iwe3.sec.mapper.CouponMapper;
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
 * Unit test for Coupon ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponMapper couponMapper;

    private CouponServiceImpl couponService;

    @BeforeEach
    void setUp() {
        couponService = new CouponServiceImpl(couponMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        CouponEntity entity1 = CouponEntity.builder().id(1L).build();
        CouponEntity entity2 = CouponEntity.builder().id(2L).build();
        List<CouponEntity> mockList = Arrays.asList(entity1, entity2);
        when(couponMapper.selectList(any())).thenReturn(mockList);

        PageResult<CouponEntity> result = couponService.list(new CouponEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(couponMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<CouponEntity> result = couponService.list(new CouponEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        CouponEntity mockEntity = CouponEntity.builder().id(1L).build();
        when(couponMapper.selectById(1L)).thenReturn(mockEntity);

        CouponEntity result = couponService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(couponMapper.selectById(999L)).thenReturn(null);

        CouponEntity result = couponService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        CouponEntity entity = CouponEntity.builder().build();
        when(couponMapper.insert(entity)).thenReturn(1);

        boolean result = couponService.add(entity);

        assertTrue(result);
        verify(couponMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        CouponEntity entity = CouponEntity.builder().build();
        when(couponMapper.insert(entity)).thenReturn(0);

        boolean result = couponService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        CouponEntity entity = CouponEntity.builder().id(1L).build();
        when(couponMapper.update(entity)).thenReturn(1);

        boolean result = couponService.update(entity);

        assertTrue(result);
        verify(couponMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        CouponEntity entity = CouponEntity.builder().id(1L).build();
        when(couponMapper.update(entity)).thenReturn(0);

        boolean result = couponService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(couponMapper.deleteById(1L)).thenReturn(1);

        boolean result = couponService.remove(1L);

        assertTrue(result);
        verify(couponMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(couponMapper.deleteById(1L)).thenReturn(0);

        boolean result = couponService.remove(1L);

        assertFalse(result);
    }
}