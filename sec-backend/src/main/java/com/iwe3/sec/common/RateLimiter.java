package com.iwe3.sec.common;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 分布式限流器（基于 Redis + Redisson）
 * 相比原来的内存令牌桶，这个支持多实例共享限流，集群部署时限额不放大
 * 同时解决了内存版 ConcurrentHashMap 只增不删导致的内存泄漏问题
 */
@Component
public class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private final RedissonClient redissonClient;

    public RateLimiter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 检查是否允许通过（基于 Redis 的分布式限流）
     *
     * @param key      限制标识（如 IP、用户ID），会自动加上 "sr:ratelimit:" 前缀
     * @param capacity 桶容量（最大突发请求数）
     * @param tokens   每秒生成的令牌数（平均速率）
     * @return true=允许通过, false=被限流
     */
    public boolean tryAcquire(String key, int capacity, int tokens) {
        String redisKey = "sr:ratelimit:" + key;
        try {
            // 获取或创建分布式限流器
            // 注意：RRateLimiter 的 rate 需要在首次使用时设置，之后不能修改
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
            if (!rateLimiter.isExists()) {
                // 首次创建：设置速率（每秒 tokens 个令牌，最大突发 capacity）
                rateLimiter.trySetRate(RateType.OVERALL, tokens, 1, RateIntervalUnit.SECONDS);
                // 注意：Redisson 的 RRateLimiter 不直接支持 capacity（突发大小），
                // 这里用 tokens 作为速率。想要支持突发的话，需要用多个 key 或自定义实现。
                // 不过对于常见的 API 限流场景，这个已经够用了。
            }
            return rateLimiter.tryAcquire(1);
        } catch (Exception e) {
            // Redis 不可用时放行，避免限流故障影响正常业务
            log.warn("Redis 限流异常，暂时放行：key={}, err={}", redisKey, e.getMessage());
            return true;
        }
    }

    /**
     * 检查是否允许通过，支持设置冷却时间（常用于登录/短信等场景）
     *
     * @param key            限制标识
     * @param maxRequests    窗口期内最大请求数
     * @param windowSeconds  窗口期（秒）
     * @return true=允许通过, false=被限流
     */
    public boolean tryAcquireWithWindow(String key, int maxRequests, int windowSeconds) {
        String redisKey = "sr:ratelimit:window:" + key;
        try {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
            if (!rateLimiter.isExists()) {
                // 窗口期内最多 maxRequests 次
                rateLimiter.trySetRate(RateType.OVERALL, maxRequests, windowSeconds, RateIntervalUnit.SECONDS);
            }
            return rateLimiter.tryAcquire(1);
        } catch (Exception e) {
            log.warn("Redis 窗口限流异常，暂时放行：key={}, err={}", redisKey, e.getMessage());
            return true;
        }
    }
}
