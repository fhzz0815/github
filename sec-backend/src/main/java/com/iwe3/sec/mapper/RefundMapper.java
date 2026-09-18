package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.RefundEntity;
import java.util.List;

/**
 * refund 表的数据访问接口
 */
@Mapper
public interface RefundMapper {

    /** 分页查询列表 */
    List<RefundEntity> selectList(@Param("query") RefundEntity query);

    /** 根据ID查询 */
    RefundEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(RefundEntity entity);

    /** 修改 */
    int update(RefundEntity entity);

    /**
     * 更新退款状态（仅当状态为 1=待审核 时才能更新）
     *
     * @param id      退款单ID
     * @param status  目标状态：2=已通过，3=已驳回，4=已完成
     * @param refundTime 退款时间
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("refundTime") java.util.Date refundTime);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
