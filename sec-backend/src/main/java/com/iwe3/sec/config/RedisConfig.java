package com.iwe3.sec.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 序列化key与value，便于缓存对象
 * 同时配置 Redisson 单机客户端
 */
@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${redisson.single-server-config.timeout:5000}")
    private int timeout;

    @Value("${redisson.single-server-config.retry-attempts:3}")
    private int retryAttempts;

    @Value("${redisson.single-server-config.retry-interval:1000}")
    private int retryInterval;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用 RedisSerializer.json() 替代已过时的 Jackson2JsonRedisSerializer
        // 它会在序列化时自动带上 @class 类型信息，确保反序列化时能正确还原对象类型
        RedisSerializer<?> jsonSerializer = RedisSerializer.json();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用 String 序列化，value 采用 JSON 序列化（带类型信息）
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redisson 单机客户端
     * 读取 spring.data.redis 配置，连接本地 Redis 单机
     */
    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            // 本机开发环境 Redis 通常未设密码：配置为空时必须传 null，否则 Redisson 会发送 AUTH 报错
            config.useSingleServer()
                    .setAddress("redis://" + redisHost + ":" + redisPort)
                    .setPassword(redisPassword == null || redisPassword.isEmpty() ? null : redisPassword)
                    .setDatabase(redisDatabase)
                    .setTimeout(timeout)
                    .setRetryAttempts(retryAttempts)
                    .setRetryInterval(retryInterval);

            return Redisson.create(config);
        } catch (Exception e) {
            // Redis 不可用时降级处理，不影响主程序启动
            log.warn("Redisson 连接失败，Redis 相关功能将降级运行", e);
            return null;
        }
    }
}
