package com.iwe3.sec.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务
 * 每天凌晨自动清理过期的数据
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final JdbcTemplate jdbcTemplate;

    public ScheduledTasks(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 每天凌晨 3 点执行一次，清理过期数据
     * 1. 清理 30 天前的订单状态日志（保留最近 30 天，方便排查问题）
     * 2. 清理 90 天前的员工登录日志
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredData() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("【定时任务】开始清理过期数据，当前时间: {}", now);

        try {
            // 清理 30 天前的订单状态日志
            int deletedStatusLogs = jdbcTemplate.update(
                "DELETE FROM order_status_log WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY)");
            if (deletedStatusLogs > 0) {
                log.info("【定时任务】清理订单状态日志 {} 条", deletedStatusLogs);
            }

            // 清理 90 天前的员工登录日志
            int deletedLoginLogs = jdbcTemplate.update(
                "DELETE FROM staff_login_log WHERE login_time < DATE_SUB(NOW(), INTERVAL 90 DAY)");
            if (deletedLoginLogs > 0) {
                log.info("【定时任务】清理员工登录日志 {} 条", deletedLoginLogs);
            }

            log.info("【定时任务】清理过期数据完成");
        } catch (Exception e) {
            log.error("【定时任务】清理过期数据失败", e);
        }
    }
}
