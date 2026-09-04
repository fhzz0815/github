package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StaffScheduleEntity;
import java.util.List;

/**
 * staff_schedule 表的数据访问接口
 */
@Mapper
public interface StaffScheduleMapper {

    /** 分页查询列表 */
    List<StaffScheduleEntity> selectList(@Param("query") StaffScheduleEntity query);

    /** 根据ID查询 */
    StaffScheduleEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StaffScheduleEntity entity);

    /** 修改 */
    int update(StaffScheduleEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
