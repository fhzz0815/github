package com.iwe3.sec.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 请求日志过滤器
 * 记录每个 API 请求的耗时、路径、状态码，方便排查问题
 */
@Component
@Order(2)
public class RequestLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    /** 不记录日志的路径（静态资源、健康检查等） */
    private static final String[] EXCLUDE_PATHS = {
        "/actuator/health", "/actuator/info",
        "/swagger-ui", "/v3/api-docs", "/druid"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // 跳过不需要记录的路径
        for (String exclude : EXCLUDE_PATHS) {
            if (path.startsWith(exclude)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 生成追踪 ID，方便在日志中关联同一个请求
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = resp.getStatus();

            // 慢请求（超过 3 秒）用 warn 级别记录，方便排查性能问题
            if (duration > 3000) {
                log.warn("[请求日志] traceId={}, method={}, path={}, status={}, 耗时={}ms, IP={} ★ 慢请求",
                        traceId, req.getMethod(), path, status, duration, getClientIp(req));
            } else if (status >= 500) {
                log.error("[请求日志] traceId={}, method={}, path={}, status={}, 耗时={}ms, IP={} ★ 服务端错误",
                        traceId, req.getMethod(), path, status, duration, getClientIp(req));
            } else {
                log.info("[请求日志] traceId={}, method={}, path={}, status={}, 耗时={}ms, IP={}",
                        traceId, req.getMethod(), path, status, duration, getClientIp(req));
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
