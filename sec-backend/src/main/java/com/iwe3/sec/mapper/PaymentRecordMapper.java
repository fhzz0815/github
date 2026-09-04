package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.PaymentRecordEntity;
import java.util.List;

/**
 * payment_record 表的数据访问接口
 */
@Mapper
public interface PaymentRecordMapper {

    /** 分页查询列表 */
    List<PaymentRecordEntity> selectList(@Param("query") PaymentRecordEntity query);

    /** 根据ID查询 */
    PaymentRecordEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(PaymentRecordEntity entity);

    /** 修改 */
    int update(PaymentRecordEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
