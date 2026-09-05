package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish_review 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishReviewEntity {

    // 评价ID
    private Long id;
    // 订单ID
    private Long orderId;
    // 订单明细ID
    private Long orderDetailId;
    // 菜品ID
    private Long dishId;
    // 客户ID
    private Long memberId;
    // 门店ID
    private Long storeId;
    // 评分 1-5
    private Integer rating;
    // 评价内容
    private String content;
    // 评价图片(逗号分隔)
    private String images;
    // 是否匿名 1是 0否
    private Integer isAnonymous;
    // 商家回复
    private String reply;
    // 回复时间
    private java.util.Date replyTime;
    // 创建时间
    private java.util.Date createTime;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
