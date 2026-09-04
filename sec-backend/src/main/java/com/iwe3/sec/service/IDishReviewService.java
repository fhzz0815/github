package com.iwe3.sec.service;

import com.iwe3.sec.entity.DishReviewEntity;
import com.iwe3.sec.common.PageResult;

/**
 * dish_review 表的业务接口
 */
public interface IDishReviewService {

    /** 分页查询列表 */
    PageResult<DishReviewEntity> list(DishReviewEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    DishReviewEntity getById(Long id);

    /** 新增 */
    boolean add(DishReviewEntity entity);

    /** 修改 */
    boolean update(DishReviewEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
