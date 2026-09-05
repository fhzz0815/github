package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.CartEntity;
import com.iwe3.sec.mapper.CartMapper;
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
 * Unit test for Cart ServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartMapper cartMapper;

    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartMapper);
    }

    @Test
    @DisplayName("List with data should return page result")
    void testList_ShouldReturnPageResult() {
        CartEntity entity1 = CartEntity.builder().id(1L).build();
        CartEntity entity2 = CartEntity.builder().id(2L).build();
        List<CartEntity> mockList = Arrays.asList(entity1, entity2);
        when(cartMapper.selectList(any())).thenReturn(mockList);

        PageResult<CartEntity> result = cartService.list(new CartEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("List with no data should return empty list")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(cartMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<CartEntity> result = cartService.list(new CartEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("Get by id when exists should return entity")
    void testGetById_WhenExists_ShouldReturnEntity() {
        CartEntity mockEntity = CartEntity.builder().id(1L).build();
        when(cartMapper.selectById(1L)).thenReturn(mockEntity);

        CartEntity result = cartService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Get by id when not exists should return null")
    void testGetById_WhenNotExists_ShouldReturnNull() {
        when(cartMapper.selectById(999L)).thenReturn(null);

        CartEntity result = cartService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("Add should return true on success")
    void testAdd_ShouldReturnTrue() {
        CartEntity entity = CartEntity.builder().build();
        when(cartMapper.insert(entity)).thenReturn(1);

        boolean result = cartService.add(entity);

        assertTrue(result);
        verify(cartMapper).insert(entity);
    }

    @Test
    @DisplayName("Add should return false on failure")
    void testAdd_WhenFailed_ShouldReturnFalse() {
        CartEntity entity = CartEntity.builder().build();
        when(cartMapper.insert(entity)).thenReturn(0);

        boolean result = cartService.add(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Update should return true on success")
    void testUpdate_ShouldReturnTrue() {
        CartEntity entity = CartEntity.builder().id(1L).build();
        when(cartMapper.update(entity)).thenReturn(1);

        boolean result = cartService.update(entity);

        assertTrue(result);
        verify(cartMapper).update(entity);
    }

    @Test
    @DisplayName("Update should return false on failure")
    void testUpdate_WhenFailed_ShouldReturnFalse() {
        CartEntity entity = CartEntity.builder().id(1L).build();
        when(cartMapper.update(entity)).thenReturn(0);

        boolean result = cartService.update(entity);

        assertFalse(result);
    }

    @Test
    @DisplayName("Remove should return true on success")
    void testRemove_ShouldReturnTrue() {
        when(cartMapper.deleteById(1L)).thenReturn(1);

        boolean result = cartService.remove(1L);

        assertTrue(result);
        verify(cartMapper).deleteById(1L);
    }

    @Test
    @DisplayName("Remove should return false on failure")
    void testRemove_WhenFailed_ShouldReturnFalse() {
        when(cartMapper.deleteById(1L)).thenReturn(0);

        boolean result = cartService.remove(1L);

        assertFalse(result);
    }
}