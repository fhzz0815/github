package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberBankCardEntity;
import java.util.List;

/**
 * member_bank_card 表的数据访问接口
 */
@Mapper
public interface MemberBankCardMapper {

    /** 分页查询列表 */
    List<MemberBankCardEntity> selectList(@Param("query") MemberBankCardEntity query);

    /** 根据ID查询 */
    MemberBankCardEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberBankCardEntity entity);

    /** 修改 */
    int update(MemberBankCardEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
