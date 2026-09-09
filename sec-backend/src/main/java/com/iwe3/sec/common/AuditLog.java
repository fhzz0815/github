package com.iwe3.sec.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作审计日志注解
 * 加在 Controller 方法上，记录谁在什么时间做了什么事
 *
 * 用法示例：
 * @AuditLog(operation = "新增菜品")
 * public Result<Void> add(@RequestBody DishEntity entity) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /** 操作描述（如：新增菜品、删除订单） */
    String operation();

    /** 操作类型（add/update/delete/query/other） */
    String type() default "other";
}
