package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表 Mapper
 * 查询数据库视图，返回报表数据
 */
@Mapper
public interface ReportMapper {

    /** 菜品流水 */
    List<Map<String, Object>> selectDishFlow(@Param("storeId") Long storeId,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);

    /** 收款报表 */
    List<Map<String, Object>> selectReceiptReport(@Param("storeId") Long storeId,
                                                   @Param("date") String date);

    /** 菜品销售排行 */
    List<Map<String, Object>> selectDishSalesRanking(@Param("storeId") Long storeId,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("topN") Integer topN);

    /** 门店销售排行 */
    List<Map<String, Object>> selectStoreSalesRanking(@Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate);

    /** 今日收入概况 */
    List<Map<String, Object>> selectTodayIncome(@Param("storeId") Long storeId);

    /** 退款记录 */
    List<Map<String, Object>> selectRefundRecord(@Param("storeId") Long storeId,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate);

    /** 菜品流水（延迟关联优化版 - 大数据量时使用） */
    List<Map<String, Object>> selectDishFlowOptimized(@Param("storeId") Long storeId,
                                                       @Param("startDate") Date startDate,
                                                       @Param("endDate") Date endDate,
                                                       @Param("offset") Integer offset,
                                                       @Param("limit") Integer limit);
}