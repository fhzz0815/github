package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.SysRolePermissionEntity;
import java.util.List;

/**
 * sys_role_permission 表的数据访问接口
 */
@Mapper
public interface SysRolePermissionMapper {

    /** 分页查询列表 */
    List<SysRolePermissionEntity> selectList(@Param("query") SysRolePermissionEntity query);

    /** 根据ID查询 */
    SysRolePermissionEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(SysRolePermissionEntity entity);

    /** 修改 */
    int update(SysRolePermissionEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
