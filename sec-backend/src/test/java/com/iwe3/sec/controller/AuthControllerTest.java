package com.iwe3.sec.controller;

import com.iwe3.sec.common.Result;
import com.iwe3.sec.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 认证控制器的单元测试
 * 测试登录、登出等接口
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(authService);
    }

    @Test
    @DisplayName("登录 - 账号密码正确时返回令牌")
    void testLogin_ShouldReturnToken() {
        Map<String, String> params = new HashMap<>();
        params.put("username", "admin");
        params.put("password", "123456");
        when(authService.login("admin", "123456")).thenReturn("mock-jwt-token");

        Result<Map<String, Object>> result = controller.login(params);

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("mock-jwt-token", result.getData().get("token"));
        assertEquals("Bearer", result.getData().get("tokenType"));
        verify(authService).login("admin", "123456");
    }

    @Test
    @DisplayName("登出 - 成功返回")
    void testLogout_ShouldSucceed() {
        Result<Void> result = controller.logout();

        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMessage());
    }
}