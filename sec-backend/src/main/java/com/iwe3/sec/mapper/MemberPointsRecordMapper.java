package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberPointsRecordEntity;
import java.util.List;

/**
 * member_points_record 表的数据访问接口
 */
@Mapper
public interface MemberPointsRecordMapper {

    /** 分页查询列表 */
    List<MemberPointsRecordEntity> selectList(@Param("query") MemberPointsRecordEntity query);

    /** 根据ID查询 */
    MemberPointsRecordEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberPointsRecordEntity entity);

    /** 修改 */
    int update(MemberPointsRecordEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
