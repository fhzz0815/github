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

    /** 新增 */
    int insert(OrderDetailEntity entity);

    /** 修改 */
    int update(OrderDetailEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
