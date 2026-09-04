package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.CartEntity;
import java.util.List;

/**
 * cart 表的数据访问接口
 */
@Mapper
public interface CartMapper {

    /** 分页查询列表 */
    List<CartEntity> selectList(@Param("query") CartEntity query);

    /** 根据ID查询 */
    CartEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(CartEntity entity);

    /** 修改 */
    int update(CartEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
