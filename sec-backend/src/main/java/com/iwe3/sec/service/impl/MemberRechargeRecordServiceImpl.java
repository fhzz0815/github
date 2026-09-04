package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberRechargeRecordEntity;
import com.iwe3.sec.mapper.MemberRechargeRecordMapper;
import com.iwe3.sec.service.IMemberRechargeRecordService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_recharge_record 表的业务实现类
 */
@Service
public class MemberRechargeRecordServiceImpl implements IMemberRechargeRecordService {

    private final MemberRechargeRecordMapper memberRechargeRecordMapper;

    public MemberRechargeRecordServiceImpl(MemberRechargeRecordMapper memberRechargeRecordMapper) {
        this.memberRechargeRecordMapper = memberRechargeRecordMapper;
    }

    @Override
    public PageResult<MemberRechargeRecordEntity> list(MemberRechargeRecordEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberRechargeRecordEntity> list = memberRechargeRecordMapper.selectList(query);
        PageInfo<MemberRechargeRecordEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberRechargeRecordEntity getById(Long id) {
        return memberRechargeRecordMapper.selectById(id);
    }

    @Override
    public boolean add(MemberRechargeRecordEntity entity) {
        return memberRechargeRecordMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberRechargeRecordEntity entity) {
        return memberRechargeRecordMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberRechargeRecordMapper.deleteById(id) > 0;
    }
}
