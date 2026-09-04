package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StorePaymentSettingEntity;
import java.util.List;

/**
 * store_payment_setting 表的数据访问接口
 */
@Mapper
public interface StorePaymentSettingMapper {

    /** 分页查询列表 */
    List<StorePaymentSettingEntity> selectList(@Param("query") StorePaymentSettingEntity query);

    /** 根据ID查询 */
    StorePaymentSettingEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StorePaymentSettingEntity entity);

    /** 修改 */
    int update(StorePaymentSettingEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
