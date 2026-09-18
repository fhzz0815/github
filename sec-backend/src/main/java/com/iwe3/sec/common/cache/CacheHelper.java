package com.iwe3.sec.common.cache;

import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * 提供三类缓存能力：
 * 1. getOrLoad：常规缓存（TTL 加随机抖动 ±10%，防雪崩）
 * 2. getWithLogicalExpire：逻辑过期 + 异步重建 + 互斥锁（防击穿）
 * 3. tryLockAndLoad：分布式锁 + 短暂重试（防热点 key 击穿）
 *
 * 读取顺序：Redis → MySQL（由 loader 回调实现）
 * 缓存一致：写操作后调用 delete() 清除缓存（Cache-Aside 模式）
 */
@Component
public class CacheHelper {

    private static final Logger log = LoggerFactory.getLogger(CacheHelper.class);

    /** 空值占位内容 */
    private static final String NULL_PLACEHOLDER = "__NULL__";

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    public CacheHelper(RedisTemplate<String, String> redisTemplate,
                       RedissonClient redissonClient,
                       ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;
    }

    // ==================== 1. 常规缓存：未命中 → 加载 → 写缓存 ====================

    /**
     * 获取缓存，未命中则通过 loader 加载并写入缓存
     * TTL 自动加随机抖动 ±10%（防缓存雪崩）
     *
     * @param key   Redis key
     * @param ttl   基础过期时间（秒）
     * @param type  返回类型
     * @param loader 数据加载回调
     * @return 缓存数据
     */
    public <T> T getOrLoad(String key, long ttl, Class<T> type, Callable<T> loader) {
        // 1. 尝试从缓存获取
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            if (NULL_PLACEHOLDER.equals(cached)) {
                return null; // 空值缓存，直接返回 null
            }
            try {
                return objectMapper.readValue(cached, type);
            } catch (Exception e) {
                log.warn("缓存反序列化失败，将重新加载: key={}", key, e);
            }
        }

        // 2. 缓存未命中，加载数据
        return loadAndCache(key, ttl, type, loader);
    }

    /**
     * 加载数据并写入缓存（带 TTL 随机抖动）
     */
    private <T> T loadAndCache(String key, long ttl, Class<T> type, Callable<T> loader) {
        try {
            T data = loader.call();
            long actualTtl = addJitter(ttl);
            if (data == null) {
                // 空值缓存：占位 60 秒（防穿透）
                redisTemplate.opsForValue().set(key, NULL_PLACEHOLDER,
                        Duration.ofSeconds(CacheKey.TTL_NULL));
                return null;
            }
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(actualTtl));
            return data;
        } catch (Exception e) {
            log.error("缓存加载数据失败: key={}", key, e);
            return null;
        }
    }

    // ==================== 2. 逻辑过期：防缓存击穿 ====================

    /**
     * 获取缓存（逻辑过期）
     * 缓存 value 内嵌 expireAt 时间戳，过期后触发异步重建 + 互斥锁
     * 适用于热点数据（如菜品详情），防止单 key 过期后大量请求回源
     *
     * @param key    Redis key
     * @param type   返回类型
     * @param loader 数据加载回调
     * @return 缓存数据（即使逻辑过期也返回旧数据，异步更新）
     */
    public <T> T getWithLogicalExpire(String key, Class<T> type, Callable<T> loader) {
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null) {
            // 缓存不存在，同步加载
            return loadAndCache(key, CacheKey.TTL_DISH, type, loader);
        }

        try {
            // 检查是否逻辑过期
            if (!isExpired(cached)) {
                return objectMapper.readValue(cached, type);
            }

            // 逻辑过期：尝试获取互斥锁，异步重建缓存
            String lockKey = "sr:lock:rebuild:" + key;
            RLock lock = redissonClient.getLock(lockKey);
            boolean locked = lock.tryLock(0, 5, TimeUnit.SECONDS);
            if (locked) {
                try {
                    // 异步重建（实际使用时建议用线程池）
                    new Thread(() -> {
                        try {
                            T data = loader.call();
                            if (data != null) {
                                String json = objectMapper.writeValueAsString(data);
                                redisTemplate.opsForValue().set(key, json,
                                        Duration.ofSeconds(addJitter(CacheKey.TTL_DISH)));
                            }
                        } catch (Exception e) {
                            log.error("逻辑过期异步重建失败: key={}", key, e);
                        }
                    }).start();
                } finally {
                    lock.unlock();
                }
            }
            // 返回旧的缓存数据
            return objectMapper.readValue(cached, type);
        } catch (Exception e) {
            log.error("逻辑过期缓存处理失败: key={}", key, e);
            return null;
        }
    }

    // ==================== 3. 分布式锁 + 加载 ====================

    /**
     * 分布式锁 + 数据加载
     * 获取锁后加载数据并缓存，拿不到锁则短暂等待后返回缓存旧值
     * 适用于极端热点 key 首次加载（如秒杀商品信息）
     *
     * @param key    Redis key
     * @param ttl    缓存过期时间（秒）
     * @param type   返回类型
     * @param loader 数据加载回调
     * @return 缓存数据
     */
    public <T> T tryLockAndLoad(String key, long ttl, Class<T> type, Callable<T> loader) {
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null && !NULL_PLACEHOLDER.equals(cached)) {
            try {
                return objectMapper.readValue(cached, type);
            } catch (Exception e) {
                log.warn("缓存反序列化失败: key={}", key);
            }
        }

        // 尝试获取分布式锁
        String lockKey = "sr:lock:load:" + key;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean locked = lock.tryLock(0, 10, TimeUnit.SECONDS);
            if (!locked) {
                // 拿不到锁，短暂等待后重试
                Thread.sleep(50);
                cached = redisTemplate.opsForValue().get(key);
                if (cached != null && !NULL_PLACEHOLDER.equals(cached)) {
                    return objectMapper.readValue(cached, type);
                }
                return null;
            }
            // 双重检查（拿到锁后可能别的线程已经更新了）
            cached = redisTemplate.opsForValue().get(key);
            if (cached != null && !NULL_PLACEHOLDER.equals(cached)) {
                return objectMapper.readValue(cached, type);
            }
            return loadAndCache(key, ttl, type, loader);
        } catch (Exception e) {
            log.error("tryLockAndLoad 失败: key={}", key, e);
            return null;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // ==================== 4. 清除缓存 ====================

    /** 删除缓存（Cache-Aside 写操作后调用） */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // ==================== 辅助方法 ====================

    /**
     * TTL 加随机抖动 ±10%，防止大量 key 同时过期导致雪崩
     */
    private long addJitter(long baseTtl) {
        double factor = 1.0 + RandomUtil.randomDouble(-0.1, 0.1);
        return Math.max(1, (long) (baseTtl * factor));
    }

    /**
     * 检查 JSON 缓存是否逻辑过期
     * 在 value 中嵌入 "__expireAt" 字段标记过期时间
     */
    private boolean isExpired(String json) {
        try {
            if (json.contains("\"__expireAt\"")) {
                long expireAt = objectMapper.readTree(json).get("__expireAt").asLong();
                return System.currentTimeMillis() > expireAt;
            }
        } catch (Exception ignored) {}
        return false;
    }
}