package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * orders 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersEntity {

    // 订单ID
    private Long id;
    // 订单号
    private String orderNo;
    // 门店ID
    private Long storeId;
    // 客户ID
    private Long memberId;
    // 订单类型 1堂食 2外卖 3自取
    private Integer orderType;
    // 台桌ID(堂食)
    private Long tableId;
    // 桌型ID(堂食)
    private Long tableTypeId;
    // 排队ID
    private Long queueId;
    // 用餐人数
    private Integer personCount;
    // 订单状态 1待支付 2待制作 3制作中 4待配送 5配送中 6待自取 7已完成 8已取消 9已退款
    private Integer orderStatus;
    // 支付状态 1待支付 2已支付 3已退款 4部分退款
    private Integer payStatus;
    // 外卖取餐方式 1外送 2自取
    private Integer takeType;
    // 收货地址ID
    private Long addressId;
    // 地址快照(下单时保存)
    private String addressSnapshot;
    // 菜品原价合计
    private java.math.BigDecimal dishAmount;
    // 优惠/折扣金额
    private java.math.BigDecimal discountAmount;
    // 免单金额
    private java.math.BigDecimal freeAmount;
    // 抹零金额
    private java.math.BigDecimal roundingAmount;
    // 使用的优惠券ID
    private Long couponId;
    // 优惠券抵扣金额
    private java.math.BigDecimal couponAmount;
    // 配送费
    private java.math.BigDecimal deliveryFee;
    // 应付金额
    private java.math.BigDecimal payableAmount;
    // 实付金额
    private java.math.BigDecimal actualAmount;
    // 会员余额支付金额
    private java.math.BigDecimal memberPayAmount;
    // 订单来源 1客户端 2服务员 3收银
    private Integer source;
    // 操作员工ID
    private Long operatorId;
    // 订单备注
    private String remark;
    // 下单时间
    private java.util.Date orderTime;
    // 支付时间
    private java.util.Date payTime;
    // 预计送达/自取时间
    private java.util.Date expectedTime;
    // 配送/出餐时间
    private java.util.Date shippingTime;
    // 乐观锁版本号（防止状态并发覆盖）
    private Integer version;
    // 完成时间
    private java.util.Date finishTime;
    // 取消时间
    private java.util.Date cancelTime;
    // 取消原因
    private String cancelReason;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    @JsonIgnore
    private Integer isDeleted;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String searchEndTime;
}
