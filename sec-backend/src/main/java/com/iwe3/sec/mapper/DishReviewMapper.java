package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishReviewEntity;
import java.util.List;

/**
 * dish_review 表的数据访问接口
 */
@Mapper
public interface DishReviewMapper {

    /** 分页查询列表 */
    List<DishReviewEntity> selectList(@Param("query") DishReviewEntity query);

    /** 根据ID查询 */
    DishReviewEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(DishReviewEntity entity);

    /** 修改 */
    int update(DishReviewEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
