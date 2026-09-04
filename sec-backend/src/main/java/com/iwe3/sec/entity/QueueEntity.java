package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * queue 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntity {

    // 排队ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 排队号(如A001)
    private String queueNo;
    // 期望桌型ID
    private Long tableTypeId;
    // 就餐人数
    private Integer persons;
    // 客户ID
    private Long memberId;
    // 联系电话
    private String phone;
    // 状态 1排队中 2已叫号 3已过号 4已就位 5已取消
    private Integer status;
    // 叫号次数
    private Integer callCount;
    // 过号次数
    private Integer overCount;
    // 最近叫号时间
    private java.util.Date callTime;
    // 就位时间
    private java.util.Date seatedTime;
    // 取号时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
}
