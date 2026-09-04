package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.QueueEntity;
import com.iwe3.sec.mapper.QueueMapper;
import com.iwe3.sec.service.IQueueService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * queue 表的业务实现类
 */
@Service
public class QueueServiceImpl implements IQueueService {

    private final QueueMapper queueMapper;

    public QueueServiceImpl(QueueMapper queueMapper) {
        this.queueMapper = queueMapper;
    }

    @Override
    public PageResult<QueueEntity> list(QueueEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<QueueEntity> list = queueMapper.selectList(query);
        PageInfo<QueueEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public QueueEntity getById(Long id) {
        return queueMapper.selectById(id);
    }

    @Override
    public boolean add(QueueEntity entity) {
        return queueMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(QueueEntity entity) {
        return queueMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return queueMapper.deleteById(id) > 0;
    }
}
