package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberAddressEntity;
import java.util.List;

/**
 * member_address 表的数据访问接口
 */
@Mapper
public interface MemberAddressMapper {

    /** 分页查询列表 */
    List<MemberAddressEntity> selectList(@Param("query") MemberAddressEntity query);

    /** 根据ID查询 */
    MemberAddressEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberAddressEntity entity);

    /** 修改 */
    int update(MemberAddressEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
