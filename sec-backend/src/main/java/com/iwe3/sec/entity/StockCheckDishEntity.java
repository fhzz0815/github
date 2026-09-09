package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * stock_check_dish 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckDishEntity {

    // 盘点单ID
    private Long id;
    // 盘点单号
    private String checkNo;
    // 门店ID
    private Long storeId;
    // 菜品ID
    private Long dishId;
    // 盘点前库存
    private Integer stockBefore;
    // 盘点后库存
    private Integer stockAfter;
    // 差异(盘后-盘前)
    private Integer diff;
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
