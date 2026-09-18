package com.iwe3.sec.common.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁模板工具类
 * 封装 Redisson tryLock + finally unlock 的标准模式
 * 不传 leaseTime 即启用看门狗自动续期（默认 30 秒，业务未完成自动续）
 * <p>
 * 使用场景：
 * - 库存扣减：sr:lock:stock:dish:{dishId}
 * - 优惠券领取：sr:lock:coupon:{couponId}:member:{memberId}
 * - 收银结账：sr:lock:checkout:order:{orderId}
 */
@Component
public class DistributedLockTemplate {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockTemplate.class);

    /** 默认等待锁的时间（秒） */
    private static final long DEFAULT_WAIT_TIME = 3;
    /** 默认锁持有时间（秒），不传则启用看门狗自动续期 */
    private static final long DEFAULT_LEASE_TIME = -1;

    private final RedissonClient redissonClient;

    public DistributedLockTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 执行加锁操作（默认等待 3 秒，看门狗自动续期）
     *
     * @param lockKey  锁 key
     * @param callback 回调逻辑
     * @param <T>      返回值类型
     * @return 回调结果
     */
    public <T> T tryLock(String lockKey, LockCallback<T> callback) {
        return tryLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.SECONDS, callback);
    }

    /**
     * 执行加锁操作（自定义参数）
     *
     * @param lockKey   锁 key
     * @param waitTime  等待锁的时间
     * @param leaseTime 锁持有时间（-1 启用看门狗自动续期）
     * @param unit      时间单位
     * @param callback  回调逻辑
     * @param <T>       返回值类型
     * @return 回调结果
     * @throws LockAcquisitionException 获取锁失败时抛出
     */
    public <T> T tryLock(String lockKey, long waitTime, long leaseTime,
                         TimeUnit unit, LockCallback<T> callback) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            if (leaseTime > 0) {
                locked = lock.tryLock(waitTime, leaseTime, unit);
            } else {
                locked = lock.tryLock(waitTime, unit);
                // leaseTime <= 0 时启用看门狗：不传 leaseTime，Redisson 自动续期
            }
            if (!locked) {
                log.warn("获取分布式锁超时: lockKey={}, waitTime={}{}", lockKey, waitTime, unit);
                throw new LockAcquisitionException("操作太频繁，请稍后重试");
            }
            log.debug("获取分布式锁成功: lockKey={}", lockKey);
            return callback.execute();
        } catch (LockAcquisitionException e) {
            throw e;
        } catch (Exception e) {
            log.error("分布式锁执行失败: lockKey={}", lockKey, e);
            throw new LockAcquisitionException("系统繁忙，请稍后重试");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("释放分布式锁: lockKey={}", lockKey);
            }
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute() throws Exception;
    }
}