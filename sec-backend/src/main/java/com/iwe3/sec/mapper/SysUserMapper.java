package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.SysUserEntity;
import java.util.List;

/**
 * sys_user 表的数据访问接口
 */
@Mapper
public interface SysUserMapper {

    /** 根据用户名查询用户，用于登录 */
    SysUserEntity selectByUsername(@Param("username") String username);

    /** 分页查询员工列表 */
    List<SysUserEntity> selectList(@Param("query") SysUserEntity query);

    /** 根据ID查询员工 */
    SysUserEntity selectById(@Param("id") Long id);

    /** 新增员工 */
    int insert(SysUserEntity entity);

    /** 修改员工 */
    int update(SysUserEntity entity);

    /** 根据ID删除员工（逻辑删除） */
    int deleteById(@Param("id") Long id);
}
