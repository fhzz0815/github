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

    /** 释放优惠券（取消订单时将优惠券状态回退为未使用） */
    int releaseCoupon(@Param("id") Long id, @Param("orderId") Long orderId);

    /** 按订单号释放优惠券（当不确定 member_coupon.id 时使用） */
    int releaseCouponByOrder(@Param("orderId") Long orderId);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
