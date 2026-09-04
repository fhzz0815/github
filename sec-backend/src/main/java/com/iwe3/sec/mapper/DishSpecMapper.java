package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishSpecEntity;
import java.util.List;

/**
 * dish_spec 表的数据访问接口
 */
@Mapper
public interface DishSpecMapper {

    /** 分页查询列表 */
    List<DishSpecEntity> selectList(@Param("query") DishSpecEntity query);

    /** 根据ID查询 */
    DishSpecEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DishSpecEntity entity);

    /** 修改 */
    int update(DishSpecEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
