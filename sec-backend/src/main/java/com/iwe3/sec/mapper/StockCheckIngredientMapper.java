package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import java.util.List;

/**
 * stock_check_ingredient 表的数据访问接口
 */
@Mapper
public interface StockCheckIngredientMapper {

    /** 分页查询列表 */
    List<StockCheckIngredientEntity> selectList(@Param("query") StockCheckIngredientEntity query);

    /** 根据ID查询 */
    StockCheckIngredientEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StockCheckIngredientEntity entity);

    /** 修改 */
    int update(StockCheckIngredientEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
