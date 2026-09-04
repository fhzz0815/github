package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import java.util.List;

/**
 * member_balance_record 表的数据访问接口
 */
@Mapper
public interface MemberBalanceRecordMapper {

    /** 分页查询列表 */
    List<MemberBalanceRecordEntity> selectList(@Param("query") MemberBalanceRecordEntity query);

    /** 根据ID查询 */
    MemberBalanceRecordEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberBalanceRecordEntity entity);

    /** 修改 */
    int update(MemberBalanceRecordEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
