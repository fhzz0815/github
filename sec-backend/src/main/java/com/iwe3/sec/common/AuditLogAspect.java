package com.iwe3.sec.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 审计日志切面
 * 自动记录所有加了 @AuditLog 注解的方法调用信息
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_LOG");

    @Around("@annotation(auditLogAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLogAnnotation) throws Throwable {
        // 记录操作前的信息
        String operation = auditLogAnnotation.operation();
        String type = auditLogAnnotation.type();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 执行原方法
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            // 记录操作成功日志
            auditLog.info("[操作审计] 时间={}, 操作={}, 类型={}, 类={}, 方法={}, 耗时={}ms, 状态=成功",
                    timestamp, operation, type, className, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            // 记录操作失败日志
            auditLog.error("[操作审计] 时间={}, 操作={}, 类型={}, 类={}, 方法={}, 耗时={}ms, 状态=失败, 异常={}",
                    timestamp, operation, type, className, methodName, duration, e.getMessage());

            throw e;
        }
    }
}
