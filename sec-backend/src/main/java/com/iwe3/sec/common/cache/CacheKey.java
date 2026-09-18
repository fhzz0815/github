package com.iwe3.sec.common.cache;

import java.util.concurrent.TimeUnit;

/**
 * 缓存键与 TTL 常量定义
 * 统一管理所有 Redis key 的命名前缀和过期时间
 * key 格式：sr:模块:业务:{id}
 */
public final class CacheKey {

    private CacheKey() {}

    // ========== Key 前缀 ==========
    public static final String PREFIX_DISH = "sr:dish:detail:";
    public static final String PREFIX_DISH_LIST = "sr:dish:list:";
    public static final String PREFIX_DISH_CATEGORY = "sr:dish:category:";
    public static final String PREFIX_DISH_SPEC = "sr:dish:spec:";
    public static final String PREFIX_DISH_TASTE = "sr:dish:taste:";
    public static final String PREFIX_STORE = "sr:store:";
    public static final String PREFIX_TABLE = "sr:table:";
    public static final String PREFIX_ROLE = "sr:role:";
    public static final String PREFIX_PERMISSION = "sr:permission:";
    public static final String PREFIX_MEMBER_CATEGORY = "sr:member:category:";
    public static final String PREFIX_MEMBER = "sr:member:";
    public static final String PREFIX_TABLE_TYPE = "sr:table:type:";
    public static final String PREFIX_PAYMENT_SETTING = "sr:payment:setting:";
    public static final String PREFIX_NULL_MARKER = "sr:null:";     // 空值占位标识
    public static final String PREFIX_RANK_DISH = "sr:rank:dish:";  // 菜品排行 ZSet

    // ========== TTL 常量（单位：秒） ==========
    /** 菜品详情缓存 30 分钟 */
    public static final long TTL_DISH = 1800;
    /** 菜品列表缓存 15 分钟 */
    public static final long TTL_DISH_LIST = 900;
    /** 菜品分类缓存 1 小时 */
    public static final long TTL_CATEGORY = 3600;
    /** 会员信息缓存 30 分钟 */
    public static final long TTL_MEMBER = 1800;
    /** 门店信息缓存 1 小时 */
    public static final long TTL_STORE = 3600;
    /** 餐桌信息缓存 30 分钟 */
    public static final long TTL_TABLE = 1800;
    /** 角色/权限缓存 2 小时 */
    public static final long TTL_ROLE = 7200;
    /** 空值占位缓存 60 秒（防穿透） */
    public static final long TTL_NULL = 60;
    /** 热点排行缓存 5 分钟 */
    public static final long TTL_RANK = 300;
}
