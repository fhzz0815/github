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
 * staff_schedule 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffScheduleEntity {

    // 排班ID
    private Long id;
    // 员工ID
    private Long staffId;
    // 排班日期（只取年月日）
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private java.util.Date workDate;
    // 班次 1早班 2中班 3晚班
    private Integer shiftType;
    // 上班时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date startTime;
    // 下班时间（只取时分秒）
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private java.util.Date endTime;
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
