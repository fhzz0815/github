package com.iwe3.sec.common.datasource;

/**
 * 数据源上下文持有者
 * 使用 ThreadLocal 存储当前线程应使用的数据源标识
 * 读写分离时：master=写库，slave=读库
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /** 写库（主库）标识 */
    public static final String MASTER = "master";
    /** 读库（从库）标识 */
    public static final String SLAVE = "slave";

    /**
     * 设置当前线程的数据源
     * @param dataSourceType master 或 slave
     */
    public static void set(String dataSourceType) {
        CONTEXT.set(dataSourceType);
    }

    /**
     * 获取当前线程的数据源
     */
    public static String get() {
        return CONTEXT.get();
    }

    /**
     * 清除当前线程的数据源（切面中 finally 调用）
     */
    public static void clear() {
        CONTEXT.remove();
    }
}