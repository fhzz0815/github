package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.RefundEntity;
import com.iwe3.sec.mapper.RefundMapper;
import com.iwe3.sec.service.IRefundService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * refund 表的业务实现类
 */
@Service
public class RefundServiceImpl implements IRefundService {

    private final RefundMapper refundMapper;

    public RefundServiceImpl(RefundMapper refundMapper) {
        this.refundMapper = refundMapper;
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
}
