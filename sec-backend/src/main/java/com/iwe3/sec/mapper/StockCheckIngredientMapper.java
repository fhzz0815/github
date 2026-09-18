package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.StockCheckIngredientEntity;
import java.util.List;

/**
 * stock_check_ingredient 表的数据访问接口
 */
@Mapper
public interface StockCheckIngredientMapper {

    /** 分页查询列表 */
    List<StockCheckIngredientEntity> selectList(@Param("query") StockCheckIngredientEntity query);

    /** 根据ID查询 */
    StockCheckIngredientEntity selectById(@Param("id") Long id);

    /** 新增 */
    int insert(StockCheckIngredientEntity entity);

    /** 修改 */
    int update(StockCheckIngredientEntity entity);

    /**
     * 审核盘点单（仅当状态为 1=待审核 时才能更新）
     *
     * @param id          盘点单ID
     * @param status      审核结果：2=已通过，3=已驳回
     * @param auditorId   审核人ID（总店长）
     * @param auditTime   审核时间
     * @param auditRemark 审核意见
     * @return 影响行数（0=状态已变更或记录不存在）
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status,
                     @Param("auditorId") Long auditorId, @Param("auditTime") java.util.Date auditTime,
                     @Param("auditRemark") String auditRemark);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
