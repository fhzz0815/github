package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DiningTableEntity;
import java.util.List;

/**
 * dining_table 表的数据访问接口
 */
@Mapper
public interface DiningTableMapper {

    /** 分页查询列表 */
    List<DiningTableEntity> selectList(@Param("query") DiningTableEntity query);

    /** 根据ID查询 */
    DiningTableEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DiningTableEntity entity);

    /** 修改 */
    int update(DiningTableEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
