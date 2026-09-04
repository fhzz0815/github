package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dining_table 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiningTableEntity {

    // 台桌ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 桌型ID
    private Long tableTypeId;
    // 桌号
    private String tableNo;
    // 桌位名称
    private String tableName;
    // 桌位二维码(扫码点餐)
    private String qrCode;
    // 台桌状态 1空闲 2待点餐 3用餐中 4预结账 5已结账
    private Integer status;
    // 当前用餐人数
    private Integer personCount;
    // 是否可预约 1是 0否
    private Integer enableReservation;
    // 排序号
    private Integer sort;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;
    // 逻辑删除 0否 1是
    private Integer isDeleted;
}
