package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.TableTypeEntity;
import java.util.List;

/**
 * table_type 表的数据访问接口
 */
@Mapper
public interface TableTypeMapper {

    /** 分页查询列表 */
    List<TableTypeEntity> selectList(@Param("query") TableTypeEntity query);

    /** 根据ID查询 */
    TableTypeEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(TableTypeEntity entity);

    /** 修改 */
    int update(TableTypeEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
