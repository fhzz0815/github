package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * refund 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundEntity {

    // 退单ID
    private Long id;
    // 退单号
    private String refundNo;
    // 订单ID
    private Long orderId;
    // 订单明细ID(退菜时)
    private Long orderDetailId;
    // 门店ID
    private Long storeId;
    // 客户ID
    private Long memberId;
    // 退单类型 1整单退 2退菜
    private Integer refundType;
    // 退菜名称
    private String dishName;
    // 退菜数量
    private Integer quantity;
    // 退款金额(元)
    private java.math.BigDecimal amount;
    // 退单原因
    private String reason;
    // 状态 1待审核 2已通过 3已驳回 4已完成
    private Integer status;
    // 操作员工ID
    private Long operatorId;
    // 退款时间
    private java.util.Date refundTime;
    // 创建时间
    private java.util.Date createTime;

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
