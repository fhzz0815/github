package com.iwe3.sec.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
