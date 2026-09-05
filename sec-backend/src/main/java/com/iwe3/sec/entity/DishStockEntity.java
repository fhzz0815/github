package com.iwe3.sec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * dish_stock 表对应的实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishStockEntity {

    // 菜品库存ID
    private Long id;
    // 门店ID
    private Long storeId;
    // 菜品ID
    private Long dishId;
    // 当前库存数量
    private Integer stockQuantity;
    // 预警阈值
    private Integer warnThreshold;
    // 计量单位(份)
    private String unit;
    // 创建时间
    private java.util.Date createTime;
    // 更新时间
    private java.util.Date updateTime;

    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}
