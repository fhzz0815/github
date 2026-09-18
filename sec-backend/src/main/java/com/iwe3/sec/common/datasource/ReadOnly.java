package com.iwe3.sec.common.datasource;

import java.lang.annotation.*;

/**
 * 只读注解
 * 标注在方法或类上，表示该操作仅查询数据
 * AOP 切面识别此注解后将路由到从库（slave）查询
 * <p>
 * 使用示例：
 * {@code @ReadOnly}
 * public PageResult<Dish> listDishes(...) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadOnly {
}