package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * sys_user 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserEntity {

    // 员工ID
    private Long id;
    // 所属门店ID（总店长为空）
    private Long storeId;
    // 登录账号（手机号）
    private String username;
    // 登录密码（生产用bcrypt加密存储）
    private String password;
    // 姓名
    private String realName;
    // 员工工号
    private String staffNo;
    // 邮箱
    private String email;
    // 手机号
    private String phone;
    // 身份证号
    private String idCard;
    // 角色ID
    private Long roleId;
    // 头像URL
    private String avatar;
    // 账号状态 1在职 0离职
    private Integer status;
    // 最后登录时间
    private java.util.Date lastLoginTime;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
