package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.ReservationEntity;
import com.iwe3.sec.mapper.ReservationMapper;
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
 * Unit test for Reservation ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationMapper reservationMapper;

    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(reservationMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        ReservationEntity entity1 = ReservationEntity.builder().id(1L).build();
        ReservationEntity entity2 = ReservationEntity.builder().id(2L).build();
        List<ReservationEntity> mockList = Arrays.asList(entity1, entity2);
        when(reservationMapper.selectList(any())).thenReturn(mockList);

        PageResult<ReservationEntity> result = reservationService.list(new ReservationEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(reservationMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<ReservationEntity> result = reservationService.list(new ReservationEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        ReservationEntity mockEntity = ReservationEntity.builder().id(1L).build();
        when(reservationMapper.selectById(1L)).thenReturn(mockEntity);

        ReservationEntity result = reservationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(reservationMapper.selectById(999L)).thenReturn(null);

        ReservationEntity result = reservationService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        ReservationEntity entity = ReservationEntity.builder().build();
        when(reservationMapper.insert(entity)).thenReturn(1);

        boolean result = reservationService.add(entity);

        assertTrue(result);
        verify(reservationMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        ReservationEntity entity = ReservationEntity.builder().build();
        when(reservationMapper.insert(entity)).thenReturn(0);

        boolean result = reservationService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        ReservationEntity entity = ReservationEntity.builder().id(1L).build();
        when(reservationMapper.update(entity)).thenReturn(1);

        boolean result = reservationService.update(entity);

        assertTrue(result);
        verify(reservationMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        ReservationEntity entity = ReservationEntity.builder().id(1L).build();
        when(reservationMapper.update(entity)).thenReturn(0);

        boolean result = reservationService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(reservationMapper.deleteById(1L)).thenReturn(1);

        boolean result = reservationService.remove(1L);

        assertTrue(result);
        verify(reservationMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(reservationMapper.deleteById(1L)).thenReturn(0);

        boolean result = reservationService.remove(1L);

        assertFalse(result);
    }
}