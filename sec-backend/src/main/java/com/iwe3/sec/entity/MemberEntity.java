package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * member 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity {

    // 客户ID
    private Long id;
    // 微信openid
    private String openid;
    // 微信unionid
    private String unionid;
    // 昵称
    private String nickname;
    // 头像URL
    private String avatar;
    // 性别 0未知 1男 2女
    private Integer gender;
    // 绑定手机号
    private String phone;
    // 真实姓名
    private String realName;
    // 身份证号（钱包实名）
    private String idCard;
    // 会员卡号
    private String memberNo;
    // 会员类别ID
    private Long memberCategoryId;
    // 钱包余额(元)
    private java.math.BigDecimal balance;
    // 累计积分
    private Integer totalPoints;
    // 可用积分
    private Integer availablePoints;
    // 支付密码（bcrypt加密）
    private String payPassword;
    // 注册门店ID
    private Long registerStoreId;
    // 状态 1正常 0冻结
    private Integer status;
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
