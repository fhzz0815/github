package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberAddressEntity;
import com.iwe3.sec.mapper.MemberAddressMapper;
import com.iwe3.sec.service.IMemberAddressService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_address 表的业务实现类
 */
@Service
public class MemberAddressServiceImpl implements IMemberAddressService {

    private final MemberAddressMapper memberAddressMapper;

    public MemberAddressServiceImpl(MemberAddressMapper memberAddressMapper) {
        this.memberAddressMapper = memberAddressMapper;
    }

    @Override
    public PageResult<MemberAddressEntity> list(MemberAddressEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberAddressEntity> list = memberAddressMapper.selectList(query);
        PageInfo<MemberAddressEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberAddressEntity getById(Long id) {
        return memberAddressMapper.selectById(id);
    }

    @Override
    public boolean add(MemberAddressEntity entity) {
        return memberAddressMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberAddressEntity entity) {
        return memberAddressMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberAddressMapper.deleteById(id) > 0;
    }
}
