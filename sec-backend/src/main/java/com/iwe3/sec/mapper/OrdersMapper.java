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

    /** 更新订单状态（带版本号和逻辑删除校验，乐观锁） */
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus,
                          @Param("version") Integer version);

    /** 更新支付状态（带版本号乐观锁） */
    int updatePayStatus(@Param("id") Long id, @Param("payStatus") Integer payStatus,
                        @Param("orderStatus") Integer orderStatus, @Param("payTime") java.util.Date payTime,
                        @Param("version") Integer version);

    /**
     * 通用订单状态流转（带版本号乐观锁 + 原状态校验）
     * 只有当前状态等于 fromStatus 且 version 匹配时才会更新
     * @param id 订单ID
     * @param fromStatus 当前状态（用于校验，防止状态已变）
     * @param toStatus 目标状态
     * @param newPayStatus 新的支付状态（不更新则传 null）
     * @param payTime 支付时间（不更新则传 null）
     * @param finishTime 完成时间（不更新则传 null）
     * @param cancelTime 取消时间（不更新则传 null）
     * @param cancelReason 取消原因（不更新则传 null）
     * @param version 乐观锁版本号
     * @return 影响行数（0表示状态已变更）
     */
    int transitionStatus(@Param("id") Long id,
                         @Param("fromStatus") Integer fromStatus,
                         @Param("toStatus") Integer toStatus,
                         @Param("newPayStatus") Integer newPayStatus,
                         @Param("payTime") java.util.Date payTime,
                         @Param("finishTime") java.util.Date finishTime,
                         @Param("cancelTime") java.util.Date cancelTime,
                         @Param("cancelReason") String cancelReason,
                         @Param("version") Integer version);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);

    /** 查询超时未支付的订单（用于自动取消，带分页） */
    List<OrdersEntity> selectTimeoutOrders(@Param("deadline") java.util.Date deadline);

    /** 分页查询超时未支付的订单（每笔独立事务，分页处理避免大事务） */
    List<OrdersEntity> selectTimeoutOrdersPage(@Param("deadline") java.util.Date deadline,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);

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
