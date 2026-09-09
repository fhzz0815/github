package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * member_balance_record 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBalanceRecordEntity {

    // 记录ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 变动金额（正=收入 负=支出）
    private java.math.BigDecimal changeAmount;
    // 变动后余额
    private java.math.BigDecimal balanceAfter;
    // 变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付
    private String changeType;
    // 来源类型 ORDER订单 RECHARGE充值单等
    private String sourceType;
    // 来源ID
    private Long sourceId;
    // 备注
    private String remark;
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
