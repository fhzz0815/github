package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberBalanceRecordEntity;
import com.iwe3.sec.mapper.MemberBalanceRecordMapper;
import com.iwe3.sec.service.IMemberBalanceRecordService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_balance_record 表的业务实现类
 */
@Service
public class MemberBalanceRecordServiceImpl implements IMemberBalanceRecordService {

    private final MemberBalanceRecordMapper memberBalanceRecordMapper;

    public MemberBalanceRecordServiceImpl(MemberBalanceRecordMapper memberBalanceRecordMapper) {
        this.memberBalanceRecordMapper = memberBalanceRecordMapper;
    }

    @Override
    public PageResult<MemberBalanceRecordEntity> list(MemberBalanceRecordEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberBalanceRecordEntity> list = memberBalanceRecordMapper.selectList(query);
        PageInfo<MemberBalanceRecordEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberBalanceRecordEntity getById(Long id) {
        return memberBalanceRecordMapper.selectById(id);
    }

    @Override
    public boolean add(MemberBalanceRecordEntity entity) {
        return memberBalanceRecordMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberBalanceRecordEntity entity) {
        return memberBalanceRecordMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberBalanceRecordMapper.deleteById(id) > 0;
    }
}
