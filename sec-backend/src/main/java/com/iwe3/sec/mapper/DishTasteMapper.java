package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishTasteEntity;
import java.util.List;

/**
 * dish_taste 表的数据访问接口
 */
@Mapper
public interface DishTasteMapper {

    /** 分页查询列表 */
    List<DishTasteEntity> selectList(@Param("query") DishTasteEntity query);

    /** 根据ID查询 */
    DishTasteEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DishTasteEntity entity);

    /** 修改 */
    int update(DishTasteEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
