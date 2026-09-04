package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.mapper.FeedbackMapper;
import com.iwe3.sec.service.IFeedbackService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * feedback 表的业务实现类
 */
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    private final FeedbackMapper feedbackMapper;

    public FeedbackServiceImpl(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    public PageResult<FeedbackEntity> list(FeedbackEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<FeedbackEntity> list = feedbackMapper.selectList(query);
        PageInfo<FeedbackEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public FeedbackEntity getById(Long id) {
        return feedbackMapper.selectById(id);
    }

    @Override
    public boolean add(FeedbackEntity entity) {
        return feedbackMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(FeedbackEntity entity) {
        return feedbackMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return feedbackMapper.deleteById(id) > 0;
    }
}
