package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.PrinterEntity;
import java.util.List;

/**
 * printer 表的数据访问接口
 */
@Mapper
public interface PrinterMapper {

    /** 分页查询列表 */
    List<PrinterEntity> selectList(@Param("query") PrinterEntity query);

    /** 根据ID查询 */
    PrinterEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(PrinterEntity entity);

    /** 修改 */
    int update(PrinterEntity entity);

    /** 根据门店和用途查找已启用的打印机 */
    List<PrinterEntity> selectByStoreAndType(@Param("storeId") Long storeId,
                                              @Param("printerType") String printerType);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
