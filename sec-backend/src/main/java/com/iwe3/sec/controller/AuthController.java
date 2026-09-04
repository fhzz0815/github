package com.iwe3.sec.controller;

import com.iwe3.sec.common.Result;
import com.iwe3.sec.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供登录、登出等接口
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // 认证业务接口
    private final IAuthService authService;

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String token = authService.login(username, password);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");
        return Result.success(data);
    }

    /** 登出 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
