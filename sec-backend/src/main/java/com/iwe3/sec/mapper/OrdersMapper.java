package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.OrdersEntity;
import java.util.List;
import java.util.Map;

/**
 * orders 表的数据访问接口
 */
@Mapper
public interface OrdersMapper {

    /** 分页查询列表 */
    List<OrdersEntity> selectList(@Param("query") OrdersEntity query);

    /** 根据ID查询 */
    OrdersEntity selectById(@Param("id") Long id);

    /** 根据订单号查询 */
    OrdersEntity selectByOrderNo(@Param("orderNo") String orderNo);

    /** 新增 */
    int insert(OrdersEntity entity);

    /** 修改 */
    int update(OrdersEntity entity);

    /** 更新订单状态 */
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus,
                          @Param("version") Integer version);

    /** 更新支付状态 */
    int updatePayStatus(@Param("id") Long id, @Param("payStatus") Integer payStatus,
                        @Param("orderStatus") Integer orderStatus, @Param("payTime") java.util.Date payTime,
                        @Param("version") Integer version);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);

    /** 查询后厨看板 */
    List<OrdersEntity> selectKitchenOrders(@Param("storeId") Long storeId);

    /** 按门店和状态统计订单数（用于仪表盘计数，避免全量查询） */
    int countByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("orderStatus") Integer orderStatus);

    /** 查询今日概况 */
    Map<String, Object> selectTodaySummary(@Param("storeId") Long storeId);

    /** 查询销售报表（按门店+日期范围） */
    List<Map<String, Object>> selectSalesReport(@Param("storeId") Long storeId,
                                                 @Param("beginDate") String beginDate,
                                                 @Param("endDate") String endDate);
}
