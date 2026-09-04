package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StoreEntity;
import java.util.List;

/**
 * store 表的数据访问接口
 */
@Mapper
public interface StoreMapper {

    /** 分页查询列表 */
    List<StoreEntity> selectList(@Param("query") StoreEntity query);

    /** 根据ID查询 */
    StoreEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StoreEntity entity);

    /** 修改 */
    int update(StoreEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
