package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 员工管理控制器的单元测试
 * 验证控制器调用 Service 层的方法是否正确传递参数
 */
@ExtendWith(MockitoExtension.class)
class SysUserControllerTest {

    @Mock
    private ISysUserService sysUserService;

    private SysUserController controller;

    @BeforeEach
    void setUp() {
        controller = new SysUserController(sysUserService);
    }

    @Test
    @DisplayName("分页查询 - 正确调用 Service 层并返回分页数据")
    void testList_ShouldCallServiceAndReturnPageResult() {
        SysUserEntity sampleUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").build();
        List<SysUserEntity> userList = Arrays.asList(sampleUser);
        PageResult<SysUserEntity> pageResult = PageResult.of(1L, 1, userList);
        when(sysUserService.list(any(), anyInt(), anyInt())).thenReturn(pageResult);

        var result = controller.list(new SysUserEntity(), 1, 10);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getTotal());
        assertEquals(1, result.getData().getList().size());
        assertEquals("zhangsan", result.getData().getList().get(0).getUsername());
        verify(sysUserService).list(any(), eq(1), eq(10));
    }

    @Test
    @DisplayName("根据ID查询 - 用户存在时返回用户信息")
    void testGetById_WhenUserExists_ShouldReturnUser() {
        SysUserEntity sampleUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").build();
        when(sysUserService.getById(1L)).thenReturn(sampleUser);

        var result = controller.getById(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNotNull(result.getData());
        assertEquals("zhangsan", result.getData().getUsername());
    }

    @Test
    @DisplayName("根据ID查询 - 用户不存在时返回空数据")
    void testGetById_WhenUserNotExists_ShouldReturnNullData() {
        when(sysUserService.getById(999L)).thenReturn(null);

        var result = controller.getById(999L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("新增用户 - 正确调用 Service 层")
    void testAdd_ShouldCallService() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").password("123456").build();

        var result = controller.add(newUser);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(sysUserService).add(newUser);
    }

    @Test
    @DisplayName("修改用户 - 正确调用 Service 层，并设置ID")
    void testUpdate_ShouldCallServiceWithId() {
        SysUserEntity updateUser = SysUserEntity.builder()
                .realName("新名字").build();

        var result = controller.update(1L, updateUser);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        // 验证 Controller 设置了 ID 后才调用 Service
        assertEquals(1L, updateUser.getId());
        verify(sysUserService).update(updateUser);
    }

    @Test
    @DisplayName("删除用户 - 正确调用 Service 层")
    void testDelete_ShouldCallService() {
        var result = controller.remove(1L);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        verify(sysUserService).remove(1L);
    }
}
