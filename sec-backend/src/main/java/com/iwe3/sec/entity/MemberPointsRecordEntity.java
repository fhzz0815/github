package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member_points_record 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPointsRecordEntity {

    // 记录ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 积分变动（正=增加 负=扣减）
    private Integer pointsChange;
    // 变动后积分
    private Integer pointsAfter;
    // 变动类型 CONSUME消费获得 EXCHANGE积分兑换 REFUND退款扣回 ACTIVITY活动赠送
    private String changeType;
    // 来源类型
    private String sourceType;
    // 来源ID
    private Long sourceId;
    // 备注
    private String remark;
    // 创建时间
    private java.util.Date createTime;
}
