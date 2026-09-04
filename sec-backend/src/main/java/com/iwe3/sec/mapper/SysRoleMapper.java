package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.SysRoleEntity;
import java.util.List;

/**
 * sys_role 表的数据访问接口
 */
@Mapper
public interface SysRoleMapper {

    /** 分页查询列表 */
    List<SysRoleEntity> selectList(@Param("query") SysRoleEntity query);

    /** 根据ID查询 */
    SysRoleEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(SysRoleEntity entity);

    /** 修改 */
    int update(SysRoleEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
