package com.iwe3.sec.common.lock;

/**
 * 分布式锁获取失败异常
 * 当无法获取到锁时抛出，上层捕获后提示"操作太频繁"
 */
public class LockAcquisitionException extends RuntimeException {

    public LockAcquisitionException(String message) {
        super(message);
    }

    public LockAcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
}