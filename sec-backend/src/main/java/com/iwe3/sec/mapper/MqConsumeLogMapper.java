package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.MqConsumeLogEntity;

@Mapper
public interface MqConsumeLogMapper {

    /** 插入消费日志（唯一键冲突时跳过） */
    int insert(MqConsumeLogEntity entity);

    /** 根据 messageId 查询 */
    MqConsumeLogEntity selectByMessageId(@Param("messageId") String messageId);
}