package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StaffLoginLogEntity;
import java.util.List;

/**
 * staff_login_log 表的数据访问接口
 */
@Mapper
public interface StaffLoginLogMapper {

    /** 分页查询列表 */
    List<StaffLoginLogEntity> selectList(@Param("query") StaffLoginLogEntity query);

    /** 根据ID查询 */
    StaffLoginLogEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StaffLoginLogEntity entity);

    /** 修改 */
    int update(StaffLoginLogEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
