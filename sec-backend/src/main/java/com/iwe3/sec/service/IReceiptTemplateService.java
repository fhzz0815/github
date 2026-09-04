package com.iwe3.sec.service;

import com.iwe3.sec.entity.ReceiptTemplateEntity;
import com.iwe3.sec.common.PageResult;

/**
 * receipt_template 表的业务接口
 */
public interface IReceiptTemplateService {

    /** 分页查询列表 */
    PageResult<ReceiptTemplateEntity> list(ReceiptTemplateEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    ReceiptTemplateEntity getById(Long id);

    /** 新增 */
    boolean add(ReceiptTemplateEntity entity);

    /** 修改 */
    boolean update(ReceiptTemplateEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
