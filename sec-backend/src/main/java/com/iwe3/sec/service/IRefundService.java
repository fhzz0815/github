package com.iwe3.sec.service;

import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.common.PageResult;

/**
 * refund 表的业务接口
 */
public interface IRefundService {

    /** 分页查询列表 */
    PageResult<RefundEntity> list(RefundEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    RefundEntity getById(Long id);

    /** 新增 */
    boolean add(RefundEntity entity);

    /** 修改 */
    boolean update(RefundEntity entity);

    /** 删除 */
    boolean remove(Long id);

    /**
     * 提交退款申请（状态设为 1=待审核）
     *
     * @param entity 退款单信息
     * @return 退款单ID
     */
    Long submitRefund(RefundEntity entity);

    /**
     * 处理退款申请（审核通过或驳回）
     *
     * @param id          退款单ID
     * @param approved    是否通过：true=通过，false=驳回
     * @param auditRemark 审核意见
     */
    void processRefund(Long id, boolean approved, String auditRemark);
}
