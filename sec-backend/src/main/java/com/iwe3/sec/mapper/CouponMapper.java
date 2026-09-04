package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.CouponEntity;
import java.util.List;

/**
 * coupon 表的数据访问接口
 */
@Mapper
public interface CouponMapper {

    /** 分页查询列表 */
    List<CouponEntity> selectList(@Param("query") CouponEntity query);

    /** 根据ID查询 */
    CouponEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(CouponEntity entity);

    /** 修改 */
    int update(CouponEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
