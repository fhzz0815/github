package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MemberCouponEntity;
import java.util.List;

/**
 * member_coupon 表的数据访问接口
 */
@Mapper
public interface MemberCouponMapper {

    /** 分页查询列表 */
    List<MemberCouponEntity> selectList(@Param("query") MemberCouponEntity query);

    /** 根据ID查询 */
    MemberCouponEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(MemberCouponEntity entity);

    /** 修改 */
    int update(MemberCouponEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
