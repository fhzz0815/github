package com.iwe3.sec.service;

import com.iwe3.sec.entity.QueueEntity;
import com.iwe3.sec.common.PageResult;

/**
 * queue 表的业务接口
 */
public interface IQueueService {

    /** 分页查询列表 */
    PageResult<QueueEntity> list(QueueEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    QueueEntity getById(Long id);

    /** 新增 */
    boolean add(QueueEntity entity);

    /** 修改 */
    boolean update(QueueEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
