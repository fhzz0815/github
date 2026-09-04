package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * stock_check_ingredient 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckIngredientEntity {

    // 盘点单ID
    private Long id;
    // 盘点单号
    private String checkNo;
    // 门店ID
    private Long storeId;
    // 原料ID
    private Long ingredientId;
    // 盘点前库存
    private java.math.BigDecimal stockBefore;
    // 盘点后库存
    private java.math.BigDecimal stockAfter;
    // 差异(盘后-盘前)
    private java.math.BigDecimal diff;
    // 差异原因
    private String reason;
    // 状态 1待审核 2已通过 3已驳回
    private Integer status;
    // 创建人(店长)
    private Long creatorId;
    // 审核人(总店长)
    private Long auditorId;
    // 审核时间
    private java.util.Date auditTime;
    // 审核意见
    private String auditRemark;
    // 创建时间
    private java.util.Date createTime;
}
