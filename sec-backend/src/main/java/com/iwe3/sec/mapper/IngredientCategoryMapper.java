package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.IngredientCategoryEntity;
import java.util.List;

/**
 * ingredient_category 表的数据访问接口
 */
@Mapper
public interface IngredientCategoryMapper {

    /** 分页查询列表 */
    List<IngredientCategoryEntity> selectList(@Param("query") IngredientCategoryEntity query);

    /** 根据ID查询 */
    IngredientCategoryEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(IngredientCategoryEntity entity);

    /** 修改 */
    int update(IngredientCategoryEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
