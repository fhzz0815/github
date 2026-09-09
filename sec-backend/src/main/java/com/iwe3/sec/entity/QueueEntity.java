package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
