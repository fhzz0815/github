package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.StoreEntity;
import com.iwe3.sec.mapper.StoreMapper;
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
 * Unit test for Store ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreMapper storeMapper;

    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(storeMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        StoreEntity entity1 = StoreEntity.builder().id(1L).build();
        StoreEntity entity2 = StoreEntity.builder().id(2L).build();
        List<StoreEntity> mockList = Arrays.asList(entity1, entity2);
        when(storeMapper.selectList(any())).thenReturn(mockList);

        PageResult<StoreEntity> result = storeService.list(new StoreEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(storeMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<StoreEntity> result = storeService.list(new StoreEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        StoreEntity mockEntity = StoreEntity.builder().id(1L).build();
        when(storeMapper.selectById(1L)).thenReturn(mockEntity);

        StoreEntity result = storeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(storeMapper.selectById(999L)).thenReturn(null);

        StoreEntity result = storeService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        StoreEntity entity = StoreEntity.builder().build();
        when(storeMapper.insert(entity)).thenReturn(1);

        boolean result = storeService.add(entity);

        assertTrue(result);
        verify(storeMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        StoreEntity entity = StoreEntity.builder().build();
        when(storeMapper.insert(entity)).thenReturn(0);

        boolean result = storeService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        StoreEntity entity = StoreEntity.builder().id(1L).build();
        when(storeMapper.update(entity)).thenReturn(1);

        boolean result = storeService.update(entity);

        assertTrue(result);
        verify(storeMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        StoreEntity entity = StoreEntity.builder().id(1L).build();
        when(storeMapper.update(entity)).thenReturn(0);

        boolean result = storeService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(storeMapper.deleteById(1L)).thenReturn(1);

        boolean result = storeService.remove(1L);

        assertTrue(result);
        verify(storeMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(storeMapper.deleteById(1L)).thenReturn(0);

        boolean result = storeService.remove(1L);

        assertFalse(result);
    }
}