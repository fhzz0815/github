package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.ReceiptTemplateEntity;
import java.util.List;

/**
 * receipt_template 表的数据访问接口
 */
@Mapper
public interface ReceiptTemplateMapper {

    /** 分页查询列表 */
    List<ReceiptTemplateEntity> selectList(@Param("query") ReceiptTemplateEntity query);

    /** 根据ID查询 */
    ReceiptTemplateEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(ReceiptTemplateEntity entity);

    /** 修改 */
    int update(ReceiptTemplateEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
