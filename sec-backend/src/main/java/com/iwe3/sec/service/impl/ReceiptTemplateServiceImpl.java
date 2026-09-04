package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.ReceiptTemplateEntity;
import com.iwe3.sec.mapper.ReceiptTemplateMapper;
import com.iwe3.sec.service.IReceiptTemplateService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * receipt_template 表的业务实现类
 */
@Service
public class ReceiptTemplateServiceImpl implements IReceiptTemplateService {

    private final ReceiptTemplateMapper receiptTemplateMapper;

    public ReceiptTemplateServiceImpl(ReceiptTemplateMapper receiptTemplateMapper) {
        this.receiptTemplateMapper = receiptTemplateMapper;
    }

    @Override
    public PageResult<ReceiptTemplateEntity> list(ReceiptTemplateEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<ReceiptTemplateEntity> list = receiptTemplateMapper.selectList(query);
        PageInfo<ReceiptTemplateEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public ReceiptTemplateEntity getById(Long id) {
        return receiptTemplateMapper.selectById(id);
    }

    @Override
    public boolean add(ReceiptTemplateEntity entity) {
        return receiptTemplateMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(ReceiptTemplateEntity entity) {
        return receiptTemplateMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return receiptTemplateMapper.deleteById(id) > 0;
    }
}
