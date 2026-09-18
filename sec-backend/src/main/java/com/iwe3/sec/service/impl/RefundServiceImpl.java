package com.iwe3.sec.service.impl;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.entity.OrdersEntity;
import com.iwe3.sec.entity.OrderDetailEntity;
import com.iwe3.sec.mapper.RefundMapper;
import com.iwe3.sec.mapper.OrdersMapper;
import com.iwe3.sec.mapper.OrderDetailMapper;
import com.iwe3.sec.service.IRefundService;
import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.PermissionChecker;
import com.iwe3.sec.common.BusinessException;
import com.iwe3.sec.common.ErrorCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * refund 表的业务实现类
 * 负责：
 *   1. 提交退款申请（整单退/退菜）
 *   2. 审核退款（通过/驳回），通过后同步更新订单和明细数据
 */
@Service
public class RefundServiceImpl implements IRefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundMapper refundMapper;
    private final OrdersMapper ordersMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final PermissionChecker permissionChecker;

    public RefundServiceImpl(RefundMapper refundMapper,
                             OrdersMapper ordersMapper,
                             OrderDetailMapper orderDetailMapper,
                             PermissionChecker permissionChecker) {
        this.refundMapper = refundMapper;
        this.ordersMapper = ordersMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public PageResult<RefundEntity> list(RefundEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<RefundEntity> list = refundMapper.selectList(query);
        PageInfo<RefundEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public RefundEntity getById(Long id) {
        return refundMapper.selectById(id);
    }

    @Override
    public boolean add(RefundEntity entity) {
        return refundMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(RefundEntity entity) {
        return refundMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return refundMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitRefund(RefundEntity entity) {
        // 1. 校验订单是否存在
        OrdersEntity order = ordersMapper.selectById(entity.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        permissionChecker.assertInOwnStore(order.getStoreId());

        // 2. 如果是退菜（refund_type=2），校验明细是否存在且未完全退款
        if (entity.getRefundType() == 2) {
            if (entity.getOrderDetailId() == null) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "退菜时必须指定订单明细ID");
            }
            OrderDetailEntity detail = orderDetailMapper.selectById(entity.getOrderDetailId());
            if (detail == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "订单明细不存在");
            }
            // 已退金额 + 本次退款 <= 小计金额
            BigDecimal alreadyRefunded = detail.getRefundAmount() != null ? detail.getRefundAmount() : BigDecimal.ZERO;
            if (alreadyRefunded.add(entity.getAmount()).compareTo(detail.getSubtotal()) > 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "退款金额超过可退金额");
            }
        }

        // 3. 校验退款金额不能超过订单实付金额
        if (entity.getAmount().compareTo(order.getActualAmount()) > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "退款金额不能超过订单实付金额");
        }

        // 4. 设置默认值
        if (entity.getStoreId() == null) {
            entity.setStoreId(order.getStoreId());
        }
        if (entity.getMemberId() == null) {
            entity.setMemberId(order.getMemberId());
        }
        if (entity.getRefundNo() == null) {
            entity.setRefundNo("RF" + DateUtil.format(new Date(), "yyyyMMddHHmmss")
                    + (int) (Math.random() * 1000));
        }
        entity.setStatus(1); // 1=待审核
        entity.setOperatorId(permissionChecker.currentUserId());

        // 5. 保存
        refundMapper.insert(entity);
        log.info("退款申请已提交：id={}, refundNo={}, amount={}", entity.getId(), entity.getRefundNo(), entity.getAmount());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRefund(Long id, boolean approved, String auditRemark) {
        // 1. 查询退款单
        RefundEntity refund = refundMapper.selectById(id);
        if (refund == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "退款单不存在");
        }
        if (refund.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "当前状态不是「待审核」，不能操作");
        }

        // 2. 校验权限：店长及以上可以审核退款
        permissionChecker.assertInOwnStore(refund.getStoreId());

        // 3. 更新退款单状态
        int targetStatus = approved ? 2 : 3; // 2=已通过, 3=已驳回
        Date now = new Date();
        int affected = refundMapper.updateStatus(id, targetStatus, now);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "处理失败，请稍后重试");
        }

        // 4. 如果驳回，直接返回
        if (!approved) {
            log.info("退款申请已驳回：id={}, auditRemark={}", id, auditRemark);
            return;
        }

        // 5. 审核通过，处理具体退款逻辑
        OrdersEntity order = ordersMapper.selectById(refund.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "关联订单不存在");
        }

        if (refund.getRefundType() == 2) {
            // ====== 退菜（部分退款） ======
            // 5a. 更新明细的已退金额
            int detailAffected = orderDetailMapper.increaseRefundAmount(
                    refund.getOrderDetailId(), refund.getAmount());
            if (detailAffected == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新订单明细退款金额失败");
            }

            // 5b. 更新订单实付金额和支付状态
            BigDecimal newActualAmount = order.getActualAmount().subtract(refund.getAmount());
            // 判断是否全部退完（所有明细的 subtotal == refund_amount 之和）
            List<OrderDetailEntity> details = orderDetailMapper.selectByOrderId(order.getId());
            boolean allRefunded = details.stream().allMatch(d ->
                    d.getRefundAmount() != null && d.getRefundAmount().compareTo(d.getSubtotal()) >= 0);

            int newPayStatus = allRefunded ? 3 : 4; // 3=已退款, 4=部分退款

            // 使用 orders 的 update 方法更新金额和支付状态
            OrdersEntity updatedOrder = OrdersEntity.builder()
                    .id(order.getId())
                    .actualAmount(newActualAmount)
                    .payStatus(newPayStatus)
                    .build();
            ordersMapper.update(updatedOrder);

            log.info("退菜审核通过：refundId={}, detailId={}, amount={}, newActualAmount={}",
                    id, refund.getOrderDetailId(), refund.getAmount(), newActualAmount);
        } else {
            // ====== 整单退（全额退款） ======
            // 使用状态机将订单状态改为已退款
            int transitionAffected = ordersMapper.transitionStatus(
                    order.getId(),
                    order.getOrderStatus(),   // fromStatus
                    9,                        // toStatus（已退款）
                    3,                        // newPayStatus（已退款）
                    null,                     // payTime
                    order.getOrderStatus() == 7 ? now : null, // finishTime
                    null,                     // cancelTime
                    "整单退款",                // cancelReason
                    order.getVersion()
            );
            if (transitionAffected == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新订单状态失败，请稍后重试");
            }

            log.info("整单退款审核通过：refundId={}, orderId={}, amount={}",
                    id, refund.getOrderId(), refund.getAmount());
        }

        // 6. 更新退款单为已完成
        refundMapper.updateStatus(id, 4, now); // 4=已完成
        log.info("退款处理完成：id={}, approved={}", id, approved);
    }
}
