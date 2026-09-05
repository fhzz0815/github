package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StorePaymentSettingEntity;
import com.iwe3.sec.mapper.StorePaymentSettingMapper;
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
 * Unit test for StorePaymentSetting ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StorePaymentSettingServiceImplTest {

    @Mock
    private StorePaymentSettingMapper storePaymentSettingMapper;

    private StorePaymentSettingServiceImpl storePaymentSettingService;

    @BeforeEach
    void setUp() {
        storePaymentSettingService = new StorePaymentSettingServiceImpl(storePaymentSettingMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StorePaymentSettingEntity entity1 = StorePaymentSettingEntity.builder().id(1L).build();
        StorePaymentSettingEntity entity2 = StorePaymentSettingEntity.builder().id(2L).build();
        List<StorePaymentSettingEntity> mockList = Arrays.asList(entity1, entity2);
        when(storePaymentSettingMapper.selectList(any())).thenReturn(mockList);

        PageResult<StorePaymentSettingEntity> result = storePaymentSettingService.list(new StorePaymentSettingEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(storePaymentSettingMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StorePaymentSettingEntity> result = storePaymentSettingService.list(new StorePaymentSettingEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StorePaymentSettingEntity mockEntity = StorePaymentSettingEntity.builder().id(1L).build();
        when(storePaymentSettingMapper.selectById(1L)).thenReturn(mockEntity);

        StorePaymentSettingEntity result = storePaymentSettingService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(storePaymentSettingMapper.selectById(999L)).thenReturn(null);

        StorePaymentSettingEntity result = storePaymentSettingService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StorePaymentSettingEntity entity = StorePaymentSettingEntity.builder().build();
        when(storePaymentSettingMapper.insert(entity)).thenReturn(1);

        boolean result = storePaymentSettingService.add(entity);

        assertTrue(result);
        verify(storePaymentSettingMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StorePaymentSettingEntity entity = StorePaymentSettingEntity.builder().build();
        when(storePaymentSettingMapper.insert(entity)).thenReturn(0);

        boolean result = storePaymentSettingService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StorePaymentSettingEntity entity = StorePaymentSettingEntity.builder().id(1L).build();
        when(storePaymentSettingMapper.update(entity)).thenReturn(1);

        boolean result = storePaymentSettingService.update(entity);

        assertTrue(result);
        verify(storePaymentSettingMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StorePaymentSettingEntity entity = StorePaymentSettingEntity.builder().id(1L).build();
        when(storePaymentSettingMapper.update(entity)).thenReturn(0);

        boolean result = storePaymentSettingService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(storePaymentSettingMapper.deleteById(1L)).thenReturn(1);

        boolean result = storePaymentSettingService.remove(1L);

        assertTrue(result);
        verify(storePaymentSettingMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(storePaymentSettingMapper.deleteById(1L)).thenReturn(0);

        boolean result = storePaymentSettingService.remove(1L);

        assertFalse(result);
    }
}