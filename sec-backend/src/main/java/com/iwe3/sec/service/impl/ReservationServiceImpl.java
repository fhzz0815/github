package com.iwe3.sec.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import com.iwe3.sec.entity.ReservationEntity;
import com.iwe3.sec.mapper.ReservationMapper;
import com.iwe3.sec.service.IReservationService;
import com.iwe3.sec.common.PageResult;

import java.util.List;

/**
 * reservation 表的业务实现类
 */
@Service
public class ReservationServiceImpl implements IReservationService {

    private final ReservationMapper reservationMapper;

    public ReservationServiceImpl(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @Override
    public PageResult<ReservationEntity> list(ReservationEntity query, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<ReservationEntity> list = reservationMapper.selectList(query);
        PageInfo<ReservationEntity> pageInfo = new PageInfo<>(list);
        return PageResult.of(pageInfo.getTotal(), pageInfo.getPages(), list);
    }

    @Override
    public ReservationEntity getById(Long id) {
        return reservationMapper.selectById(id);
    }

    @Override
    public boolean add(ReservationEntity entity) {
        return reservationMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(ReservationEntity entity) {
        return reservationMapper.update(entity) > 0;
    }

    @Override
    public boolean remove(Long id) {
        return reservationMapper.deleteById(id) > 0;
    }
}
