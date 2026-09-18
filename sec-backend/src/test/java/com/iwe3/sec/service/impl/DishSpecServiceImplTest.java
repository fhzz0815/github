package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.cache.CacheHelper;
import com.iwe3.sec.entity.DishSpecEntity;
import com.iwe3.sec.mapper.DishSpecMapper;
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
 * Unit test for DishSpec ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class DishSpecServiceImplTest {

    @Mock
    private DishSpecMapper dishSpecMapper;

    @Mock
    private CacheHelper cacheHelper;

    private DishSpecServiceImpl dishSpecService;

    @BeforeEach
    void setUp() {
        dishSpecService = new DishSpecServiceImpl(dishSpecMapper, cacheHelper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        DishSpecEntity entity1 = DishSpecEntity.builder().id(1L).build();
        DishSpecEntity entity2 = DishSpecEntity.builder().id(2L).build();
        List<DishSpecEntity> mockList = Arrays.asList(entity1, entity2);
        when(dishSpecMapper.selectList(any())).thenReturn(mockList);

        PageResult<DishSpecEntity> result = dishSpecService.list(new DishSpecEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(dishSpecMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<DishSpecEntity> result = dishSpecService.list(new DishSpecEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() throws Exception {
        DishSpecEntity mockEntity = DishSpecEntity.builder().id(1L).build();
        when(dishSpecMapper.selectById(1L)).thenReturn(mockEntity);
        // 让 cacheHelper 执行加载器（实际调用 dishSpecMapper.selectById）
        when(cacheHelper.getOrLoad(anyString(), anyLong(), eq(DishSpecEntity.class), any()))
                .thenAnswer(invocation -> {
                    Callable<DishSpecEntity> loader = invocation.getArgument(3);
                    return loader.call();
                });

        DishSpecEntity result = dishSpecService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() throws Exception {
        // cacheHelper 返回 null 模拟缓存未命中且数据库无数据
        when(cacheHelper.getOrLoad(anyString(), anyLong(), eq(DishSpecEntity.class), any()))
                .thenReturn(null);

        DishSpecEntity result = dishSpecService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        DishSpecEntity entity = DishSpecEntity.builder().build();
        when(dishSpecMapper.insert(entity)).thenReturn(1);

        boolean result = dishSpecService.add(entity);

        assertTrue(result);
        verify(dishSpecMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        DishSpecEntity entity = DishSpecEntity.builder().build();
        when(dishSpecMapper.insert(entity)).thenReturn(0);

        boolean result = dishSpecService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        DishSpecEntity entity = DishSpecEntity.builder().id(1L).build();
        when(dishSpecMapper.update(entity)).thenReturn(1);

        boolean result = dishSpecService.update(entity);

        assertTrue(result);
        verify(dishSpecMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        DishSpecEntity entity = DishSpecEntity.builder().id(1L).build();
        when(dishSpecMapper.update(entity)).thenReturn(0);

        boolean result = dishSpecService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(dishSpecMapper.deleteById(1L)).thenReturn(1);

        boolean result = dishSpecService.remove(1L);

        assertTrue(result);
        verify(dishSpecMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(dishSpecMapper.deleteById(1L)).thenReturn(0);

        boolean result = dishSpecService.remove(1L);

        assertFalse(result);
    }
}
