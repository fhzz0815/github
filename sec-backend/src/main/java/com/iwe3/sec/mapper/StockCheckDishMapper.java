package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StockCheckDishEntity;
import java.util.List;

/**
 * stock_check_dish 表的数据访问接口
 */
@Mapper
public interface StockCheckDishMapper {

    /** 分页查询列表 */
    List<StockCheckDishEntity> selectList(@Param("query") StockCheckDishEntity query);

    /** 根据ID查询 */
    StockCheckDishEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StockCheckDishEntity entity);

    /** 修改 */
    int update(StockCheckDishEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
