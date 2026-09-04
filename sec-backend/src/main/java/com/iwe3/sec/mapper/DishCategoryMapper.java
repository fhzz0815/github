package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishCategoryEntity;
import java.util.List;

/**
 * dish_category 表的数据访问接口
 */
@Mapper
public interface DishCategoryMapper {

    /** 分页查询列表 */
    List<DishCategoryEntity> selectList(@Param("query") DishCategoryEntity query);

    /** 根据ID查询 */
    DishCategoryEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DishCategoryEntity entity);

    /** 修改 */
    int update(DishCategoryEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
