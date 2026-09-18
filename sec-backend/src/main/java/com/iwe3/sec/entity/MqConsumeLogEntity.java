package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqConsumeLogEntity {
    private Long id;
    private String messageId;
    private String queueName;
    private Integer status;
    private String errorMessage;
    private Date consumedAt;
    private Date createTime;
}