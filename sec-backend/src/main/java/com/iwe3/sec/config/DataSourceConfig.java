package com.iwe3.sec.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.iwe3.sec.common.datasource.DynamicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置
 * <p>
 * 主库数据源由 Druid 自动装配（基于 application.yml 的 spring.datasource.druid.* 配置），
 * 此处只负责将从库数据源和动态路由注册到 Spring 容器中。
 * <p>
 * 从库仅在生产环境启用，通过环境变量 DB_SLAVE_URL 指定真实从库地址；
 * 开发/测试环境可不配置，此时从库使用主库地址，读写分离逻辑正常运行。
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    /**
     * 从库数据源
     * 生产环境通过环境变量 DB_SLAVE_URL 配置真实从库地址，
     * 未配置时默认使用主库地址（开发/测试环境可共用本地库）
     */
    @Bean
    public DataSource slaveDataSource() {
        String slaveUrl = System.getenv().getOrDefault("DB_SLAVE_URL",
                "jdbc:mysql://localhost:3306/smart_restaurant?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf-8");

        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(slaveUrl);
        ds.setUsername(System.getenv().getOrDefault("DB_SLAVE_USER", "root"));
        ds.setPassword(System.getenv().getOrDefault("DB_SLAVE_PASS", "123456"));
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setInitialSize(3);
        ds.setMaxActive(20);
        ds.setMinIdle(2);
        ds.setMaxWait(60000);
        log.info("初始化从库数据源: {}", slaveUrl);
        return ds;
    }

    /**
     * 动态数据源路由
     * 主库使用 Druid 自动装配的数据源，从库使用上面定义的 slaveDataSource
     */
    @Primary
    @Bean
    public DynamicDataSource dynamicDataSource(DataSource dataSource,
                                               DataSource slaveDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", dataSource);
        targetDataSources.put("slave", slaveDataSource);

        DynamicDataSource routingDataSource = new DynamicDataSource();
        routingDataSource.setDefaultTargetDataSource(dataSource);
        routingDataSource.setTargetDataSources(targetDataSources);
        log.info("动态数据源初始化完成（主库+从库）");
        return routingDataSource;
    }
}
