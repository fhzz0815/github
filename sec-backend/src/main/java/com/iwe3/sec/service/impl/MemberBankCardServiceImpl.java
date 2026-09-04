package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberBankCardEntity;
import com.iwe3.sec.mapper.MemberBankCardMapper;
import com.iwe3.sec.service.IMemberBankCardService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_bank_card 表的业务实现类
 */
@Service
public class MemberBankCardServiceImpl implements IMemberBankCardService {

    private final MemberBankCardMapper memberBankCardMapper;

    public MemberBankCardServiceImpl(MemberBankCardMapper memberBankCardMapper) {
        this.memberBankCardMapper = memberBankCardMapper;
    }

    @Override
    public PageResult<MemberBankCardEntity> list(MemberBankCardEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberBankCardEntity> list = memberBankCardMapper.selectList(query);
        PageInfo<MemberBankCardEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberBankCardEntity getById(Long id) {
        return memberBankCardMapper.selectById(id);
    }

    @Override
    public boolean add(MemberBankCardEntity entity) {
        return memberBankCardMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberBankCardEntity entity) {
        return memberBankCardMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberBankCardMapper.deleteById(id) > 0;
    }
}
