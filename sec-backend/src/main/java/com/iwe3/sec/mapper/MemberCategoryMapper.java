package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberCategoryEntity;
import java.util.List;

/**
 * member_category 表的数据访问接口
 */
@Mapper
public interface MemberCategoryMapper {

    /** 分页查询列表 */
    List<MemberCategoryEntity> selectList(@Param("query") MemberCategoryEntity query);

    /** 根据ID查询 */
    MemberCategoryEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberCategoryEntity entity);

    /** 修改 */
    int update(MemberCategoryEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
