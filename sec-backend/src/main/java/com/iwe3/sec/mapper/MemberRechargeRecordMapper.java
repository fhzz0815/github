package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import java.util.List;

/**
 * member_recharge_record 表的数据访问接口
 */
@Mapper
public interface MemberRechargeRecordMapper {

    /** 分页查询列表 */
    List<MemberRechargeRecordEntity> selectList(@Param("query") MemberRechargeRecordEntity query);

    /** 根据ID查询 */
    MemberRechargeRecordEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberRechargeRecordEntity entity);

    /** 修改 */
    int update(MemberRechargeRecordEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
