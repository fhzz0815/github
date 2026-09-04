package com.iwe3.sec.service;

import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.common.PageResult;

/**
 * printer 表的业务接口
 */
public interface IPrinterService {

    /** 分页查询列表 */
    PageResult<PrinterEntity> list(PrinterEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    PrinterEntity getById(Long id);

    /** 新增 */
    boolean add(PrinterEntity entity);

    /** 修改 */
    boolean update(PrinterEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
