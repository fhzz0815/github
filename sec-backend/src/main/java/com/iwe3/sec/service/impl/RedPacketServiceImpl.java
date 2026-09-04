package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.RedPacketEntity;
import com.iwe3.sec.mapper.RedPacketMapper;
import com.iwe3.sec.service.IRedPacketService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * red_packet 表的业务实现类
 */
@Service
public class RedPacketServiceImpl implements IRedPacketService {

    private final RedPacketMapper redPacketMapper;

    public RedPacketServiceImpl(RedPacketMapper redPacketMapper) {
        this.redPacketMapper = redPacketMapper;
    }

    @Override
    public PageResult<RedPacketEntity> list(RedPacketEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<RedPacketEntity> list = redPacketMapper.selectList(query);
        PageInfo<RedPacketEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public RedPacketEntity getById(Long id) {
        return redPacketMapper.selectById(id);
    }

    @Override
    public boolean add(RedPacketEntity entity) {
        return redPacketMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(RedPacketEntity entity) {
        return redPacketMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return redPacketMapper.deleteById(id) > 0;
    }
}
