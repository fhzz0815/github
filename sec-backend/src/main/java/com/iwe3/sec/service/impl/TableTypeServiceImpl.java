package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.TableTypeEntity;
import com.iwe3.sec.mapper.TableTypeMapper;
import com.iwe3.sec.service.ITableTypeService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * table_type 表的业务实现类
 */
@Service
public class TableTypeServiceImpl implements ITableTypeService {

    private final TableTypeMapper tableTypeMapper;

    public TableTypeServiceImpl(TableTypeMapper tableTypeMapper) {
        this.tableTypeMapper = tableTypeMapper;
    }

    @Override
    public PageResult<TableTypeEntity> list(TableTypeEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<TableTypeEntity> list = tableTypeMapper.selectList(query);
        PageInfo<TableTypeEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    @Cacheable(value = "entity", key = "#id")
    public TableTypeEntity getById(Long id) {
        return tableTypeMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean add(TableTypeEntity entity) {
        return tableTypeMapper.insert(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#entity.id")
    public boolean update(TableTypeEntity entity) {
        return tableTypeMapper.update(entity) > 0;
    }

    @Override
    @CacheEvict(value = "entity", key = "#id")
    public boolean remove(Long id) {
        return tableTypeMapper.deleteById(id) > 0;
    }
}
