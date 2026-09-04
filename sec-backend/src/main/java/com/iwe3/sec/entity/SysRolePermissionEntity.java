package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * sys_role_permission 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRolePermissionEntity {

    // 主键ID
    private Long id;
    // 角色ID
    private Long roleId;
    // 权限ID
    private Long permissionId;
    // 创建时间
    private java.util.Date createTime;
}
