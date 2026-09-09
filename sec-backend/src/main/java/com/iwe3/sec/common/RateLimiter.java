package com.iwe3.sec.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的令牌桶限流器
 * 用于防止 API 被恶意频繁调用
 */
public class RateLimiter {

    /** 令牌桶：key = 限制标识, value = 限流器 */
    private static final ConcurrentHashMap<String, TokenBucket> BUCKETS = new ConcurrentHashMap<>();

    /**
     * 检查是否允许通过
     *
     * @param key      限制标识（如 IP、用户ID）
     * @param capacity 桶容量（最大突发请求数）
     * @param tokens   每秒生成的令牌数（平均速率）
     * @return true=允许通过, false=被限流
     */
    public static boolean tryAcquire(String key, int capacity, int tokens) {
        TokenBucket bucket = BUCKETS.computeIfAbsent(key, k -> new TokenBucket(capacity, tokens));
        return bucket.tryAcquire();
    }

    /**
     * 令牌桶内部实现
     */
    private static class TokenBucket {
        private final int capacity;
        private final int tokensPerSecond;
        private long lastRefillTime;
        private double availableTokens;

        TokenBucket(int capacity, int tokensPerSecond) {
            this.capacity = capacity;
            this.tokensPerSecond = tokensPerSecond;
            this.lastRefillTime = System.nanoTime();
            this.availableTokens = capacity;
        }

        synchronized boolean tryAcquire() {
            refill();
            if (availableTokens >= 1) {
                availableTokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsedSeconds = (double) (now - lastRefillTime) / 1_000_000_000;
            double tokensToAdd = elapsedSeconds * tokensPerSecond;
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}
