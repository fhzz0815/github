package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.FeedbackEntity;
import java.util.List;

/**
 * feedback 表的数据访问接口
 */
@Mapper
public interface FeedbackMapper {

    /** 分页查询列表 */
    List<FeedbackEntity> selectList(@Param("query") FeedbackEntity query);

    /** 根据ID查询 */
    FeedbackEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(FeedbackEntity entity);

    /** 修改 */
    int update(FeedbackEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
