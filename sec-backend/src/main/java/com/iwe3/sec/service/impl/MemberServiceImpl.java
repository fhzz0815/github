package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberEntity;
import com.iwe3.sec.mapper.MemberMapper;
import com.iwe3.sec.service.IMemberService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member 表的业务实现类
 */
@Service
public class MemberServiceImpl implements IMemberService {

    private final MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public PageResult<MemberEntity> list(MemberEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberEntity> list = memberMapper.selectList(query);
        PageInfo<MemberEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberEntity getById(Long id) {
        return memberMapper.selectById(id);
    }

    @Override
    public boolean add(MemberEntity entity) {
        return memberMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberEntity entity) {
        return memberMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberMapper.deleteById(id) > 0;
    }
}
