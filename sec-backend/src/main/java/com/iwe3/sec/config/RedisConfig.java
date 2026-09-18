package com.iwe3.sec.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类
 * 序列化key与value，便于缓存对象
 * 同时配置 Redisson 客户端（优先哨兵模式，fallback 单机模式）
 */
@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    // ===== 单机模式配置（开发/测试环境兜底） =====
    @Value("${spring.data.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    // ===== 哨兵模式配置（生产环境） =====
    @Value("${redis.sentinel.master:mymaster}")
    private String sentinelMaster;

    @Value("${redis.sentinel.nodes:}")
    private String sentinelNodes;

    // ===== 通用配置 =====
    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${redisson.connection.timeout:5000}")
    private int timeout;

    @Value("${redisson.connection.retry-attempts:3}")
    private int retryAttempts;

    @Value("${redisson.connection.retry-interval:1000}")
    private int retryInterval;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisSerializer<?> jsonSerializer = RedisSerializer.json();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redisson 客户端（哨兵模式优先，单机模式兜底）
     *
     * 生产环境：
     *   配置 redis.sentinel.nodes=sentinel1:26379,sentinel2:26380,sentinel3:26381
     *   自动启用哨兵模式，故障转移时自动切换 master
     *
     * 开发环境：
     *   不配 sentinel.nodes，回退到 useSingleServer 连接本地 Redis
     */
    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            String password = (redisPassword == null || redisPassword.isEmpty()) ? null : redisPassword;

            if (sentinelNodes != null && !sentinelNodes.isBlank()) {
                // 哨兵模式：读取 sentinel.nodes 列表，动态分配哨兵地址
                String[] nodes = sentinelNodes.split(",");
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i] = nodes[i].startsWith("redis://") ? nodes[i] : "redis://" + nodes[i].trim();
                    nodes[i] = nodes[i].replace("redis://redis://", "redis://");
                }
                config.useSentinelServers()
                        .setMasterName(sentinelMaster)
                        .addSentinelAddress(nodes)
                        .setPassword(password)
                        .setDatabase(redisDatabase)
                        .setTimeout(timeout)
                        .setRetryAttempts(retryAttempts)
                        .setRetryInterval(retryInterval)
                        .setCheckSentinelsList(false);
                log.info("Redisson 哨兵模式初始化：master={}, nodes={}", sentinelMaster, sentinelNodes);
            } else {
                // 单机模式（开发环境兜底）
                config.useSingleServer()
                        .setAddress("redis://" + redisHost + ":" + redisPort)
                        .setPassword(password)
                        .setDatabase(redisDatabase)
                        .setTimeout(timeout)
                        .setRetryAttempts(retryAttempts)
                        .setRetryInterval(retryInterval);
                log.info("Redisson 单机模式初始化：{}:{}", redisHost, redisPort);
            }

            return Redisson.create(config);
        } catch (Exception e) {
            log.warn("Redisson 连接失败，Redis 相关功能将降级运行", e);
            return null;
        }
    }

    /**
     * StringRedisTemplate 用于 CacheHelper 的字符串存取操作
     * 与 RedisTemplate<String, Object> 共用同一个连接工厂
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * 缓存管理器（用于 @Cacheable、@CacheEvict 等注解）
     * 使用 JSON 序列化缓存值，默认过期 30 分钟
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
