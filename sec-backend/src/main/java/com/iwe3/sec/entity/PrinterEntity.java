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
}
