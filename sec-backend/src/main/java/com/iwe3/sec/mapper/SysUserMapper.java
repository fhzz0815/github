package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.common.UserDataScope;
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

    /** 按数据范围（门店+等级）分页查询员工，同时联表回显角色/门店信息 */
    List<SysUserEntity> selectListByDataScope(@Param("query") SysUserEntity query,
                                              @Param("scope") UserDataScope scope);

    /** 根据ID查询员工（联表回显角色/门店信息） */
    SysUserEntity selectById(@Param("id") Long id);

    /** 新增员工 */
    int insert(SysUserEntity entity);

    /** 修改员工 */
    int update(SysUserEntity entity);

    /** 根据ID删除员工（逻辑删除） */
    int deleteById(@Param("id") Long id);

    /** 更新用户最后登录时间（登录成功后调用，精确到秒） */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") java.util.Date lastLoginTime);

    /** 更新用户密码（MD5 升级 bcrypt 时调用） */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
