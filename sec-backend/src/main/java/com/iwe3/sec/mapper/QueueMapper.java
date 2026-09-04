package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.QueueEntity;
import java.util.List;

/**
 * queue 表的数据访问接口
 */
@Mapper
public interface QueueMapper {

    /** 分页查询列表 */
    List<QueueEntity> selectList(@Param("query") QueueEntity query);

    /** 根据ID查询 */
    QueueEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(QueueEntity entity);

    /** 修改 */
    int update(QueueEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
