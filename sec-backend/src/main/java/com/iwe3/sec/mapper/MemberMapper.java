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

    // ========== 钱包相关：原子增减（避免并发读写导致数据不一致） ==========

    /** 原子增加余额（充值）：balance = balance + #{amount} */
    int increaseBalance(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);

    /** 原子减少余额（消费）：balance = balance - #{amount}，同时校验余额足够 */
    int decreaseBalance(@Param("id") Long id, @Param("amount") java.math.BigDecimal amount);

    /** 原子增加可用积分 */
    int increasePoints(@Param("id") Long id, @Param("points") Integer points);

    /** 原子减少可用积分，同时增加总积分 */
    int increaseTotalAndAvailablePoints(@Param("id") Long id, @Param("points") Integer points);

    /** 原子减少可用积分（消费/兑换） */
    int decreaseAvailablePoints(@Param("id") Long id, @Param("points") Integer points);
}
