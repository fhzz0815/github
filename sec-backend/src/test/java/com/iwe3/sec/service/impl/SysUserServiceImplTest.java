package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import cn.hutool.crypto.SecureUtil;
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
 * 员工管理业务类的单元测试
 * 使用 Mockito 模拟 Mapper 层，只测试业务逻辑的正确性
 */
@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private JwtUtil jwtUtil;

    private SysUserServiceImpl sysUserService;

    @BeforeEach
    void setUp() {
        sysUserService = new SysUserServiceImpl(sysUserMapper, jwtUtil);
    }

    @Test
    @DisplayName("测试分页查询列表 - 返回数据时密码应被清空")
    void testList_ShouldMaskPassword() {
        // 准备模拟数据
        SysUserEntity user1 = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").password("hashed123").build();
        SysUserEntity user2 = SysUserEntity.builder()
                .id(2L).username("lisi").realName("李四").password("hashed456").build();
        List<SysUserEntity> mockList = Arrays.asList(user1, user2);

        when(sysUserMapper.selectList(any())).thenReturn(mockList);

        // 执行查询
        PageResult<SysUserEntity> result = sysUserService.list(new SysUserEntity(), 1, 10);

        // 验证：返回的列表不为空，且密码字段被清空
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        result.getList().forEach(user -> assertNull(user.getPassword(), "列表返回时密码必须为空"));
    }

    @Test
    @DisplayName("测试分页查询列表 - 无数据时返回空列表")
    void testList_WhenNoData_ShouldReturnEmptyList() {
        when(sysUserMapper.selectList(any())).thenReturn(Collections.emptyList());

        PageResult<SysUserEntity> result = sysUserService.list(new SysUserEntity(), 1, 10);

        assertNotNull(result);
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试根据ID查询 - 用户存在时返回用户且密码为空")
    void testGetById_WhenUserExists_ShouldReturnUserWithoutPassword() {
        SysUserEntity mockUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").password("hashed123").build();
        when(sysUserMapper.selectById(1L)).thenReturn(mockUser);

        SysUserEntity result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
        assertNull(result.getPassword(), "详情返回时密码必须为空");
    }

    @Test
    @DisplayName("测试根据ID查询 - 用户不存在时返回null")
    void testGetById_WhenUserNotExists_ShouldReturnNull() {
        when(sysUserMapper.selectById(999L)).thenReturn(null);

        SysUserEntity result = sysUserService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("测试新增用户 - 有密码时进行MD5加密后入库")
    void testAdd_WithPassword_ShouldEncryptPassword() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").password("123456").build();

        sysUserService.add(newUser);

        // 验证密码被MD5加密
        String expectedPwd = SecureUtil.md5("123456");
        assertEquals(expectedPwd, newUser.getPassword(), "密码应被MD5加密");
        verify(sysUserMapper).insert(newUser);
    }

    @Test
    @DisplayName("测试新增用户 - 未填密码时使用默认密码123456")
    void testAdd_WithoutPassword_ShouldUseDefaultPassword() {
        SysUserEntity newUser = SysUserEntity.builder()
                .username("newuser").realName("新用户").build();

        sysUserService.add(newUser);

        // 验证默认密码被MD5加密
        String expectedPwd = SecureUtil.md5("123456");
        assertEquals(expectedPwd, newUser.getPassword(), "未填密码时应使用默认密码123456的MD5值");
        verify(sysUserMapper).insert(newUser);
    }

    @Test
    @DisplayName("测试修改用户 - 同时修改密码时进行MD5加密")
    void testUpdate_WithPassword_ShouldEncryptPassword() {
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").password("newpwd123").build();

        sysUserService.update(updateUser);

        String expectedPwd = SecureUtil.md5("newpwd123");
        assertEquals(expectedPwd, updateUser.getPassword(), "修改密码时需MD5加密");
        verify(sysUserMapper).update(updateUser);
    }

    @Test
    @DisplayName("测试修改用户 - 未修改密码时不应加密")
    void testUpdate_WithoutPassword_ShouldNotEncrypt() {
        SysUserEntity updateUser = SysUserEntity.builder()
                .id(1L).realName("新名字").password(null).build();

        sysUserService.update(updateUser);

        // 密码为空字符串时不应加密
        assertNull(updateUser.getPassword());
        verify(sysUserMapper).update(updateUser);
    }

    @Test
    @DisplayName("测试删除用户 - 调用Mapper的deleteById方法")
    void testRemove_ShouldCallDeleteById() {
        when(sysUserMapper.deleteById(1L)).thenReturn(1);

        boolean result = sysUserService.remove(1L);

        assertTrue(result);
        verify(sysUserMapper).deleteById(1L);
    }

    @Test
    @DisplayName("测试登录 - 账号不存在时抛出业务异常")
    void testLogin_WhenUserNotExists_ShouldThrowException() {
        when(sysUserMapper.selectByUsername("nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("nonexistent", "123456"));

        assertEquals(1002, exception.getCode());
        assertEquals("账号不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号被禁用时抛出业务异常")
    void testLogin_WhenUserDisabled_ShouldThrowException() {
        SysUserEntity disabledUser = SysUserEntity.builder()
                .id(1L).username("disabled").password("hashed").isDeleted(1).build();
        when(sysUserMapper.selectByUsername("disabled")).thenReturn(disabledUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("disabled", "123456"));

        assertEquals(1003, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 密码错误时抛出业务异常")
    void testLogin_WhenPasswordWrong_ShouldThrowException() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd")).isDeleted(0).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> sysUserService.login("zhangsan", "wrongpwd"));

        assertEquals(1004, exception.getCode());
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号密码正确时返回JWT令牌")
    void testLogin_WhenCredentialsCorrect_ShouldReturnToken() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd")).isDeleted(0).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "zhangsan")).thenReturn("mock-jwt-token");

        String token = sysUserService.login("zhangsan", "correctpwd");

        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
        verify(jwtUtil).generateAccessToken(1L, "zhangsan");
    }
}
