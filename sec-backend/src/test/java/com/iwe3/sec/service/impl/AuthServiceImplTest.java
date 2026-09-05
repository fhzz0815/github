package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.JwtUtil;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysUserMapper;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 认证业务类的单元测试
 * 测试登录时的账号验证、密码校验、令牌生成逻辑
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(sysUserMapper, jwtUtil);
    }

    @Test
    @DisplayName("测试登录 - 账号不存在时抛出异常")
    void testLogin_WhenUserNotExists_ShouldThrowException() {
        when(sysUserMapper.selectByUsername("nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login("nonexistent", "123456"));

        assertEquals(1002, exception.getCode());
        assertEquals("账号不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号已删除（禁用）时抛出异常")
    void testLogin_WhenUserDeleted_ShouldThrowException() {
        SysUserEntity deletedUser = SysUserEntity.builder()
                .id(1L).username("deleted").password("hashed").isDeleted(1).build();
        when(sysUserMapper.selectByUsername("deleted")).thenReturn(deletedUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login("deleted", "123456"));

        assertEquals(1003, exception.getCode());
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 账号被停用（status=0）时抛出异常")
    void testLogin_WhenUserDeactivated_ShouldThrowException() {
        SysUserEntity deactivatedUser = SysUserEntity.builder()
                .id(1L).username("deactivated").password("hashed").isDeleted(0).status(0).build();
        when(sysUserMapper.selectByUsername("deactivated")).thenReturn(deactivatedUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login("deactivated", "123456"));

        assertEquals(1005, exception.getCode());
        assertEquals("账号已被停用", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 密码错误时抛出异常")
    void testLogin_WhenPasswordWrong_ShouldThrowException() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd"))
                .isDeleted(0).status(1).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.login("zhangsan", "wrongpwd"));

        assertEquals(1004, exception.getCode());
        assertEquals("密码错误", exception.getMessage());
    }

    @Test
    @DisplayName("测试登录 - 使用MD5密码，账号密码正确时返回JWT令牌")
    void testLogin_WithMd5Password_ShouldReturnToken() {
        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("zhangsan").password(SecureUtil.md5("correctpwd"))
                .isDeleted(0).status(1).build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "zhangsan")).thenReturn("mock-jwt-token");

        String token = authService.login("zhangsan", "correctpwd");

        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
        verify(jwtUtil).generateAccessToken(1L, "zhangsan");
    }

    @Test
    @DisplayName("测试登录 - 使用BCrypt密码，密码正确时返回JWT令牌")
    void testLogin_WithBcryptPassword_ShouldReturnToken() {
        // 生成一个真实的 BCrypt 加密密码，确保测试时能正确匹配
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "testPwd123";
        String bcryptPwd = encoder.encode(rawPassword);

        SysUserEntity user = SysUserEntity.builder()
                .id(1L).username("lisi").password(bcryptPwd)
                .isDeleted(0).status(1).build();
        when(sysUserMapper.selectByUsername("lisi")).thenReturn(user);
        when(jwtUtil.generateAccessToken(1L, "lisi")).thenReturn("mock-jwt-token");

        String token = authService.login("lisi", rawPassword);

        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
        verify(jwtUtil).generateAccessToken(1L, "lisi");
    }

    @Test
    @DisplayName("测试根据用户名查询 - 用户存在时返回用户信息")
    void testGetByUsername_WhenUserExists_ShouldReturnUser() {
        SysUserEntity mockUser = SysUserEntity.builder()
                .id(1L).username("zhangsan").realName("张三").build();
        when(sysUserMapper.selectByUsername("zhangsan")).thenReturn(mockUser);

        SysUserEntity result = authService.getByUsername("zhangsan");

        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
    }

    @Test
    @DisplayName("测试根据用户名查询 - 用户不存在时返回null")
    void testGetByUsername_WhenUserNotExists_ShouldReturnNull() {
        when(sysUserMapper.selectByUsername("nonexistent")).thenReturn(null);

        SysUserEntity result = authService.getByUsername("nonexistent");

        assertNull(result);
    }
}
