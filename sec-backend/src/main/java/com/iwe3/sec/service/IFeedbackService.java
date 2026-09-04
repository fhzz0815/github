package com.iwe3.sec.service;

import com.iwe3.sec.entity.FeedbackEntity;
import com.iwe3.sec.common.PageResult;

/**
 * feedback 表的业务接口
 */
public interface IFeedbackService {

    /** 分页查询列表 */
    PageResult<FeedbackEntity> list(FeedbackEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    FeedbackEntity getById(Long id);

    /** 新增 */
    boolean add(FeedbackEntity entity);

    /** 修改 */
    boolean update(FeedbackEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
