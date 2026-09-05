package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * printer 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrinterEntity {

    // 打印机ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 打印机名称
    private String printerName;
    // 打印机编号
    private String printerNo;
    // 用途 KITCHEN后厨 RECEIPT前台 TAKEOUT外卖
    private String printerType;
    // 状态 1启用 0停用
    private Integer status;
    // 创建时间
    private java.util.Date createTime;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
