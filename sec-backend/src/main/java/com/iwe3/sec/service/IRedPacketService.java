package com.iwe3.sec.service;

import com.iwe3.sec.entity.RedPacketEntity;
import com.iwe3.sec.common.PageResult;

/**
 * red_packet 表的业务接口
 */
public interface IRedPacketService {

    /** 分页查询列表 */
    PageResult<RedPacketEntity> list(RedPacketEntity query, Integer page, Integer size);

    /** 根据ID查询详情 */
    RedPacketEntity getById(Long id);

    /** 新增 */
    boolean add(RedPacketEntity entity);

    /** 修改 */
    boolean update(RedPacketEntity entity);

    /** 删除 */
    boolean remove(Long id);
}
