package com.iwe3.sec.common.datasource;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据源切面
 * 识别 @ReadOnly 注解，自动将数据源切换到从库
 * 执行完成后自动清理 ThreadLocal
 * <p>
 * 优先执行（@Order(0)），确保在事务开启前切换数据源
 */
@Aspect
@Component
@Order(0)
public class DataSourceAspect {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    @Before("@annotation(com.iwe3.sec.common.datasource.ReadOnly)")
    public void setReadOnlyDataSource() {
        log.debug("切换到从库（只读）");
        DataSourceContextHolder.set(DataSourceContextHolder.SLAVE);
    }

    @After("@annotation(com.iwe3.sec.common.datasource.ReadOnly)")
    public void clearDataSource() {
        DataSourceContextHolder.clear();
    }
}