package com.iwe3.sec.integration;

import com.iwe3.sec.SecApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试：Spring 容器完整启动验证
 * 真实加载全部配置（application.yml + dev profile），
 * 验证核心依赖组件（Druid数据源、MyBatis、Redisson、Redis）都能正常初始化。
 */
@SpringBootTest(classes = SecApplication.class)
class SpringContextIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Spring 容器应成功启动")
    void context_shouldLoad() {
        assertNotNull(context, "Spring 上下文不应为空");
        assertTrue(context.getBeanDefinitionCount() > 200,
                "Bean 数量应在 200 以上，实际: " + context.getBeanDefinitionCount());
    }

    @Test
    @DisplayName("Druid 数据源应正常注册")
    void dataSource_shouldBeAvailable() {
        DataSource dataSource = context.getBean(DataSource.class);
        assertNotNull(dataSource, "数据源 Bean 必须存在");
        assertTrue(dataSource.getClass().getName().contains("druid"),
                "应使用 Druid 连接池，实际: " + dataSource.getClass().getName());
    }

    @Test
    @DisplayName("Redisson 客户端应连接成功（非降级 null）")
    void redissonClient_shouldConnect() {
        RedissonClient client = context.getBean(RedissonClient.class);
        assertNotNull(client, "RedissonClient 不应为 null（Redis 连接失败会降级为 null）");
        assertTrue(client.getNodesGroup().getNodes().size() > 0, "应至少有 1 个 Redis 节点");
    }

    @Test
    @DisplayName("RedisTemplate 应可执行读写")
    void redisTemplate_shouldWork() {
        // 容器中有两个 RedisTemplate（自定义 redisTemplate 与 Spring Boot 自动的 stringRedisTemplate），按名称取
        @SuppressWarnings("unchecked")
        RedisTemplate<String, Object> template = (RedisTemplate<String, Object>) context.getBean("redisTemplate");
        String key = "it:test:context:" + System.currentTimeMillis();
        template.opsForValue().set(key, "ok");
        assertEquals("ok", template.opsForValue().get(key));
        template.delete(key);
        assertNull(template.opsForValue().get(key));
    }
}
