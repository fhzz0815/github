package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.OrderStatusLogEntity;
import java.util.List;

/**
 * order_status_log 表的数据访问接口
 */
@Mapper
public interface OrderStatusLogMapper {

    /** 分页查询列表 */
    List<OrderStatusLogEntity> selectList(@Param("query") OrderStatusLogEntity query);

    /** 根据ID查询 */
    OrderStatusLogEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(OrderStatusLogEntity entity);

    /** 修改 */
    int update(OrderStatusLogEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
