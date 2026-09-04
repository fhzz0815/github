package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.OrdersEntity;
import java.util.List;

/**
 * orders 表的数据访问接口
 */
@Mapper
public interface OrdersMapper {

    /** 分页查询列表 */
    List<OrdersEntity> selectList(@Param("query") OrdersEntity query);

    /** 根据ID查询 */
    OrdersEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(OrdersEntity entity);

    /** 修改 */
    int update(OrdersEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
