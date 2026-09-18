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

/**
 * API 限流过滤器
 * 根据客户端 IP 对每个接口进行限流，防止恶意调用
 */
@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    /** 每个 IP 每秒最多允许的请求数 */
    private static final int MAX_REQUESTS_PER_SECOND = 20;

    /** 每个 IP 最大突发请求数 */
    private static final int MAX_BURST = 50;

    private final RateLimiter rateLimiter;

    public RateLimitFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String clientIp = getClientIp(req);

        // 检查是否被限流（通过注入的 RateLimiter 实例调用，而非静态调用）
        if (!rateLimiter.tryAcquire(clientIp, MAX_BURST, MAX_REQUESTS_PER_SECOND)) {
            log.warn("API 被限流，IP: {}, 路径: {}", clientIp, req.getRequestURI());
            resp.setStatus(429);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"code\":429,\"message\":\"请求太频繁了，请稍后再试\",\"data\":null}");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 获取客户端真实 IP（考虑代理转发的情况）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
