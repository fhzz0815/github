package com.iwe3.sec.service.impl;

import com.iwe3.sec.common.datasource.ReadOnly;
import com.iwe3.sec.mapper.ReportMapper;
import com.iwe3.sec.service.IReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 * 所有报表查询标注 @ReadOnly 路由到从库
 */
@Service
public class ReportServiceImpl implements IReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getDishFlow(Long storeId, Date startDate, Date endDate) {
        log.info("查询菜品流水报表: storeId={}, startDate={}, endDate={}", storeId, startDate, endDate);
        return reportMapper.selectDishFlow(storeId, startDate, endDate);
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getReceiptReport(Long storeId, String date) {
        log.info("查询收款报表: storeId={}, date={}", storeId, date);
        return reportMapper.selectReceiptReport(storeId, date);
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getDishSalesRanking(Long storeId, Date startDate, Date endDate, Integer topN) {
        log.info("查询菜品销售排行: storeId={}, topN={}", storeId, topN);
        return reportMapper.selectDishSalesRanking(storeId, startDate, endDate, topN);
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getStoreSalesRanking(Date startDate, Date endDate) {
        log.info("查询门店销售排行");
        return reportMapper.selectStoreSalesRanking(startDate, endDate);
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getTodayIncome(Long storeId) {
        log.info("查询今日收入: storeId={}", storeId);
        return reportMapper.selectTodayIncome(storeId);
    }

    @Override
    @ReadOnly
    public List<Map<String, Object>> getRefundRecord(Long storeId, Date startDate, Date endDate) {
        log.info("查询退款记录: storeId={}, startDate={}, endDate={}", storeId, startDate, endDate);
        return reportMapper.selectRefundRecord(storeId, startDate, endDate);
    }
}