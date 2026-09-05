package com.iwe3.sec.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwe3.sec.entity.SysRoleEntity;
import com.iwe3.sec.entity.SysUserEntity;
import com.iwe3.sec.mapper.SysRoleMapper;
import com.iwe3.sec.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT 登录认证拦截器
 * 校验请求头中的令牌，未携带或无效则拒绝访问
 * 校验通过后把 LoginUser 装入请求域，供业务层权限判断使用
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    // JSON 序列化工具，用来输出错误信息
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtUtil jwtUtil, SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper) {
        this.jwtUtil = jwtUtil;
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /** 白名单：无需登录即可访问的路径 */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/druid"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 白名单放行
        for (String white : WHITE_LIST) {
            if (uri.startsWith(white)) {
                return true;
            }
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        // 解析用户ID，并查库装载完整登录人信息
        Long userId = jwtUtil.getUserId(token);
        SysUserEntity user = sysUserMapper.selectById(userId);
        if (user == null || (user.getIsDeleted() != null && user.getIsDeleted() == 1)) {
            writeError(response, 401, "账号不存在或已删除，请重新登录");
            return false;
        }
        SysRoleEntity role = user.getRoleId() == null ? null : sysRoleMapper.selectById(user.getRoleId());
        LoginUser loginUser = LoginUser.builder()
                .userId(user.getId())
                .storeId(user.getStoreId())
                .roleId(user.getRoleId())
                .roleLevel(role == null ? null : role.getLevel())
                .roleCode(role == null ? null : role.getRoleCode())
                .username(user.getUsername())
                .build();
        // 保留原属性，向后兼容
        request.setAttribute("currentUserId", userId);
        // 新增统一登录人对象
        request.setAttribute("loginUser", loginUser);
        return true;
    }

    private void writeError(HttpServletResponse response, Integer code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, message)));
    }
}
