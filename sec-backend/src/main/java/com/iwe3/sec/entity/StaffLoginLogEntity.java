package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * staff_login_log 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffLoginLogEntity {

    // 日志ID
    private Long id;
    // 员工ID
    private Long staffId;
    // 登录时间
    private java.util.Date loginTime;
    // 登录IP
    private String loginIp;
    // 登录设备
    private String device;
    // 结果 1成功 0失败
    private Integer loginResult;
    // 失败原因
    private String failReason;
}
