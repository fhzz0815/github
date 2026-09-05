package com.iwe3.sec.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工列表查询时的数据范围过滤器
 * 总店长：allStores=true，全国可见
 * 店长：storeId+includeSameLevelCrossStore=true，本门店全部 + 跨门店同级
 * 普通员工：levelOnly=N，仅同级（跨门店）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDataScope {
    // true 表示总店长，不加门店过滤
    private boolean allStores;
    // 仅返回此门店的员工（店长场景）
    private Long storeId;
    // 仅返回此等级的员工（普通员工场景，跨门店同级）
    private Integer levelOnly;
    // 当前用户的等级，用于"跨门店同级"过滤
    private Integer currentLevel;
    // 是否包含跨门店的同级员工（店长场景：本门店全部 + 跨门店同级）
    private boolean includeSameLevelCrossStore;
}
