package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.RedPacketEntity;
import java.util.List;

/**
 * red_packet 表的数据访问接口
 */
@Mapper
public interface RedPacketMapper {

    /** 分页查询列表 */
    List<RedPacketEntity> selectList(@Param("query") RedPacketEntity query);

    /** 根据ID查询 */
    RedPacketEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(RedPacketEntity entity);

    /** 修改 */
    int update(RedPacketEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
