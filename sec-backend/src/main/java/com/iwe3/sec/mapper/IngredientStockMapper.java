package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.IngredientStockEntity;
import java.util.List;

/**
 * ingredient_stock 表的数据访问接口
 */
@Mapper
public interface IngredientStockMapper {

    /** 分页查询列表 */
    List<IngredientStockEntity> selectList(@Param("query") IngredientStockEntity query);

    /** 根据ID查询 */
    IngredientStockEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(IngredientStockEntity entity);

    /** 修改 */
    int update(IngredientStockEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
