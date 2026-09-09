package com.iwe3.sec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * reservation 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    // 预约ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 台桌ID
    private Long tableId;
    // 桌型ID
    private Long tableTypeId;
    // 客户ID
    private Long memberId;
    // 联系人姓名
    private String contactName;
    // 联系电话
    private String contactPhone;
    // 预约日期（只取年月日）
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private java.util.Date reservationDate;
    // 预约时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date reservationTime;
    // 就餐人数
    private Integer persons;
    // 备注
    private String remark;
    // 状态 1待确认 2预约成功 3已取消 4已完成
    private Integer status;
    // 确认时间
    private java.util.Date confirmTime;
    // 完成时间
    private java.util.Date completeTime;
    // 创建时间
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
