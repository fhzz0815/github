package com.iwe3.sec.common;

/**
 * 统一错误码常量
 * 分段说明：
 *   1xxx — 参数校验错误
 *   2xxx — 业务逻辑错误（订单、库存、优惠券等）
 *   3xxx — 权限校验错误
 *   4xxx — 支付相关错误
 *   5xxx — 系统内部错误
 */
public final class ErrorCode {

    // ========== 通用 ==========
    /** 操作成功 */
    public static final int SUCCESS = 0;
    /** 系统内部错误 */
    public static final int SYSTEM_ERROR = 500;
    /** 参数校验失败 */
    public static final int PARAM_ERROR = 1001;
    /** 请求的资源不存在 */
    public static final int NOT_FOUND = 1002;
    /** 数据已存在（唯一键冲突） */
    public static final int DUPLICATE_KEY = 1006;

    // ========== 账号与认证 (1xxx) ==========
    /** 账号不存在 */
    public static final int ACCOUNT_NOT_FOUND = 1010;
    /** 账号已被禁用 */
    public static final int ACCOUNT_DISABLED = 1011;
    /** 账号已被停用 */
    public static final int ACCOUNT_SUSPENDED = 1012;
    /** 密码错误 */
    public static final int PASSWORD_ERROR = 1013;

    // ========== 订单状态机 (2xxx) ==========
    /** 订单状态已变更，请刷新后重试（乐观锁冲突） */
    public static final int ORDER_STATUS_CHANGED = 2001;
    /** 订单状态流转不合法 */
    public static final int ORDER_STATUS_INVALID = 2002;
    /** 订单不存在 */
    public static final int ORDER_NOT_FOUND = 2003;
    /** 订单已支付，请勿重复操作 */
    public static final int ORDER_ALREADY_PAID = 2004;
    /** 订单已完成或已取消，无法操作 */
    public static final int ORDER_FINISHED_OR_CANCELLED = 2005;

    // ========== 库存 (2xxx) ==========
    /** 库存不足 */
    public static final int STOCK_NOT_ENOUGH = 2010;
    /** 库存扣减失败 */
    public static final int STOCK_DECREASE_FAILED = 2011;

    // ========== 优惠券 (2xxx) ==========
    /** 优惠券已领完 */
    public static final int COUPON_RUN_OUT = 2020;
    /** 优惠券已领取过 */
    public static final int COUPON_ALREADY_RECEIVED = 2021;
    /** 优惠券不存在 */
    public static final int COUPON_NOT_FOUND = 2022;
    /** 优惠券已过期或已下架 */
    public static final int COUPON_EXPIRED = 2023;
    /** 优惠券还未到领取时间 */
    public static final int COUPON_NOT_STARTED = 2024;
    /** 每人限领已达上限 */
    public static final int COUPON_LIMIT_REACHED = 2025;
    /** 优惠券已售罄 */
    public static final int COUPON_SOLD_OUT = 2026;

    // ========== 支付 (4xxx) ==========
    /** 支付失败 */
    public static final int PAY_FAILED = 4001;
    /** 重复回调（幂等） */
    public static final int PAY_DUPLICATE_CALLBACK = 4002;
    /** 支付金额不匹配 */
    public static final int PAY_AMOUNT_MISMATCH = 4003;

    // ========== 权限 (3xxx) ==========
    /** 无权限 */
    public static final int FORBIDDEN = 3001;
    /** 未登录 */
    public static final int UNAUTHORIZED = 3002;

    private ErrorCode() {
        // 工具类，禁止实例化
    }
}
