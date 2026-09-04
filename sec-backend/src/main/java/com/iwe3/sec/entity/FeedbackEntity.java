package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * feedback 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackEntity {

    // 反馈ID
    private Long id;
    // 客户ID
    private Long memberId;
    // 门店ID
    private Long storeId;
    // 反馈内容
    private String content;
    // 反馈图片(逗号分隔)
    private String images;
    // 联系电话
    private String contactPhone;
    // 状态 1待处理 2已处理
    private Integer status;
    // 处理回复
    private String reply;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
}
