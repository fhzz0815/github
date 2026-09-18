package com.iwe3.sec.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表服务接口
 * 提供 6 类报表查询能力，所有报表查询均走从库
 */
public interface IReportService {

    /** 菜品流水报表 */
    List<Map<String, Object>> getDishFlow(Long storeId, Date startDate, Date endDate);

    /** 收款报表 */
    List<Map<String, Object>> getReceiptReport(Long storeId, String date);

    /** 菜品销售排行 */
    List<Map<String, Object>> getDishSalesRanking(Long storeId, Date startDate, Date endDate, Integer topN);

    /** 门店销售排行 */
    List<Map<String, Object>> getStoreSalesRanking(Date startDate, Date endDate);

    /** 今日收入概况 */
    List<Map<String, Object>> getTodayIncome(Long storeId);

    /** 退款记录 */
    List<Map<String, Object>> getRefundRecord(Long storeId, Date startDate, Date endDate);
}