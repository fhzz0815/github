package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishIngredientRelEntity;
import java.util.List;

/**
 * dish_ingredient_rel 表的数据访问接口
 */
@Mapper
public interface DishIngredientRelMapper {

    /** 分页查询列表 */
    List<DishIngredientRelEntity> selectList(@Param("query") DishIngredientRelEntity query);

    /** 根据ID查询 */
    DishIngredientRelEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DishIngredientRelEntity entity);

    /** 修改 */
    int update(DishIngredientRelEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
