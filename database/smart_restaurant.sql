-- =====================================================================
-- 智慧餐厅（Smart Restaurant）MySQL 8.0 数据库脚本  V3.0（规范对齐版：逻辑外键 + utf8mb4_general_ci）
-- 说明：本脚本包含数据库创建、建表语句、索引、视图及初始化数据
-- 适用：MySQL 8.0+ / InnoDB / utf8mb4
-- 三端体系：客户端（微信小程序）、员工端（APP）、商户端（Web）
-- 字符集：utf8mb4_general_ci
-- 设计约定：
--   0. 表间关联完整性由【逻辑外键+索引】保障（不使用物理外键约束），ER 关系见数据库字典
--   1. 主键统一为 id BIGINT UNSIGNED AUTO_INCREMENT
--   2. 软删除统一使用 is_deleted (0未删除 1已删除)
--   3. 创建/更新时间统一为 create_time / update_time
--   4. 金额统一使用 DECIMAL(10,2)
--   5. 状态字段统一使用 TINYINT 并附注释说明枚举含义
-- 工程化说明（V2.0 新增）：
--   A. 并发与幂等：orders.version 乐观锁防止状态并发覆盖；payment_record.idempotency_key 幂等键防重复支付回调
--   B. 报表支撑：文末提供 6 个报表视图（菜品流水/收款日报/菜品销售排行/门店销售排行/今日收入/退单记录）
--   C. 索引覆盖高频查询：后厨按制作状态、收款按时间汇总、会员流水按来源追溯等
--   D. 安全建议：演示数据密码为 MD5(123456)；生产环境务必改用 bcrypt/scrypt + 盐，禁止存储明文与裸 MD5
--   E. 扩展预留：订单表按量可做按月分区/分表；高并发下建议 Redis 缓存菜品与台桌状态、消息队列解耦下单/出单
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `smart_restaurant`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `smart_restaurant`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 第一部分：系统权限与账号（角色、权限、员工账号、排班、登录日志）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. sys_role 角色表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`   VARCHAR(50)     NOT NULL                COMMENT '角色名称',
  `role_code`   VARCHAR(50)     NOT NULL                COMMENT '角色编码',
  `level`       INT             NOT NULL DEFAULT 10     COMMENT '角色等级（数字越大权限越高 99/50/10）',
  `description` VARCHAR(255)    DEFAULT NULL            COMMENT '角色描述',
  `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`  TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ---------------------------------------------------------------------
-- 2. sys_permission 权限表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(50)     NOT NULL                COMMENT '权限名称',
  `permission_code` VARCHAR(50)     NOT NULL                COMMENT '权限编码（接口/按钮标识）',
  `menu_url`        VARCHAR(200)    DEFAULT NULL            COMMENT '菜单URL',
  `parent_id`       BIGINT UNSIGNED NOT NULL DEFAULT 0      COMMENT '父级ID，0为顶级',
  `sort`            INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`          TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

-- ---------------------------------------------------------------------
-- 3. sys_role_permission 角色-权限关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`       BIGINT UNSIGNED NOT NULL                COMMENT '角色ID',
  `permission_id` BIGINT UNSIGNED NOT NULL                COMMENT '权限ID',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色-权限关联表';

-- ---------------------------------------------------------------------
-- 4. sys_user 员工/商户账号表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `store_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '所属门店ID（总店长为空）',
  `username`       VARCHAR(50)     NOT NULL                COMMENT '登录账号（手机号）',
  `password`       VARCHAR(255)    NOT NULL                COMMENT '登录密码（生产用bcrypt加密存储）',
  `real_name`      VARCHAR(50)     DEFAULT NULL            COMMENT '姓名',
  `staff_no`       VARCHAR(20)     DEFAULT NULL            COMMENT '员工工号',
  `email`          VARCHAR(100)    DEFAULT NULL            COMMENT '邮箱',
  `phone`          VARCHAR(20)     DEFAULT NULL            COMMENT '手机号',
  `id_card`        VARCHAR(20)     DEFAULT NULL            COMMENT '身份证号',
  `role_id`        BIGINT UNSIGNED NOT NULL                COMMENT '角色ID',
  `avatar`         VARCHAR(255)    DEFAULT NULL            COMMENT '头像URL',
  `status`         TINYINT         NOT NULL DEFAULT 1      COMMENT '账号状态 1在职 0离职',
  `last_login_time` DATETIME       DEFAULT NULL            COMMENT '最后登录时间',
  `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`     TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工/商户账号表';

-- ---------------------------------------------------------------------
-- 5. staff_schedule 员工排班表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `staff_schedule`;
CREATE TABLE `staff_schedule` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '排班ID',
  `staff_id`   BIGINT UNSIGNED NOT NULL                COMMENT '员工ID',
  `work_date`  DATE            NOT NULL                COMMENT '排班日期',
  `shift_type` TINYINT         NOT NULL DEFAULT 1      COMMENT '班次 1早班 2中班 3晚班',
  `start_time` TIME            DEFAULT NULL            COMMENT '上班时间',
  `end_time`   TIME            DEFAULT NULL            COMMENT '下班时间',
  `remark`     VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_staff_date` (`staff_id`,`work_date`),
  KEY `idx_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工排班表';

-- ---------------------------------------------------------------------
-- 6. staff_login_log 员工登录日志表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `staff_login_log`;
CREATE TABLE `staff_login_log` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `staff_id`    BIGINT UNSIGNED NOT NULL                COMMENT '员工ID',
  `login_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip`    VARCHAR(50)     DEFAULT NULL            COMMENT '登录IP',
  `device`      VARCHAR(255)    DEFAULT NULL            COMMENT '登录设备',
  `login_result` TINYINT        NOT NULL DEFAULT 1      COMMENT '结果 1成功 0失败',
  `fail_reason` VARCHAR(255)    DEFAULT NULL            COMMENT '失败原因',
  PRIMARY KEY (`id`),
  KEY `idx_staff_id` (`staff_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工登录日志表';

-- =====================================================================
-- 第二部分：门店与门店设置
-- =====================================================================

-- ---------------------------------------------------------------------
-- 7. store 门店表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '门店ID',
  `store_no`      VARCHAR(20)     NOT NULL                COMMENT '门店编号',
  `store_name`    VARCHAR(100)    NOT NULL                COMMENT '门店名称',
  `province`      VARCHAR(50)     DEFAULT NULL            COMMENT '省份',
  `city`          VARCHAR(50)     DEFAULT NULL            COMMENT '城市',
  `district`      VARCHAR(50)     DEFAULT NULL            COMMENT '区县',
  `address`       VARCHAR(255)    DEFAULT NULL            COMMENT '详细地址',
  `longitude`     DECIMAL(10,6)   DEFAULT NULL            COMMENT '经度',
  `latitude`      DECIMAL(10,6)   DEFAULT NULL            COMMENT '纬度',
  `phone`         VARCHAR(20)     DEFAULT NULL            COMMENT '门店电话',
  `contact_name`  VARCHAR(50)     DEFAULT NULL            COMMENT '负责人',
  `business_status` TINYINT       NOT NULL DEFAULT 1      COMMENT '营业状态 1营业 0打烊',
  `open_time`     TIME            DEFAULT NULL            COMMENT '开始营业时间',
  `close_time`    TIME            DEFAULT NULL            COMMENT '结束营业时间',
  `dine_in_enabled` TINYINT       NOT NULL DEFAULT 1      COMMENT '是否允许堂食 1是 0否',
  `takeout_enabled` TINYINT       NOT NULL DEFAULT 1      COMMENT '是否允许外卖 1是 0否',
  `delivery_radius` DECIMAL(5,2)  NOT NULL DEFAULT 5.00   COMMENT '配送范围(km)',
  `min_order_amount` DECIMAL(10,2) NOT NULL DEFAULT 20.00 COMMENT '起送价(元)',
  `delivery_fee`  DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '基础配送费(元)',
  `delivery_free_threshold` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '免配送费门槛(元)',
  `delivery_start_time` TIME      DEFAULT NULL            COMMENT '配送开始时间',
  `delivery_end_time`   TIME      DEFAULT NULL            COMMENT '配送结束时间',
  `logo`          VARCHAR(255)    DEFAULT NULL            COMMENT '门店Logo',
  `description`   VARCHAR(500)    DEFAULT NULL            COMMENT '门店简介',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_no` (`store_no`),
  KEY `idx_city` (`city`),
  KEY `idx_business_status` (`business_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='门店表';

-- ---------------------------------------------------------------------
-- 8. store_payment_setting 门店支付设置表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `store_payment_setting`;
CREATE TABLE `store_payment_setting` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `store_id`   BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `pay_type`   VARCHAR(20)     NOT NULL                COMMENT '支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金',
  `is_enabled` TINYINT         NOT NULL DEFAULT 1      COMMENT '是否启用 1启用 0停用',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_paytype` (`store_id`,`pay_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='门店支付设置表';

-- =====================================================================
-- 第三部分：客户 / 会员
-- =====================================================================

-- ---------------------------------------------------------------------
-- 9. member_category 会员类别表（全门店通用，由总店长维护）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_category`;
CREATE TABLE `member_category` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会员类别ID',
  `category_name` VARCHAR(50)     NOT NULL                COMMENT '类别名称',
  `discount_rate` DECIMAL(3,2)    NOT NULL DEFAULT 1.00   COMMENT '折扣率(0.90=9折)',
  `recharge_rule` VARCHAR(255)    DEFAULT NULL            COMMENT '充值规则说明',
  `description`   VARCHAR(255)    DEFAULT NULL            COMMENT '类别描述',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员类别表';

-- ---------------------------------------------------------------------
-- 10. member 客户/会员表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  `openid`            VARCHAR(64)     DEFAULT NULL            COMMENT '微信openid',
  `unionid`           VARCHAR(64)     DEFAULT NULL            COMMENT '微信unionid',
  `nickname`          VARCHAR(100)    DEFAULT NULL            COMMENT '昵称',
  `avatar`            VARCHAR(255)    DEFAULT NULL            COMMENT '头像URL',
  `gender`            TINYINT         NOT NULL DEFAULT 0      COMMENT '性别 0未知 1男 2女',
  `phone`             VARCHAR(20)     DEFAULT NULL            COMMENT '绑定手机号',
  `real_name`         VARCHAR(50)     DEFAULT NULL            COMMENT '真实姓名',
  `id_card`           VARCHAR(20)     DEFAULT NULL            COMMENT '身份证号（钱包实名）',
  `member_no`         VARCHAR(32)     DEFAULT NULL            COMMENT '会员卡号',
  `member_category_id` BIGINT UNSIGNED DEFAULT NULL           COMMENT '会员类别ID',
  `balance`           DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '钱包余额(元)',
  `total_points`      INT             NOT NULL DEFAULT 0      COMMENT '累计积分',
  `available_points`  INT             NOT NULL DEFAULT 0      COMMENT '可用积分',
  `pay_password`      VARCHAR(255)    DEFAULT NULL            COMMENT '支付密码（bcrypt加密）',
  `register_store_id` BIGINT UNSIGNED DEFAULT NULL            COMMENT '注册门店ID',
  `status`            TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1正常 0冻结',
  `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`        TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_member_no` (`member_no`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_member_category` (`member_category_id`),
  KEY `idx_register_store` (`register_store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户/会员表';

-- ---------------------------------------------------------------------
-- 11. member_address 收货地址表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_address`;
CREATE TABLE `member_address` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `member_id`     BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `contact_name`  VARCHAR(50)     NOT NULL                COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20)     NOT NULL                COMMENT '联系人电话',
  `province`      VARCHAR(50)     DEFAULT NULL            COMMENT '省份',
  `city`          VARCHAR(50)     DEFAULT NULL            COMMENT '城市',
  `district`      VARCHAR(50)     DEFAULT NULL            COMMENT '区县',
  `detail_address` VARCHAR(255)   NOT NULL                COMMENT '详细地址',
  `longitude`     DECIMAL(10,6)   DEFAULT NULL            COMMENT '经度',
  `latitude`      DECIMAL(10,6)   DEFAULT NULL            COMMENT '纬度',
  `tag`           VARCHAR(20)     DEFAULT NULL            COMMENT '地址标签 家/公司',
  `is_default`    TINYINT         NOT NULL DEFAULT 0      COMMENT '是否默认 1是 0否',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户收货地址表';

-- ---------------------------------------------------------------------
-- 12. member_bank_card 会员银行卡表（钱包实名认证用）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_bank_card`;
CREATE TABLE `member_bank_card` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '银行卡ID',
  `member_id`   BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `bank_name`   VARCHAR(50)     DEFAULT NULL            COMMENT '开户行',
  `card_no`     VARCHAR(32)     NOT NULL                COMMENT '银行卡号',
  `holder_name` VARCHAR(50)     DEFAULT NULL            COMMENT '持卡人姓名',
  `is_default`  TINYINT         NOT NULL DEFAULT 0      COMMENT '是否默认 1是 0否',
  `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`  TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员银行卡表';

-- ---------------------------------------------------------------------
-- 13. member_balance_record 钱包交易记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_balance_record`;
CREATE TABLE `member_balance_record` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `member_id`    BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `change_amount` DECIMAL(10,2)  NOT NULL                COMMENT '变动金额（正=收入 负=支出）',
  `balance_after` DECIMAL(10,2)  NOT NULL                COMMENT '变动后余额',
  `change_type`  VARCHAR(30)     NOT NULL                COMMENT '变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付',
  `source_type`  VARCHAR(30)     DEFAULT NULL            COMMENT '来源类型 ORDER订单 RECHARGE充值单等',
  `source_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '来源ID',
  `remark`       VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_source` (`source_type`,`source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员钱包交易记录表';

-- ---------------------------------------------------------------------
-- 14. member_points_record 积分记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_points_record`;
CREATE TABLE `member_points_record` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `member_id`     BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `points_change` INT             NOT NULL                COMMENT '积分变动（正=增加 负=扣减）',
  `points_after`  INT             NOT NULL                COMMENT '变动后积分',
  `change_type`   VARCHAR(30)     NOT NULL                COMMENT '变动类型 CONSUME消费获得 EXCHANGE积分兑换 REFUND退款扣回 ACTIVITY活动赠送',
  `source_type`   VARCHAR(30)     DEFAULT NULL            COMMENT '来源类型',
  `source_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '来源ID',
  `remark`        VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_change_type` (`change_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员积分记录表';

-- ---------------------------------------------------------------------
-- 15. member_recharge_record 会员充值记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_recharge_record`;
CREATE TABLE `member_recharge_record` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '充值记录ID',
  `recharge_no`     VARCHAR(32)     NOT NULL                COMMENT '充值单号',
  `member_id`       BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `store_id`        BIGINT UNSIGNED DEFAULT NULL            COMMENT '充值门店ID',
  `recharge_amount` DECIMAL(10,2)   NOT NULL                COMMENT '充值金额(元)',
  `gift_amount`     DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '赠送金额(元)',
  `pay_type`        VARCHAR(20)     DEFAULT NULL            COMMENT '支付方式 WECHAT/ALIPAY/CASH',
  `status`          TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1成功 0失败',
  `operator_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '操作收银员工ID',
  `recharge_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值时间',
  `remark`          VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recharge_no` (`recharge_no`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员充值记录表';

-- ---------------------------------------------------------------------
-- 16. red_packet 红包表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `red_packet`;
CREATE TABLE `red_packet` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '红包ID',
  `red_packet_no` VARCHAR(32)     NOT NULL                COMMENT '红包编号',
  `member_id`     BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `store_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '所属门店ID',
  `amount`        DECIMAL(10,2)   NOT NULL                COMMENT '红包金额(元)',
  `source`        VARCHAR(30)     DEFAULT NULL            COMMENT '来源 RECHARGE_REWARD充值返利 ACTIVITY活动',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1未使用 2已使用 3已过期',
  `expire_time`   DATETIME        DEFAULT NULL            COMMENT '过期时间',
  `use_time`      DATETIME        DEFAULT NULL            COMMENT '使用时间',
  `order_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '使用订单ID',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_red_packet_no` (`red_packet_no`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员红包表';

-- =====================================================================
-- 第四部分：菜品与商品
-- =====================================================================

-- ---------------------------------------------------------------------
-- 17. dish_category 菜品分类表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_category`;
CREATE TABLE `dish_category` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `category_name` VARCHAR(50)     NOT NULL                COMMENT '分类名称',
  `sort`          INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品分类表';

-- ---------------------------------------------------------------------
-- 18. dish_taste 口味表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_taste`;
CREATE TABLE `dish_taste` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '口味ID',
  `store_id`    BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `taste_name`  VARCHAR(50)     NOT NULL                COMMENT '口味名称',
  `sort`        INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`      TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`  TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品口味表';

-- ---------------------------------------------------------------------
-- 19. dish_spec 规格表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_spec`;
CREATE TABLE `dish_spec` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `store_id`    BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `spec_name`   VARCHAR(50)     NOT NULL                COMMENT '规格名称',
  `price_delta` DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '加价金额(元)',
  `sort`        INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`      TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`  TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品规格表';

-- ---------------------------------------------------------------------
-- 20. ingredient_category 原料类别表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `ingredient_category`;
CREATE TABLE `ingredient_category` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '原料类别ID',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `category_name` VARCHAR(50)     NOT NULL                COMMENT '类别名称',
  `sort`          INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='原料类别表';

-- ---------------------------------------------------------------------
-- 21. ingredient 原料表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `ingredient`;
CREATE TABLE `ingredient` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '原料ID',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `category_id`   BIGINT UNSIGNED DEFAULT NULL            COMMENT '原料类别ID',
  `ingredient_name` VARCHAR(50)   NOT NULL                COMMENT '原料名称',
  `unit`          VARCHAR(20)     DEFAULT NULL            COMMENT '计量单位',
  `spec`          VARCHAR(50)     DEFAULT NULL            COMMENT '规格说明',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='原料表';

-- ---------------------------------------------------------------------
-- 22. dish 菜品表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `category_id`   BIGINT UNSIGNED NOT NULL                COMMENT '菜品分类ID',
  `dish_name`     VARCHAR(100)    NOT NULL                COMMENT '菜品名称',
  `dish_no`       VARCHAR(20)     DEFAULT NULL            COMMENT '菜品编号',
  `image`         VARCHAR(255)    DEFAULT NULL            COMMENT '菜品图片URL',
  `description`   VARCHAR(500)    DEFAULT NULL            COMMENT '菜品描述',
  `price`         DECIMAL(10,2)   NOT NULL                COMMENT '单价(元)',
  `origin_price`  DECIMAL(10,2)   DEFAULT NULL            COMMENT '划线原价(元)，用于展示促销',
  `unit`          VARCHAR(20)     DEFAULT '份'            COMMENT '售卖单位(份/杯/例等)',
  `taste_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '默认口味ID',
  `spec_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '默认规格ID',
  `is_recommend`  TINYINT         NOT NULL DEFAULT 0      COMMENT '是否推荐 1是 0否',
  `is_new`        TINYINT         NOT NULL DEFAULT 0      COMMENT '是否新品 1是 0否',
  `is_sold_out`   TINYINT         NOT NULL DEFAULT 0      COMMENT '是否售罄 1是 0否',
  `sale_count`    INT             NOT NULL DEFAULT 0      COMMENT '累计销量（销售排行用）',
  `sort`          INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1上架 0下架',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`    TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_category` (`store_id`,`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_fk_category_id` (`category_id`),
  KEY `idx_fk_taste_id` (`taste_id`),
  KEY `idx_fk_spec_id` (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品表';

-- ---------------------------------------------------------------------
-- 23. dish_ingredient_rel 菜品-原料关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_ingredient_rel`;
CREATE TABLE `dish_ingredient_rel` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `dish_id`       BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `ingredient_id` BIGINT UNSIGNED NOT NULL                COMMENT '原料ID',
  `quantity`      DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '用量',
  `unit`          VARCHAR(20)     DEFAULT NULL            COMMENT '单位',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dish_ingredient` (`dish_id`,`ingredient_id`),
  KEY `idx_ingredient_id` (`ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品-原料关联表';

-- ---------------------------------------------------------------------
-- 24. dish_review 菜品评价表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_review`;
CREATE TABLE `dish_review` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '订单ID',
  `order_detail_id` BIGINT UNSIGNED DEFAULT NULL           COMMENT '订单明细ID',
  `dish_id`        BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `member_id`      BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `store_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '门店ID',
  `rating`         TINYINT         NOT NULL DEFAULT 5      COMMENT '评分 1-5',
  `content`        VARCHAR(500)    DEFAULT NULL            COMMENT '评价内容',
  `images`         VARCHAR(500)    DEFAULT NULL            COMMENT '评价图片(逗号分隔)',
  `is_anonymous`   TINYINT         NOT NULL DEFAULT 0      COMMENT '是否匿名 1是 0否',
  `reply`          VARCHAR(500)    DEFAULT NULL            COMMENT '商家回复',
  `reply_time`     DATETIME        DEFAULT NULL            COMMENT '回复时间',
  `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_dish_id` (`dish_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_store_rating` (`store_id`,`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品评价表';

-- =====================================================================
-- 第五部分：台桌、排队、预约
-- =====================================================================

-- ---------------------------------------------------------------------
-- 25. table_type 桌型表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `table_type`;
CREATE TABLE `table_type` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '桌型ID',
  `store_id`    BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `type_name`   VARCHAR(50)     NOT NULL                COMMENT '桌型名称',
  `max_persons` INT             NOT NULL DEFAULT 4      COMMENT '最大容纳人数',
  `sort`        INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `status`      TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted`  TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='桌型表';

-- ---------------------------------------------------------------------
-- 26. dining_table 台桌表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dining_table`;
CREATE TABLE `dining_table` (
  `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '台桌ID',
  `store_id`           BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `table_type_id`      BIGINT UNSIGNED NOT NULL                COMMENT '桌型ID',
  `table_no`           VARCHAR(20)     NOT NULL                COMMENT '桌号',
  `table_name`         VARCHAR(50)     DEFAULT NULL            COMMENT '桌位名称',
  `qr_code`            VARCHAR(255)    DEFAULT NULL            COMMENT '桌位二维码(扫码点餐)',
  `status`             TINYINT         NOT NULL DEFAULT 1      COMMENT '台桌状态 1空闲 2待点餐 3用餐中 4预结账 5已结账',
  `person_count`       INT             NOT NULL DEFAULT 0      COMMENT '当前用餐人数',
  `enable_reservation` TINYINT         NOT NULL DEFAULT 1      COMMENT '是否可预约 1是 0否',
  `sort`               INT             NOT NULL DEFAULT 0      COMMENT '排序号',
  `create_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`         TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_table_no` (`store_id`,`table_no`),
  KEY `idx_table_type` (`table_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='台桌表';

-- ---------------------------------------------------------------------
-- 27. queue 排队记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `queue`;
CREATE TABLE `queue` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '排队ID',
  `store_id`     BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `queue_no`     VARCHAR(20)     NOT NULL                COMMENT '排队号(如A001)',
  `table_type_id` BIGINT UNSIGNED DEFAULT NULL           COMMENT '期望桌型ID',
  `persons`      INT             NOT NULL DEFAULT 1      COMMENT '就餐人数',
  `member_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '客户ID',
  `phone`        VARCHAR(20)     DEFAULT NULL            COMMENT '联系电话',
  `status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1排队中 2已叫号 3已过号 4已就位 5已取消',
  `call_count`   INT             NOT NULL DEFAULT 0      COMMENT '叫号次数',
  `over_count`   INT             NOT NULL DEFAULT 0      COMMENT '过号次数',
  `call_time`    DATETIME        DEFAULT NULL            COMMENT '最近叫号时间',
  `seated_time`  DATETIME        DEFAULT NULL            COMMENT '就位时间',
  `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '取号时间',
  `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_status` (`store_id`,`status`),
  KEY `idx_store_time` (`store_id`,`create_time`),
  KEY `idx_table_type` (`table_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='排队取号记录表';

-- ---------------------------------------------------------------------
-- 28. reservation 餐桌预约表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `store_id`        BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `table_id`        BIGINT UNSIGNED DEFAULT NULL            COMMENT '台桌ID',
  `table_type_id`   BIGINT UNSIGNED DEFAULT NULL            COMMENT '桌型ID',
  `member_id`       BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `contact_name`    VARCHAR(50)     NOT NULL                COMMENT '联系人姓名',
  `contact_phone`   VARCHAR(20)     NOT NULL                COMMENT '联系电话',
  `reservation_date` DATE           NOT NULL                COMMENT '预约日期',
  `reservation_time` TIME           NOT NULL                COMMENT '预约时间',
  `persons`         INT             NOT NULL DEFAULT 1      COMMENT '就餐人数',
  `remark`          VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  `status`          TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待确认 2预约成功 3已取消 4已完成',
  `confirm_time`    DATETIME        DEFAULT NULL            COMMENT '确认时间',
  `complete_time`   DATETIME        DEFAULT NULL            COMMENT '完成时间',
  `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_date` (`store_id`,`reservation_date`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_table_id` (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='餐桌预约表';

-- =====================================================================
-- 第六部分：订单
-- =====================================================================

-- ---------------------------------------------------------------------
-- 29. cart 购物车表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `member_id`  BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `store_id`   BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `dish_id`    BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `dish_name`  VARCHAR(100)    DEFAULT NULL            COMMENT '菜品名称快照',
  `dish_image` VARCHAR(255)    DEFAULT NULL            COMMENT '菜品图片快照',
  `spec_name`  VARCHAR(50)     DEFAULT NULL            COMMENT '规格快照',
  `taste_name` VARCHAR(50)     DEFAULT NULL            COMMENT '口味快照',
  `price`      DECIMAL(10,2)   NOT NULL                COMMENT '加入时单价',
  `quantity`   INT             NOT NULL DEFAULT 1      COMMENT '数量',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_dish` (`member_id`,`store_id`,`dish_id`,`spec_name`,`taste_name`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车表';

-- ---------------------------------------------------------------------
-- 30. orders 订单主表（order为MySQL保留字，故用orders）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`         VARCHAR(32)     NOT NULL                COMMENT '订单号',
  `store_id`         BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `member_id`        BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `order_type`       TINYINT         NOT NULL                COMMENT '订单类型 1堂食 2外卖 3自取',
  `table_id`         BIGINT UNSIGNED DEFAULT NULL            COMMENT '台桌ID(堂食)',
  `table_type_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '桌型ID(堂食)',
  `queue_id`         BIGINT UNSIGNED DEFAULT NULL            COMMENT '排队ID',
  `person_count`     INT             NOT NULL DEFAULT 1      COMMENT '用餐人数',
  `order_status`     TINYINT         NOT NULL DEFAULT 1      COMMENT '订单状态 1待支付 2待制作 3制作中 4待配送 5配送中 6待自取 7已完成 8已取消 9已退款',
  `pay_status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '支付状态 1待支付 2已支付 3已退款 4部分退款',
  `take_type`        TINYINT         DEFAULT NULL            COMMENT '外卖取餐方式 1外送 2自取',
  `address_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '收货地址ID',
  `address_snapshot` VARCHAR(1000)   DEFAULT NULL            COMMENT '地址快照(下单时保存)',
  `dish_amount`      DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '菜品原价合计',
  `discount_amount`  DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '优惠/折扣金额',
  `free_amount`      DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '免单金额',
  `rounding_amount`  DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '抹零金额',
  `coupon_id`        BIGINT UNSIGNED DEFAULT NULL            COMMENT '使用的优惠券ID',
  `coupon_amount`    DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '优惠券抵扣金额',
  `delivery_fee`     DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '配送费',
  `payable_amount`   DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '应付金额',
  `actual_amount`    DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '实付金额',
  `member_pay_amount` DECIMAL(10,2)  NOT NULL DEFAULT 0.00   COMMENT '会员余额支付金额',
  `source`           TINYINT         NOT NULL DEFAULT 1      COMMENT '订单来源 1客户端 2服务员 3收银',
  `operator_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '操作员工ID',
  `remark`           VARCHAR(255)    DEFAULT NULL            COMMENT '订单备注',
  `order_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time`         DATETIME        DEFAULT NULL            COMMENT '支付时间',
  `expected_time`    DATETIME        DEFAULT NULL            COMMENT '预计送达/自取时间',
  `shipping_time`    DATETIME        DEFAULT NULL            COMMENT '配送/出餐时间',
  `version`          INT             NOT NULL DEFAULT 0      COMMENT '乐观锁版本号（防止状态并发覆盖）',
  `finish_time`      DATETIME        DEFAULT NULL            COMMENT '完成时间',
  `cancel_time`      DATETIME        DEFAULT NULL            COMMENT '取消时间',
  `cancel_reason`    VARCHAR(255)    DEFAULT NULL            COMMENT '取消原因',
  `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`       TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_store_status` (`store_id`,`order_status`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_member_time` (`member_id`,`order_time`),
  KEY `idx_order_time` (`order_time`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单主表（coupon_id为逻辑外键，关联coupon.id）';

-- ---------------------------------------------------------------------
-- 31. order_detail 订单明细表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`   BIGINT UNSIGNED NOT NULL                COMMENT '订单ID',
  `dish_id`    BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `dish_name`  VARCHAR(100)    NOT NULL                COMMENT '菜品名称快照',
  `dish_image` VARCHAR(255)    DEFAULT NULL            COMMENT '菜品图片快照',
  `dish_price` DECIMAL(10,2)   NOT NULL                COMMENT '成交单价(元)',
  `spec_name`  VARCHAR(50)     DEFAULT NULL            COMMENT '规格快照',
  `taste_name` VARCHAR(50)     DEFAULT NULL            COMMENT '口味快照',
  `quantity`   INT             NOT NULL DEFAULT 1      COMMENT '数量',
  `subtotal`   DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '小计金额',
  `refund_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT '已退金额(元)，退菜时累加',
  `status`     TINYINT         NOT NULL DEFAULT 1      COMMENT '制作状态 1待制作 2制作中 3已上齐 4退菜',
  `remark`     VARCHAR(255)    DEFAULT NULL            COMMENT '备注',
  `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_dish_id` (`dish_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';

-- ---------------------------------------------------------------------
-- 32. order_status_log 订单状态日志表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `order_status_log`;
CREATE TABLE `order_status_log` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id`      BIGINT UNSIGNED NOT NULL                COMMENT '订单ID',
  `from_status`   TINYINT         DEFAULT NULL            COMMENT '原状态',
  `to_status`     TINYINT         NOT NULL                COMMENT '新状态',
  `operator_type` VARCHAR(20)     NOT NULL DEFAULT 'SYSTEM' COMMENT '操作方 CLIENT客户端 STAFF员工 SYSTEM系统',
  `operator_id`   BIGINT UNSIGNED DEFAULT NULL            COMMENT '操作者ID',
  `remark`        VARCHAR(255)    DEFAULT NULL            COMMENT '说明',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单状态日志表';

-- ---------------------------------------------------------------------
-- 33. payment_record 支付记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `pay_no`        VARCHAR(32)     NOT NULL                COMMENT '支付流水号',
  `order_id`      BIGINT UNSIGNED NOT NULL                COMMENT '订单ID',
  `store_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '门店ID',
  `member_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '客户ID',
  `pay_type`      VARCHAR(20)     NOT NULL                COMMENT '支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付',
  `pay_channel`   VARCHAR(50)     DEFAULT NULL            COMMENT '支付渠道说明',
  `amount`        DECIMAL(10,2)   NOT NULL                COMMENT '支付金额(元)',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待支付 2成功 3失败 4已退款',
  `transaction_id` VARCHAR(64)    DEFAULT NULL            COMMENT '第三方交易号',
  `idempotency_key` VARCHAR(64)   DEFAULT NULL            COMMENT '幂等键（防重复支付回调，同一订单同渠道唯一）',
  `payer_account` VARCHAR(100)    DEFAULT NULL            COMMENT '付款账号（脱敏展示）',
  `notify_time`   DATETIME        DEFAULT NULL            COMMENT '支付回调通知时间',
  `pay_time`      DATETIME        DEFAULT NULL            COMMENT '支付时间',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  UNIQUE KEY `uk_idempotency_key` (`idempotency_key`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_pay_time` (`pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录表';

-- ---------------------------------------------------------------------
-- 34. refund 退单记录表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '退单ID',
  `refund_no`      VARCHAR(32)     NOT NULL                COMMENT '退单号',
  `order_id`       BIGINT UNSIGNED NOT NULL                COMMENT '订单ID',
  `order_detail_id` BIGINT UNSIGNED DEFAULT NULL           COMMENT '订单明细ID(退菜时)',
  `store_id`       BIGINT UNSIGNED DEFAULT NULL            COMMENT '门店ID',
  `member_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '客户ID',
  `refund_type`    TINYINT         NOT NULL DEFAULT 1      COMMENT '退单类型 1整单退 2退菜',
  `dish_name`      VARCHAR(100)    DEFAULT NULL            COMMENT '退菜名称',
  `quantity`       INT             DEFAULT NULL            COMMENT '退菜数量',
  `amount`         DECIMAL(10,2)   NOT NULL                COMMENT '退款金额(元)',
  `reason`         VARCHAR(255)    DEFAULT NULL            COMMENT '退单原因',
  `status`         TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待审核 2已通过 3已驳回 4已完成',
  `operator_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '操作员工ID',
  `refund_time`    DATETIME        DEFAULT NULL            COMMENT '退款时间',
  `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_store_time` (`store_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退单记录表';

-- =====================================================================
-- 第七部分：促销（优惠券）
-- =====================================================================

-- ---------------------------------------------------------------------
-- 35. coupon 优惠券表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `store_id`         BIGINT UNSIGNED DEFAULT NULL            COMMENT '门店ID(NULL为通用券)',
  `coupon_name`      VARCHAR(50)     NOT NULL                COMMENT '优惠券名称',
  `coupon_type`      TINYINT         NOT NULL DEFAULT 1      COMMENT '类型 1满减 2折扣 3立减',
  `threshold_amount` DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '使用门槛金额(元)',
  `discount_amount`  DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '减免金额(元)(满减/立减)',
  `discount_rate`    DECIMAL(3,2)    DEFAULT NULL            COMMENT '折扣率(折扣券)',
  `total_count`      INT             NOT NULL DEFAULT 0      COMMENT '发行总量(0不限量)',
  `issued_count`     INT             NOT NULL DEFAULT 0      COMMENT '已发放数量',
  `per_user_limit`   INT             NOT NULL DEFAULT 1      COMMENT '每人限领数量',
  `valid_type`       TINYINT         NOT NULL DEFAULT 1      COMMENT '有效期类型 1固定时间 2领取后N天',
  `valid_start`      DATETIME        DEFAULT NULL            COMMENT '生效开始时间',
  `valid_end`        DATETIME        DEFAULT NULL            COMMENT '生效结束时间',
  `valid_days`       INT             DEFAULT NULL            COMMENT '领取后有效天数',
  `use_scope`        TINYINT         NOT NULL DEFAULT 1      COMMENT '使用范围 1全场 2指定分类',
  `scope_category_id` BIGINT UNSIGNED DEFAULT NULL           COMMENT '适用菜品分类ID（use_scope=2时）',
  `status`           TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1草稿 2已发布 3已结束',
  `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`       TINYINT         NOT NULL DEFAULT 0      COMMENT '逻辑删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='优惠券表';

-- ---------------------------------------------------------------------
-- 36. member_coupon 用户优惠券表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `member_coupon`;
CREATE TABLE `member_coupon` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户券ID',
  `member_id`    BIGINT UNSIGNED NOT NULL                COMMENT '客户ID',
  `coupon_id`    BIGINT UNSIGNED NOT NULL                COMMENT '优惠券ID',
  `store_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '适用门店ID',
  `status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1未使用 2已使用 3已过期',
  `receive_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `expire_time`  DATETIME        DEFAULT NULL            COMMENT '过期时间',
  `use_time`     DATETIME        DEFAULT NULL            COMMENT '使用时间',
  `order_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '使用订单ID',
  `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire` (`status`,`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户优惠券表';

-- =====================================================================
-- 第八部分：库存
-- =====================================================================

-- ---------------------------------------------------------------------
-- 37. dish_stock 菜品库存表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dish_stock`;
CREATE TABLE `dish_stock` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜品库存ID',
  `store_id`       BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `dish_id`        BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `stock_quantity` INT             NOT NULL DEFAULT 0      COMMENT '当前库存数量',
  `warn_threshold` INT             NOT NULL DEFAULT 0      COMMENT '预警阈值',
  `unit`           VARCHAR(20)     DEFAULT NULL            COMMENT '计量单位(份)',
  `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_dish` (`store_id`,`dish_id`),
  KEY `idx_fk_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品库存表';

-- ---------------------------------------------------------------------
-- 38. ingredient_stock 原料库存表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `ingredient_stock`;
CREATE TABLE `ingredient_stock` (
  `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '原料库存ID',
  `store_id`       BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `ingredient_id`  BIGINT UNSIGNED NOT NULL                COMMENT '原料ID',
  `stock_quantity` DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '当前库存数量',
  `warn_threshold` DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '预警阈值',
  `unit`           VARCHAR(20)     DEFAULT NULL            COMMENT '计量单位',
  `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_store_ingredient` (`store_id`,`ingredient_id`),
  KEY `idx_fk_ingredient_id` (`ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='原料库存表';

-- ---------------------------------------------------------------------
-- 39. stock_check_dish 菜品盘点单表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `stock_check_dish`;
CREATE TABLE `stock_check_dish` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '盘点单ID',
  `check_no`      VARCHAR(32)     NOT NULL                COMMENT '盘点单号',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `dish_id`       BIGINT UNSIGNED NOT NULL                COMMENT '菜品ID',
  `stock_before`  INT             NOT NULL DEFAULT 0      COMMENT '盘点前库存',
  `stock_after`   INT             NOT NULL DEFAULT 0      COMMENT '盘点后库存',
  `diff`          INT             NOT NULL DEFAULT 0      COMMENT '差异(盘后-盘前)',
  `reason`        VARCHAR(255)    DEFAULT NULL            COMMENT '差异原因',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待审核 2已通过 3已驳回',
  `creator_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '创建人(店长)',
  `auditor_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '审核人(总店长)',
  `audit_time`    DATETIME        DEFAULT NULL            COMMENT '审核时间',
  `audit_remark`  VARCHAR(255)    DEFAULT NULL            COMMENT '审核意见',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_status` (`status`),
  KEY `idx_fk_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品盘点单表';

-- ---------------------------------------------------------------------
-- 40. stock_check_ingredient 原料盘点单表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `stock_check_ingredient`;
CREATE TABLE `stock_check_ingredient` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '盘点单ID',
  `check_no`      VARCHAR(32)     NOT NULL                COMMENT '盘点单号',
  `store_id`      BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `ingredient_id` BIGINT UNSIGNED NOT NULL                COMMENT '原料ID',
  `stock_before`  DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '盘点前库存',
  `stock_after`   DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '盘点后库存',
  `diff`          DECIMAL(10,2)   NOT NULL DEFAULT 0.00   COMMENT '差异(盘后-盘前)',
  `reason`        VARCHAR(255)    DEFAULT NULL            COMMENT '差异原因',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待审核 2已通过 3已驳回',
  `creator_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '创建人(店长)',
  `auditor_id`    BIGINT UNSIGNED DEFAULT NULL            COMMENT '审核人(总店长)',
  `audit_time`    DATETIME        DEFAULT NULL            COMMENT '审核时间',
  `audit_remark`  VARCHAR(255)    DEFAULT NULL            COMMENT '审核意见',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_store_id` (`store_id`),
  KEY `idx_status` (`status`),
  KEY `idx_fk_ingredient_id` (`ingredient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='原料盘点单表';

-- =====================================================================
-- 第九部分：硬件（打印机、小票模板）与意见反馈
-- =====================================================================

-- ---------------------------------------------------------------------
-- 41. printer 打印机表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `printer`;
CREATE TABLE `printer` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '打印机ID',
  `store_id`     BIGINT UNSIGNED NOT NULL                COMMENT '门店ID',
  `printer_name` VARCHAR(50)     NOT NULL                COMMENT '打印机名称',
  `printer_no`   VARCHAR(50)     DEFAULT NULL            COMMENT '打印机编号',
  `printer_type` VARCHAR(20)     DEFAULT NULL            COMMENT '用途 KITCHEN后厨 RECEIPT前台 TAKEOUT外卖',
  `status`       TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='打印机表';

-- ---------------------------------------------------------------------
-- 42. receipt_template 小票模板表（全国统一，由总店长维护）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `receipt_template`;
CREATE TABLE `receipt_template` (
  `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_type`    VARCHAR(20)     NOT NULL                COMMENT '类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值',
  `template_name`    VARCHAR(50)     NOT NULL                COMMENT '模板名称',
  `template_content` TEXT            DEFAULT NULL            COMMENT '模板内容',
  `is_default`       TINYINT         NOT NULL DEFAULT 0      COMMENT '是否默认 1是 0否',
  `create_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_type` (`template_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='小票模板表';

-- ---------------------------------------------------------------------
-- 43. feedback 意见反馈表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `member_id`     BIGINT UNSIGNED DEFAULT NULL            COMMENT '客户ID',
  `store_id`      BIGINT UNSIGNED DEFAULT NULL            COMMENT '门店ID',
  `content`       VARCHAR(1000)   NOT NULL                COMMENT '反馈内容',
  `images`        VARCHAR(500)    DEFAULT NULL            COMMENT '反馈图片(逗号分隔)',
  `contact_phone` VARCHAR(20)     DEFAULT NULL            COMMENT '联系电话',
  `status`        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态 1待处理 2已处理',
  `reply`         VARCHAR(500)    DEFAULT NULL            COMMENT '处理回复',
  `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='意见反馈表';

-- =====================================================================
-- 第二部分：初始化数据
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. 角色初始化
-- ---------------------------------------------------------------------
INSERT INTO `sys_role` (`id`,`role_name`,`role_code`,`level`,`description`) VALUES
(1,'总店长','GENERAL_MANAGER',99,'管理全国所有门店'),
(2,'店长','STORE_MANAGER',50,'管理本门店日常运营'),
(3,'服务员','WAITER',10,'为客户点餐、开台、叫号'),
(4,'收银','CASHIER',10,'收银结账、会员充值'),
(5,'后厨','KITCHEN',10,'制作菜品、查看后厨订单');

-- ---------------------------------------------------------------------
-- 2. 权限（商户端菜单权限）初始化
-- ---------------------------------------------------------------------
INSERT INTO `sys_permission` (`id`,`permission_name`,`permission_code`,`menu_url`,`parent_id`,`sort`) VALUES
(1,'门店管理','store:list','/store/list',0,10),
(2,'员工管理','staff:manage','/staff/manage',0,20),
(3,'硬件管理','hardware:manage','/hardware/manage',0,30),
(4,'台桌管理','table:manage','/table/manage',0,40),
(5,'菜品管理','dish:manage','/dish/manage',0,50),
(6,'堂食设置','dinein:setting','/dinein/setting',0,60),
(7,'外卖设置','takeout:setting','/takeout/setting',0,70),
(8,'库存管理','stock:manage','/stock/manage',0,80),
(9,'库存盘点','stock:check','/stock/check',0,90),
(10,'订单管理','order:manage','/order/manage',0,100),
(11,'会员管理','member:manage','/member/manage',0,110),
(12,'会员类别','member:category','/member/category',0,120),
(13,'促销管理','promotion:manage','/promotion/manage',0,130),
(14,'报表管理','report:manage','/report/manage',0,140),
(15,'支付设置','payment:setting','/payment/setting',0,150),
(16,'账号设置','account:setting','/account/setting',0,160);

-- 角色-权限：总店长全部，店长除门店管理/会员类别/小票模板等总店专属外的权限
INSERT INTO `sys_role_permission` (`role_id`,`permission_id`) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),
(2,2),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,13),(2,14),(2,15),(2,16);

-- ---------------------------------------------------------------------
-- 3. 门店初始化
-- ---------------------------------------------------------------------
INSERT INTO `store`
(`id`,`store_no`,`store_name`,`province`,`city`,`district`,`address`,`longitude`,`latitude`,`phone`,`contact_name`,`business_status`,`open_time`,`close_time`,`dine_in_enabled`,`takeout_enabled`,`delivery_radius`,`min_order_amount`,`delivery_fee`,`delivery_free_threshold`,`delivery_start_time`,`delivery_end_time`,`description`) VALUES
(1,'S001','智慧餐厅·成都春熙路旗舰店','四川省','成都市','锦江区','锦江区红星路三段99号',104.081949,30.657689,'028-88888801','张店长',1,'10:00:00','22:00:00',1,1,5.00,20.00,3.00,50.00,'10:00:00','21:30:00','春熙路商圈人气川菜馆'),
(2,'S002','智慧餐厅·成都天府广场店','四川省','成都市','青羊区','青羊区人民中路一段天府广场',104.065735,30.657542,'028-88888802','李店长',1,'10:00:00','21:30:00',1,1,4.00,20.00,3.00,50.00,'10:00:00','21:00:00','天府广场核心商圈店'),
(3,'S003','智慧餐厅·北京朝阳大悦城店','北京市','北京市','朝阳区','朝阳区朝阳北路101号大悦城',116.521488,39.924507,'010-66666601','王店长',0,'10:30:00','21:30:00',1,1,5.00,30.00,5.00,80.00,'10:30:00','21:00:00','北京朝阳大悦城店');

-- ---------------------------------------------------------------------
-- 4. 员工账号初始化（密码统一为123456的MD5，生产环境默认密码=身份证后4位+手机号后3位）
-- ---------------------------------------------------------------------
INSERT INTO `sys_user`
(`id`,`store_id`,`username`,`password`,`real_name`,`staff_no`,`email`,`phone`,`id_card`,`role_id`,`status`)
VALUES
(1,NULL,'13800000001','e10adc3949ba59abbe56e057f20f883e','张总','GM001','boss@smartrest.com','13800000001','510101197001011234',1,1),
(2,1,'13800000002','e10adc3949ba59abbe56e057f20f883e','张店长','SM001','sm001@smartrest.com','13800000002','510101198501014321',2,1),
(3,2,'13800000003','e10adc3949ba59abbe56e057f20f883e','李店长','SM002','sm002@smartrest.com','13800000003','510101198601013210',2,1),
(4,3,'13800000004','e10adc3949ba59abbe56e057f20f883e','王店长','SM003','sm003@smartrest.com','13800000004','110101198701011234',2,1),
(5,1,'13800000011','e10adc3949ba59abbe56e057f20f883e','小李','WA001',NULL,'13800000011','510101199001014567',3,1),
(6,1,'13800000012','e10adc3949ba59abbe56e057f20f883e','小周','WA002',NULL,'13800000012','510101199101016789',3,1),
(7,1,'13800000021','e10adc3949ba59abbe56e057f20f883e','小赵','CA001',NULL,'13800000021','510101199201017890',4,1),
(8,1,'13800000031','e10adc3949ba59abbe56e057f20f883e','老陈','KT001',NULL,'13800000031','510101198901019012',5,1),
(9,1,'13800000032','e10adc3949ba59abbe56e057f20f883e','老刘','KT002',NULL,'13800000032','510101199001010101',5,1);

-- 员工排班示例
INSERT INTO `staff_schedule` (`staff_id`,`work_date`,`shift_type`,`start_time`,`end_time`) VALUES
(5,'2026-09-03',1,'08:00:00','16:00:00'),
(6,'2026-09-03',2,'12:00:00','20:00:00'),
(7,'2026-09-03',1,'08:00:00','16:00:00'),
(8,'2026-09-03',2,'12:00:00','21:00:00');

-- ---------------------------------------------------------------------
-- 5. 门店支付设置初始化
-- ---------------------------------------------------------------------
INSERT INTO `store_payment_setting` (`store_id`,`pay_type`,`is_enabled`) VALUES
(1,'WECHAT',1),(1,'ALIPAY',1),(1,'MEMBER',1),(1,'CASH',1),
(2,'WECHAT',1),(2,'ALIPAY',1),(2,'MEMBER',1),(2,'CASH',0),
(3,'WECHAT',1),(3,'ALIPAY',1),(3,'MEMBER',0),(3,'CASH',0);

-- ---------------------------------------------------------------------
-- 6. 会员类别初始化（全门店通用）
-- ---------------------------------------------------------------------
INSERT INTO `member_category` (`id`,`category_name`,`discount_rate`,`recharge_rule`,`description`) VALUES
(1,'普通会员',1.00,'无','注册即享，无折扣'),
(2,'银卡会员',0.95,'充500送30','充值500元赠送30元，享95折'),
(3,'金卡会员',0.90,'充1000送100','充值1000元赠送100元，享9折');

-- ---------------------------------------------------------------------
-- 7. 客户/会员初始化
-- ---------------------------------------------------------------------
INSERT INTO `member`
(`id`,`openid`,`nickname`,`gender`,`phone`,`real_name`,`member_no`,`member_category_id`,`balance`,`total_points`,`available_points`,`register_store_id`,`status`)
VALUES
(1,'wx_openid_001','张小明',1,'13811110001','张小明','M20260001',1,200.00,150,150,1,1),
(2,'wx_openid_002','李小红',2,'13811110002','李小红','M20260002',2,100.00,500,500,1,1),
(3,'wx_openid_003','王大力',1,'13811110003','王大力','M20260003',3,500.00,1200,1200,1,1),
(4,'wx_openid_004','陈静',2,'13811110004','陈静','M20260004',1,50.00,80,80,2,1),
(5,'wx_openid_005','刘洋',1,'13811110005','刘洋','M20260005',2,0.00,300,300,3,1);

-- 收货地址
INSERT INTO `member_address`
(`id`,`member_id`,`contact_name`,`contact_phone`,`province`,`city`,`district`,`detail_address`,`longitude`,`latitude`,`tag`,`is_default`) VALUES
(1,1,'张小明','13811110001','四川省','成都市','锦江区','红星路一段2号院3栋1单元',104.081949,30.657689,'家',1),
(2,1,'张小明','13811110001','四川省','成都市','锦江区','IFS国际金融中心2号楼25楼',104.083630,30.655520,'公司',0),
(3,2,'李小红','13811110002','四川省','成都市','锦江区','春熙路步行街某公寓1203',104.082350,30.659210,'家',1);

-- 会员银行卡
INSERT INTO `member_bank_card` (`member_id`,`bank_name`,`card_no`,`holder_name`,`is_default`) VALUES
(3,'中国工商银行','6222020200001234567','王大力',1);

-- ---------------------------------------------------------------------
-- 8. 菜品基础资料初始化（以成都春熙路旗舰店 store_id=1 为例）
-- ---------------------------------------------------------------------
-- 菜品分类
INSERT INTO `dish_category` (`id`,`store_id`,`category_name`,`sort`) VALUES
(1,1,'招牌热菜',1),(2,1,'家常小炒',2),(3,1,'凉菜',3),(4,1,'汤类',4),(5,1,'主食',5),(6,1,'饮品',6);

-- 口味
INSERT INTO `dish_taste` (`id`,`store_id`,`taste_name`,`sort`) VALUES
(1,1,'不辣',1),(2,1,'微辣',2),(3,1,'中辣',3),(4,1,'特辣',4);

-- 规格
INSERT INTO `dish_spec` (`id`,`store_id`,`spec_name`,`price_delta`,`sort`) VALUES
(1,1,'标准份',0.00,1),(2,1,'大份',8.00,2),(3,1,'小份',-5.00,3);

-- 原料类别
INSERT INTO `ingredient_category` (`id`,`store_id`,`category_name`,`sort`) VALUES
(1,1,'蔬菜类',1),(2,1,'肉类',2),(3,1,'水产类',3),(4,1,'调料类',4),(5,1,'主食类',5);

-- 原料
INSERT INTO `ingredient` (`id`,`store_id`,`category_id`,`ingredient_name`,`unit`,`spec`) VALUES
(1,1,1,'土豆','kg','袋装'),
(2,1,1,'黄瓜','kg','散装'),
(3,1,1,'番茄','kg','散装'),
(4,1,1,'青椒','kg','散装'),
(5,1,2,'猪里脊肉','kg','新鲜'),
(6,1,2,'鸡胸肉','kg','新鲜'),
(7,1,2,'牛肉','kg','新鲜'),
(8,1,3,'草鱼','kg','鲜活'),
(9,1,4,'豆瓣酱','瓶','500g'),
(10,1,4,'干辣椒','kg','散装'),
(11,1,5,'大米','kg','袋装'),
(12,1,5,'面条','kg','袋装');

-- 菜品
INSERT INTO `dish`
(`id`,`store_id`,`category_id`,`dish_name`,`dish_no`,`image`,`description`,`price`,`taste_id`,`spec_id`,`is_recommend`,`is_new`,`is_sold_out`,`sale_count`,`sort`,`status`) VALUES
(1,1,1,'麻辣香锅','D001',NULL,'招牌麻辣香锅，食材丰富，麻辣鲜香',58.00,3,1,1,0,0,1250,1,1),
(2,1,1,'水煮鱼','D002',NULL,'选用鲜活草鱼，麻辣爽口',68.00,3,1,1,0,0,980,2,1),
(3,1,1,'宫保鸡丁','D003',NULL,'经典川菜，鸡丁滑嫩，花生酥脆',32.00,2,1,1,0,0,1100,3,1),
(4,1,2,'鱼香肉丝','D004',NULL,'鱼香浓郁，下饭首选',28.00,2,1,0,0,0,1500,1,1),
(5,1,2,'麻婆豆腐','D005',NULL,'麻辣鲜嫩，经典家常',22.00,3,1,0,0,0,1700,2,1),
(6,1,2,'酸辣土豆丝','D006',NULL,'酸辣开胃，爽脆可口',16.00,2,1,0,0,0,1900,3,1),
(7,1,2,'青椒肉丝','D007',NULL,'家常小炒，营养均衡',26.00,1,1,0,0,0,800,4,1),
(8,1,3,'拍黄瓜','D008',NULL,'清爽开胃凉菜',12.00,1,1,0,0,0,900,1,1),
(9,1,3,'夫妻肺片','D009',NULL,'麻辣卤味，风味独特',32.00,3,1,0,0,0,760,2,1),
(10,1,4,'番茄蛋汤','D010',NULL,'酸甜可口，营养丰富',15.00,1,1,0,0,0,1100,1,1),
(11,1,4,'紫菜蛋花汤','D011',NULL,'清淡鲜美',12.00,1,1,0,0,0,600,2,1),
(12,1,5,'扬州炒饭','D012',NULL,'粒粒分明，配料丰富',18.00,1,1,0,0,0,1300,1,1),
(13,1,5,'米饭','D013',NULL,'东北大米，粒粒饱满',3.00,1,1,0,0,0,8000,2,1),
(14,1,6,'可乐','D014',NULL,'冰镇可乐330ml',6.00,1,1,0,0,0,2000,1,1),
(15,1,6,'酸梅汤','D015',NULL,'自制酸梅汤，解腻消暑',10.00,1,1,0,1,0,1500,2,1);

-- 菜品-原料关联示例
INSERT INTO `dish_ingredient_rel` (`dish_id`,`ingredient_id`,`quantity`,`unit`) VALUES
(1,6,0.50,'kg'),(1,10,0.10,'kg'),(1,4,0.20,'kg'),
(2,8,0.80,'kg'),(2,10,0.10,'kg'),
(3,6,0.30,'kg'),(3,4,0.10,'kg'),
(5,5,0.15,'kg'),(5,9,0.05,'瓶'),
(6,1,0.30,'kg'),(6,10,0.03,'kg'),
(13,11,0.20,'kg');

-- ---------------------------------------------------------------------
-- 9. 台桌/桌型初始化
-- ---------------------------------------------------------------------
-- 桌型
INSERT INTO `table_type` (`id`,`store_id`,`type_name`,`max_persons`,`sort`) VALUES
(1,1,'2人桌',2,1),(2,1,'4人桌',4,2),(3,1,'6人桌',6,3),(4,1,'包间',10,4),
(5,2,'2人桌',2,1),(6,2,'4人桌',4,2),(7,2,'6人桌',6,3);

-- 台桌
INSERT INTO `dining_table` (`id`,`store_id`,`table_type_id`,`table_no`,`table_name`,`status`,`enable_reservation`,`sort`) VALUES
(1,1,1,'A01','A01-2人桌',1,1,1),
(2,1,1,'A02','A02-2人桌',1,1,2),
(3,1,1,'A03','A03-2人桌',1,1,3),
(4,1,2,'A04','A04-4人桌',1,1,4),
(5,1,2,'A05','A05-4人桌',1,1,5),
(6,1,2,'A06','A06-4人桌',1,1,6),
(7,1,3,'B01','B01-6人桌',1,1,7),
(8,1,3,'B02','B02-6人桌',1,1,8),
(9,1,4,'B03','B03-包间',1,1,9),
(10,2,6,'C01','C01-4人桌',1,1,1),
(11,2,6,'C02','C02-4人桌',1,1,2);

-- ---------------------------------------------------------------------
-- 10. 优惠券初始化
-- ---------------------------------------------------------------------
INSERT INTO `coupon`
(`id`,`store_id`,`coupon_name`,`coupon_type`,`threshold_amount`,`discount_amount`,`discount_rate`,`total_count`,`issued_count`,`per_user_limit`,`valid_type`,`valid_start`,`valid_end`,`valid_days`,`status`) VALUES
(1,1,'满30减5券',1,30.00,5.00,NULL,1000,0,1,1,'2026-09-01 00:00:00','2026-12-31 23:59:59',NULL,2),
(2,1,'满50减10券',1,50.00,10.00,NULL,800,0,1,1,'2026-09-01 00:00:00','2026-12-31 23:59:59',NULL,2),
(3,NULL,'新客立减5元券',3,0.00,5.00,NULL,2000,0,1,2,NULL,NULL,7,2),
(4,NULL,'全场满100减20券',1,100.00,20.00,NULL,500,0,1,1,'2026-09-01 00:00:00','2026-12-31 23:59:59',NULL,2);

-- 用户已领取优惠券示例
INSERT INTO `member_coupon` (`member_id`,`coupon_id`,`store_id`,`status`,`receive_time`,`expire_time`) VALUES
(1,1,1,1,'2026-09-02 10:00:00','2026-12-31 23:59:59'),
(1,3,NULL,1,'2026-09-01 12:00:00','2026-09-08 12:00:00'),
(2,2,1,1,'2026-09-02 15:00:00','2026-12-31 23:59:59');

-- ---------------------------------------------------------------------
-- 11. 库存初始化
-- ---------------------------------------------------------------------
-- 菜品库存（仅部分菜品维护库存）
INSERT INTO `dish_stock` (`store_id`,`dish_id`,`stock_quantity`,`warn_threshold`,`unit`) VALUES
(1,1,50,10,'份'),(1,2,40,10,'份'),(1,3,60,15,'份'),(1,6,80,20,'份'),(1,12,70,15,'份'),(1,14,120,30,'份');

-- 原料库存
INSERT INTO `ingredient_stock` (`store_id`,`ingredient_id`,`stock_quantity`,`warn_threshold`,`unit`) VALUES
(1,1,30.00,10.00,'kg'),(1,2,20.00,5.00,'kg'),(1,3,25.00,8.00,'kg'),
(1,4,18.00,5.00,'kg'),(1,5,22.00,8.00,'kg'),(1,6,26.00,8.00,'kg'),
(1,7,15.00,5.00,'kg'),(1,8,18.00,5.00,'kg'),(1,9,12.00,3.00,'瓶'),
(1,10,9.00,3.00,'kg'),(1,11,100.00,30.00,'kg'),(1,12,45.00,15.00,'kg');

-- 盘点单示例
INSERT INTO `stock_check_dish` (`check_no`,`store_id`,`dish_id`,`stock_before`,`stock_after`,`diff`,`reason`,`status`,`creator_id`) VALUES
('PD20260903001',1,1,48,50,2,'采购补货',2,2),
('PD20260903002',1,6,85,80,-5,'盘点损耗',1,2);

INSERT INTO `stock_check_ingredient` (`check_no`,`store_id`,`ingredient_id`,`stock_before`,`stock_after`,`diff`,`reason`,`status`,`creator_id`) VALUES
('PY20260903001',1,5,20.00,22.00,2.00,'采购入库',2,2);

-- ---------------------------------------------------------------------
-- 12. 打印机与小票模板初始化
-- ---------------------------------------------------------------------
INSERT INTO `printer` (`store_id`,`printer_name`,`printer_no`,`printer_type`,`status`) VALUES
(1,'后厨打印机','PRINT-K-001','KITCHEN',1),
(1,'前台小票打印机','PRINT-R-001','RECEIPT',1);

INSERT INTO `receipt_template` (`template_type`,`template_name`,`template_content`,`is_default`) VALUES
('DINE_IN','堂食小票模板','【堂食小票】\n门店：{storeName}\n桌号：{tableNo}\n订单号：{orderNo}\n时间：{orderTime}\n----------\n{items}\n----------\n合计：{total}\n支付方式：{payType}',1),
('TAKEOUT','外卖小票模板','【外卖小票】\n门店：{storeName}\n订单号：{orderNo}\n配送方式：{takeType}\n地址：{address}\n电话：{phone}\n----------\n{items}\n----------\n合计：{total}',1),
('KITCHEN','后厨小票模板','【后厨制作单】\n桌号/单号：{tableNo}\n----------\n{items}\n下单时间：{orderTime}',1),
('RECHARGE','充值小票模板','【会员充值单】\n会员：{memberNo}\n充值金额：{amount}\n赠送金额：{gift}\n收银员：{operator}\n时间：{time}',1);

-- ---------------------------------------------------------------------
-- 13. 示例订单（演示订单主从表、支付、积分、评价）
-- ---------------------------------------------------------------------
-- 订单1：堂食已完成
INSERT INTO `orders`
(`id`,`order_no`,`store_id`,`member_id`,`order_type`,`table_id`,`table_type_id`,`person_count`,`order_status`,`pay_status`,`dish_amount`,`discount_amount`,`free_amount`,`rounding_amount`,`coupon_id`,`coupon_amount`,`delivery_fee`,`payable_amount`,`actual_amount`,`member_pay_amount`,`source`,`operator_id`,`remark`,`order_time`,`pay_time`,`finish_time`) VALUES
(1,'SO20260903000001',1,1,1,4,2,2,7,2,86.00,5.00,0.00,1.00,1,5.00,0.00,80.00,80.00,0.00,2,7,NULL,'2026-09-03 11:30:00','2026-09-03 11:32:00','2026-09-03 12:15:00');

INSERT INTO `order_detail` (`id`,`order_id`,`dish_id`,`dish_name`,`dish_price`,`spec_name`,`taste_name`,`quantity`,`subtotal`,`status`) VALUES
(1,1,3,'宫保鸡丁',32.00,'标准份','微辣',1,32.00,3),
(2,1,5,'麻婆豆腐',22.00,'标准份','中辣',1,22.00,3),
(3,1,12,'扬州炒饭',18.00,'标准份',NULL,1,18.00,3),
(4,1,14,'可乐',6.00,NULL,NULL,2,12.00,3);

INSERT INTO `payment_record` (`pay_no`,`order_id`,`store_id`,`member_id`,`pay_type`,`pay_channel`,`amount`,`status`,`transaction_id`,`idempotency_key`,`notify_time`,`pay_time`) VALUES
('PAY20260903000001',1,1,1,'WECHAT','微信支付',80.00,2,'wx_tx_202609030001','IDEMP-20260903-0001','2026-09-03 11:32:01','2026-09-03 11:32:00');

INSERT INTO `order_status_log` (`order_id`,`from_status`,`to_status`,`operator_type`,`operator_id`,`remark`) VALUES
(1,NULL,2,'STAFF',7,'下单支付'),
(1,2,7,'SYSTEM',NULL,'订单完成');

-- 订单2：外卖待配送
INSERT INTO `orders`
(`id`,`order_no`,`store_id`,`member_id`,`order_type`,`take_type`,`address_id`,`address_snapshot`,`person_count`,`order_status`,`pay_status`,`dish_amount`,`discount_amount`,`free_amount`,`rounding_amount`,`coupon_id`,`coupon_amount`,`delivery_fee`,`payable_amount`,`actual_amount`,`member_pay_amount`,`source`,`remark`,`order_time`,`pay_time`) VALUES
(2,'SO20260903000002',1,2,2,1,3,'四川省成都市锦江区春熙路步行街某公寓1203 李小红 13811110002',1,4,2,50.00,10.00,0.00,0.00,2,10.00,3.00,43.00,43.00,0.00,1,'少放辣','2026-09-03 18:00:00','2026-09-03 18:01:00');

INSERT INTO `order_detail` (`id`,`order_id`,`dish_id`,`dish_name`,`dish_price`,`spec_name`,`taste_name`,`quantity`,`subtotal`,`status`) VALUES
(5,2,4,'鱼香肉丝',28.00,'标准份','微辣',1,28.00,2),
(6,2,13,'米饭',3.00,NULL,NULL,2,6.00,3),
(7,2,10,'番茄蛋汤',15.00,'标准份',NULL,1,15.00,3);

INSERT INTO `payment_record` (`pay_no`,`order_id`,`store_id`,`member_id`,`pay_type`,`pay_channel`,`amount`,`status`,`transaction_id`,`idempotency_key`,`notify_time`,`pay_time`) VALUES
('PAY20260903000002',2,1,2,'ALIPAY','支付宝',43.00,2,'ali_202609030002','IDEMP-20260903-0002','2026-09-03 18:01:01','2026-09-03 18:01:00');

-- 积分示例记录
INSERT INTO `member_points_record` (`member_id`,`points_change`,`points_after`,`change_type`,`source_type`,`source_id`,`remark`) VALUES
(1,80,150,'CONSUME','ORDER',1,'消费80元获得积分');

-- 钱包交易记录示例
INSERT INTO `member_balance_record` (`member_id`,`change_amount`,`balance_after`,`change_type`,`source_type`,`source_id`,`remark`) VALUES
(1,-80.00,120.00,'CONSUME','ORDER',1,'订单SO20260903000001支付');

-- 充值记录示例
INSERT INTO `member_recharge_record` (`recharge_no`,`member_id`,`store_id`,`recharge_amount`,`gift_amount`,`pay_type`,`status`,`operator_id`,`remark`) VALUES
('RC202609030001',3,1,500.00,50.00,'WECHAT',1,7,'收银台会员充值');

-- ---------------------------------------------------------------------
-- 14. 排队、预约、评价、反馈示例
-- ---------------------------------------------------------------------
INSERT INTO `queue` (`store_id`,`queue_no`,`table_type_id`,`persons`,`member_id`,`phone`,`status`,`create_time`) VALUES
(1,'A001',2,4,2,'13811110002',1,'2026-09-03 18:30:00'),
(1,'A002',3,6,NULL,'13811110006',1,'2026-09-03 18:35:00');

INSERT INTO `reservation` (`store_id`,`table_id`,`table_type_id`,`member_id`,`contact_name`,`contact_phone`,`reservation_date`,`reservation_time`,`persons`,`status`,`create_time`) VALUES
(1,9,4,3,'王大力','13811110003','2026-09-04','19:00:00',8,1,'2026-09-03 09:00:00'),
(1,5,2,2,'李小红','13811110002','2026-09-05','18:30:00',4,2,'2026-09-02 20:00:00');

INSERT INTO `dish_review` (`order_id`,`order_detail_id`,`dish_id`,`member_id`,`store_id`,`rating`,`content`,`is_anonymous`) VALUES
(1,1,3,1,1,5,'宫保鸡丁味道正宗，鸡肉很嫩！',0);

INSERT INTO `feedback` (`member_id`,`store_id`,`content`,`contact_phone`,`status`) VALUES
(1,1,'希望可以增加积分兑换功能','13811110001',1);

-- ---------------------------------------------------------------------
-- 15. 购物车示例
-- ---------------------------------------------------------------------
INSERT INTO `cart` (`member_id`,`store_id`,`dish_id`,`dish_name`,`spec_name`,`taste_name`,`price`,`quantity`) VALUES
(5,1,3,'宫保鸡丁','标准份','微辣',32.00,1),
(5,1,13,'米饭',NULL,NULL,3.00,2);

-- =====================================================================
-- 消息队列消费日志表：用于消息幂等消费，防止重复处理
-- 每次消费前先 INSERT，唯一键冲突则跳过（已消费过）
-- =====================================================================
DROP TABLE IF EXISTS `mq_consume_log`;
CREATE TABLE `mq_consume_log` (
    `id`              BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    `message_id`      VARCHAR(128)    NOT NULL COMMENT '消息唯一ID（业务层生成，如 orderId + 事件类型）',
    `queue_name`      VARCHAR(64)     NOT NULL COMMENT '队列名称',
    `status`          TINYINT         NOT NULL DEFAULT 1 COMMENT '消费状态 1成功 2失败',
    `error_message`   VARCHAR(1000)   DEFAULT NULL COMMENT '错误信息',
    `consumed_at`     DATETIME        NOT NULL COMMENT '消费时间',
    `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`) USING BTREE COMMENT '消息ID唯一键，保证幂等消费',
    KEY `idx_queue_name` (`queue_name`) USING BTREE,
    KEY `idx_consumed_at` (`consumed_at`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='消息队列消费日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 16. 员工登录日志 / 红包 / 退单 示例数据（补齐空表）
-- ---------------------------------------------------------------------
INSERT INTO `staff_login_log` (`staff_id`,`login_time`,`login_ip`,`device`,`login_result`) VALUES
(2,'2026-09-03 09:00:00','192.168.1.100','Web Chrome',1),
(7,'2026-09-03 11:00:00','192.168.1.101','APP iPhone14',1),
(8,'2026-09-03 11:05:00','192.168.1.102','APP Android',1),
(7,'2026-09-03 11:06:00','192.168.1.101','APP iPhone14',0);

INSERT INTO `red_packet` (`red_packet_no`,`member_id`,`store_id`,`amount`,`source`,`status`,`expire_time`,`create_time`) VALUES
('RP202609030001',3,1,10.00,'RECHARGE_REWARD',1,'2026-12-31 23:59:59','2026-09-03 10:00:00');

INSERT INTO `refund` (`refund_no`,`order_id`,`order_detail_id`,`store_id`,`member_id`,`refund_type`,`dish_name`,`quantity`,`amount`,`reason`,`status`,`operator_id`,`create_time`) VALUES
('RF202609030001',2,5,1,2,2,'鱼香肉丝',1,28.00,'顾客要求退菜',2,7,'2026-09-03 18:20:00');

-- =====================================================================
-- 第三部分：报表视图（支撑商户端报表模块）
-- =====================================================================

-- 视图1：菜品流水（明细级流水记录，对应报表-菜品流水）
CREATE OR REPLACE VIEW `v_dish_flow` AS
SELECT od.id AS flow_id, o.order_no, o.order_type, o.order_time,
       s.store_name, d.dish_name, dc.category_name,
       od.dish_price, od.quantity, od.subtotal, od.status AS make_status,
       m.nickname AS member_name
FROM `order_detail` od
JOIN `orders` o ON od.order_id = o.id
JOIN `store` s ON o.store_id = s.id
JOIN `dish` d ON od.dish_id = d.id
LEFT JOIN `dish_category` dc ON d.category_id = dc.id
LEFT JOIN `member` m ON o.member_id = m.id
WHERE od.is_deleted = 0;

-- 视图2：收款日报（按门店按天汇总实收，对应报表-收款报表）
CREATE OR REPLACE VIEW `v_receipt_report` AS
SELECT o.store_id, s.store_name, DATE(o.pay_time) AS pay_date,
       COUNT(DISTINCT o.id) AS order_count,
       SUM(o.actual_amount) AS actual_income,
       SUM(o.dish_amount) AS dish_income,
       SUM(o.discount_amount + o.free_amount + o.rounding_amount + o.coupon_amount) AS discount_total
FROM `orders` o
JOIN `store` s ON o.store_id = s.id
WHERE o.pay_status = 2 AND o.pay_time IS NOT NULL AND o.is_deleted = 0
GROUP BY o.store_id, s.store_name, DATE(o.pay_time);

-- 视图3：菜品销售排行（对应报表-菜品销售排行）
CREATE OR REPLACE VIEW `v_dish_sales_ranking` AS
SELECT d.id AS dish_id, d.dish_name, d.price, dc.category_name,
       s.store_name, SUM(od.quantity) AS sale_count,
       SUM(od.subtotal) AS sale_amount
FROM `order_detail` od
JOIN `orders` o ON od.order_id = o.id AND o.pay_status = 2
JOIN `dish` d ON od.dish_id = d.id
LEFT JOIN `dish_category` dc ON d.category_id = dc.id
LEFT JOIN `store` s ON o.store_id = s.id
WHERE od.is_deleted = 0
GROUP BY d.id, d.dish_name, d.price, dc.category_name, s.store_name;

-- 视图4：门店销售排行（对应报表-门店销售排行）
CREATE OR REPLACE VIEW `v_store_sales_ranking` AS
SELECT o.store_id, s.store_name, COUNT(DISTINCT o.id) AS order_count,
       SUM(o.actual_amount) AS sales_amount
FROM `orders` o
JOIN `store` s ON o.store_id = s.id
WHERE o.pay_status = 2 AND o.is_deleted = 0
GROUP BY o.store_id, s.store_name;

-- 视图5：今日收入（收银端/首页今日统计）
CREATE OR REPLACE VIEW `v_today_income` AS
SELECT o.store_id, s.store_name,
       COUNT(DISTINCT o.id) AS today_order_count,
       COALESCE(SUM(o.actual_amount),0) AS today_income
FROM `orders` o
JOIN `store` s ON o.store_id = s.id
WHERE o.pay_status = 2 AND DATE(o.pay_time) = CURDATE() AND o.is_deleted = 0
GROUP BY o.store_id, s.store_name;

-- 视图6：退单记录（对应报表-退单记录）
CREATE OR REPLACE VIEW `v_refund_record` AS
SELECT r.refund_no, r.refund_type, r.dish_name, r.quantity, r.amount,
       r.reason, r.status, o.order_no, o.order_type, s.store_name,
       m.nickname AS member_name, r.create_time AS refund_time
FROM `refund` r
LEFT JOIN `orders` o ON r.order_id = o.id
LEFT JOIN `store` s ON r.store_id = s.id
LEFT JOIN `member` m ON r.member_id = m.id;

-- =====================================================================
-- 建表与初始化数据脚本结束
-- =====================================================================