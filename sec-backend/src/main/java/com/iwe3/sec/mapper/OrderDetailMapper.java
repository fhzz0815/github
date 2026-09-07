package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.OrderDetailEntity;
import java.util.List;

/**
 * order_detail 表的数据访问接口
 */
@Mapper
public interface OrderDetailMapper {

    /** 分页查询列表 */
    List<OrderDetailEntity> selectList(@Param("query") OrderDetailEntity query);

    /** 根据ID查询 */
    OrderDetailEntity selectById(@Param("id") Long id);

    /** 根据订单ID查询所有明细 */
    List<OrderDetailEntity> selectByOrderId(@Param("orderId") Long orderId);

    /** 批量新增 */
    int insertBatch(List<OrderDetailEntity> list);

    /** 新增 */
    int insert(OrderDetailEntity entity);

    /** 修改 */
    int update(OrderDetailEntity entity);

    /** 更新制作状态 */
    int updateMakeStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);

    /** 根据订单ID删除 */
    int deleteByOrderId(@Param("orderId") Long orderId);
}
