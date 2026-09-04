package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.PrinterEntity;
import com.iwe3.sec.mapper.PrinterMapper;
import com.iwe3.sec.service.IPrinterService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * printer 表的业务实现类
 */
@Service
public class PrinterServiceImpl implements IPrinterService {

    private final PrinterMapper printerMapper;

    public PrinterServiceImpl(PrinterMapper printerMapper) {
        this.printerMapper = printerMapper;
    }

    @Override
    public PageResult<PrinterEntity> list(PrinterEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<PrinterEntity> list = printerMapper.selectList(query);
        PageInfo<PrinterEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public PrinterEntity getById(Long id) {
        return printerMapper.selectById(id);
    }

    @Override
    public boolean add(PrinterEntity entity) {
        return printerMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(PrinterEntity entity) {
        return printerMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return printerMapper.deleteById(id) > 0;
    }
}
