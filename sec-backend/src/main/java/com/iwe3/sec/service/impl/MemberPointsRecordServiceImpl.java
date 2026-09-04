package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.MemberPointsRecordEntity;
import com.iwe3.sec.mapper.MemberPointsRecordMapper;
import com.iwe3.sec.service.IMemberPointsRecordService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * member_points_record 表的业务实现类
 */
@Service
public class MemberPointsRecordServiceImpl implements IMemberPointsRecordService {

    private final MemberPointsRecordMapper memberPointsRecordMapper;

    public MemberPointsRecordServiceImpl(MemberPointsRecordMapper memberPointsRecordMapper) {
        this.memberPointsRecordMapper = memberPointsRecordMapper;
    }

    @Override
    public PageResult<MemberPointsRecordEntity> list(MemberPointsRecordEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<MemberPointsRecordEntity> list = memberPointsRecordMapper.selectList(query);
        PageInfo<MemberPointsRecordEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public MemberPointsRecordEntity getById(Long id) {
        return memberPointsRecordMapper.selectById(id);
    }

    @Override
    public boolean add(MemberPointsRecordEntity entity) {
        return memberPointsRecordMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(MemberPointsRecordEntity entity) {
        return memberPointsRecordMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return memberPointsRecordMapper.deleteById(id) > 0;
    }
}
