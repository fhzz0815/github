package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.PaymentRecordEntity;
import com.iwe3.sec.mapper.PaymentRecordMapper;
import com.iwe3.sec.service.IPaymentRecordService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * payment_record 表的业务实现类
 */
@Service
public class PaymentRecordServiceImpl implements IPaymentRecordService {

    private final PaymentRecordMapper paymentRecordMapper;

    public PaymentRecordServiceImpl(PaymentRecordMapper paymentRecordMapper) {
        this.paymentRecordMapper = paymentRecordMapper;
    }

    @Override
    public PageResult<PaymentRecordEntity> list(PaymentRecordEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<PaymentRecordEntity> list = paymentRecordMapper.selectList(query);
        PageInfo<PaymentRecordEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public PaymentRecordEntity getById(Long id) {
        return paymentRecordMapper.selectById(id);
    }

    @Override
    public boolean add(PaymentRecordEntity entity) {
        return paymentRecordMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(PaymentRecordEntity entity) {
        return paymentRecordMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return paymentRecordMapper.deleteById(id) > 0;
    }
}
