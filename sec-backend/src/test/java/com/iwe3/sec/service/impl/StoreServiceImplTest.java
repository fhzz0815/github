package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.cache.CacheHelper;
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
import java.util.concurrent.Callable;

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

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private CacheHelper cacheHelper;

    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(storeMapper, permissionChecker, cacheHelper);
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
    void testGetById_WhenExists_ShouldReturnEntity() throws Exception {
        StoreEntity mockEntity = StoreEntity.builder().id(1L).build();
        when(storeMapper.selectById(1L)).thenReturn(mockEntity);
        // 让 cacheHelper 执行加载器（实际调用 storeMapper.selectById）
        when(cacheHelper.getOrLoad(anyString(), anyLong(), eq(StoreEntity.class), any()))
                .thenAnswer(invocation -> {
                    Callable<StoreEntity> loader = invocation.getArgument(3);
                    return loader.call();
                });

        StoreEntity result = storeService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() throws Exception {
        // cacheHelper 返回 null 模拟缓存未命中且数据库无数据
        when(cacheHelper.getOrLoad(anyString(), anyLong(), eq(StoreEntity.class), any()))
                .thenReturn(null);

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
