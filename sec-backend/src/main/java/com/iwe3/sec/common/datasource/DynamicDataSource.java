package com.iwe3.sec.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源路由
 * 继承 Spring 的 AbstractRoutingDataSource
 * 根据 DataSourceContextHolder 中的标识动态选择主库或从库
 * <p>
 * 路由规则：
 * - master：写操作（增删改）、事务操作
 * - slave：只读查询（报表、列表）
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceType = DataSourceContextHolder.get();
        if (dataSourceType == null) {
            return DataSourceContextHolder.MASTER;
        }
        log.debug("数据源路由: {}", dataSourceType);
        return dataSourceType;
    }
}