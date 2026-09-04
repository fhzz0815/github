package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.DiningTableEntity;
import com.iwe3.sec.mapper.DiningTableMapper;
import com.iwe3.sec.service.IDiningTableService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * dining_table 表的业务实现类
 */
@Service
public class DiningTableServiceImpl implements IDiningTableService {

    private final DiningTableMapper diningTableMapper;

    public DiningTableServiceImpl(DiningTableMapper diningTableMapper) {
        this.diningTableMapper = diningTableMapper;
    }

    @Override
    public PageResult<DiningTableEntity> list(DiningTableEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<DiningTableEntity> list = diningTableMapper.selectList(query);
        PageInfo<DiningTableEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public DiningTableEntity getById(Long id) {
        return diningTableMapper.selectById(id);
    }

    @Override
    public boolean add(DiningTableEntity entity) {
        return diningTableMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(DiningTableEntity entity) {
        return diningTableMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return diningTableMapper.deleteById(id) > 0;
    }
}
