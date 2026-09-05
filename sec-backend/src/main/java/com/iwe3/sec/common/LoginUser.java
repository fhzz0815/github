package com.iwe3.sec.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录人信息（每次请求由 JwtInterceptor 一次性装载，避免业务层重复查库）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser {
    // 员工ID
    private Long userId;
    // 所属门店ID（总店长为 null）
    private Long storeId;
    // 角色ID
    private Long roleId;
    // 角色等级（99/50/10）
    private Integer roleLevel;
    // 角色编码
    private String roleCode;
    // 登录账号
    private String username;
}
