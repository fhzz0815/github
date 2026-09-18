package com.iwe3.sec.controller;

import com.iwe3.sec.common.PageResult;
import com.iwe3.sec.common.Result;
import com.iwe3.sec.service.IReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表控制器
 * 提供 6 个报表查询接口，所有接口返回统一 Result 格式
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "报表中心", description = "菜品流水、收款、排行等报表查询")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final IReportService reportServiceImpl;

    public ReportController(IReportService reportServiceImpl) {
        this.reportServiceImpl = reportServiceImpl;
    }

    /**
     * 菜品流水报表
     * 查询某段时间内各门店的菜品销售明细
     */
    @GetMapping("/dish-flow")
    @Operation(summary = "菜品流水报表")
    public Result<List<Map<String, Object>>> getDishFlow(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Map<String, Object>> data = reportServiceImpl.getDishFlow(storeId, startDate, endDate);
        return Result.success(data);
    }

    /**
     * 收款报表
     * 按日期统计各门店收款情况（微信、支付宝、现金、刷卡）
     */
    @GetMapping("/receipt")
    @Operation(summary = "收款报表")
    public Result<List<Map<String, Object>>> getReceiptReport(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String date) {
        List<Map<String, Object>> data = reportServiceImpl.getReceiptReport(storeId, date);
        return Result.success(data);
    }

    /**
     * 菜品销售排行
     * 按销量或金额排序展示热门菜品
     */
    @GetMapping("/dish-ranking")
    @Operation(summary = "菜品销售排行")
    public Result<List<Map<String, Object>>> getDishSalesRanking(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "20") Integer topN) {
        List<Map<String, Object>> data = reportServiceImpl.getDishSalesRanking(storeId, startDate, endDate, topN);
        return Result.success(data);
    }

    /**
     * 门店销售排行
     * 多门店之间的销售业绩对比
     */
    @GetMapping("/store-ranking")
    @Operation(summary = "门店销售排行")
    public Result<List<Map<String, Object>>> getStoreSalesRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Map<String, Object>> data = reportServiceImpl.getStoreSalesRanking(startDate, endDate);
        return Result.success(data);
    }

    /**
     * 今日收入概况
     * 今日各门店的实时收入、订单数、平均客单价
     */
    @GetMapping("/today-income")
    @Operation(summary = "今日收入概况")
    public Result<List<Map<String, Object>>> getTodayIncome(
            @RequestParam(required = false) Long storeId) {
        List<Map<String, Object>> data = reportServiceImpl.getTodayIncome(storeId);
        return Result.success(data);
    }

    /**
     * 退款记录
     * 查询退款明细列表
     */
    @GetMapping("/refunds")
    @Operation(summary = "退款记录")
    public Result<List<Map<String, Object>>> getRefundRecord(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Map<String, Object>> data = reportServiceImpl.getRefundRecord(storeId, startDate, endDate);
        return Result.success(data);
    }
}