package com.iwe3.sec.service;

import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.entity.OrderStatusLogEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * orders 表的业务接口
 */
public interface IOrdersService {

    /** 分页查询列表 */
    PageResult<OrdersEntity> list(OrdersEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    OrdersEntity getById(Long id);

    /** 新增 */
    boolean add(OrdersEntity entity);

    /** 修改 */
    boolean update(OrdersEntity entity);

    /** 删除 */
    boolean remove(Long id);

    // ========== 业务接口 ==========

    /**
     * 提交订单（从购物车/服务员点餐）
     * @param entity 订单基本信息
     * @param details 订单明细（菜品列表）
     * @param operatorId 操作员工ID（服务员/收银）
     * @return 生成的订单ID
     */
    Long submitOrder(OrdersEntity entity, List<OrderDetailEntity> details, Long operatorId);

    /**
     * 支付订单
     * @param orderId 订单ID
     * @param payType 支付类型 WECHAT/ALIPAY/MEMBER_BALANCE/CASH
     * @param actualAmount 实际支付金额
     * @param memberPayAmount 会员余额支付金额
     * @param idempotencyKey 幂等键（防止重复支付）
     * @param operatorId 操作员工ID
     */
    void payOrder(Long orderId, String payType, BigDecimal actualAmount,
                  BigDecimal memberPayAmount, String idempotencyKey, Long operatorId);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param reason 取消原因
     * @param operatorId 操作员工ID
     */
    void cancelOrder(Long orderId, String reason, Long operatorId);

    /**
     * 更新订单制作状态（后厨用）
     * @param orderId 订单ID
     * @param detailId 明细ID（可空，空表示整单）
     * @param makeStatus 制作状态 1待制作 2制作中 3已上齐
     * @param operatorId 操作员工ID
     */
    void updateMakeStatus(Long orderId, Long detailId, Integer makeStatus, Long operatorId);

    /**
     * 完成订单（制作中/配送中/待自取 → 已完成）
     * @param orderId 订单ID
     * @param operatorId 操作员工ID
     */
    void completeOrder(Long orderId, Long operatorId);

    /**
     * 退款订单（已支付的订单 → 已退款）
     * @param orderId 订单ID
     * @param reason 退款原因
     * @param operatorId 操作员工ID
     */
    void refundOrder(Long orderId, String reason, Long operatorId);

    /**
     * 获取订单详情（含明细列表）
     */
    OrdersEntity getOrderWithDetails(Long orderId);

    /**
     * 获取订单状态日志
     */
    List<OrderStatusLogEntity> getOrderStatusLogs(Long orderId);

    /**
     * 根据订单ID查询订单明细列表
     */
    List<OrderDetailEntity> getOrderDetailsByOrderId(Long orderId);

    /**
     * 获取后厨看板数据（待制作/制作中的订单）
     */
    List<OrdersEntity> getKitchenOrders(Long storeId);

    /**
     * 获取销售报表（按门店+日期范围）
     */
    Map<String, Object> getSalesReport(Long storeId, String beginDate, String endDate);

    /**
     * 获取今日概况统计数据
     */
    Map<String, Object> getTodaySummary(Long storeId);
}
