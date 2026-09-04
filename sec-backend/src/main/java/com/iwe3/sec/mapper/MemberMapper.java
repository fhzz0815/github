package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberEntity;
import java.util.List;

/**
 * member 表的数据访问接口
 */
@Mapper
public interface MemberMapper {

    /** 分页查询列表 */
    List<MemberEntity> selectList(@Param("query") MemberEntity query);

    /** 根据ID查询 */
    MemberEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberEntity entity);

    /** 修改 */
    int update(MemberEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
