package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.SysPermissionEntity;
import java.util.List;

/**
 * sys_permission 表的数据访问接口
 */
@Mapper
public interface SysPermissionMapper {

    /** 分页查询列表 */
    List<SysPermissionEntity> selectList(@Param("query") SysPermissionEntity query);

    /** 根据ID查询 */
    SysPermissionEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(SysPermissionEntity entity);

    /** 修改 */
    int update(SysPermissionEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
