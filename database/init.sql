-- ============================================================
-- 智慧社区AI系统 - 数据库初始化脚本
-- MySQL 8.0+, 字符集 utf8mb4
-- ============================================================

-- 创建数据库（如 docker-compose 已自动创建则跳过）
-- CREATE DATABASE IF NOT EXISTS smart_community
--     CHARACTER SET utf8mb4
--     COLLATE utf8mb4_0900_ai_ci;

-- ============================================================
-- SaaS 平台基础表
-- ============================================================

-- 平台套餐表
CREATE TABLE IF NOT EXISTS saas_package (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL COMMENT '套餐名称',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '套餐编码',
    description TEXT COMMENT '套餐描述',
    price DECIMAL(14,2) NOT NULL DEFAULT 0 COMMENT '价格',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态: 1=启用, 0=停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='平台套餐表';

-- 物业公司表
CREATE TABLE IF NOT EXISTS saas_company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL COMMENT '公司名称',
    contact_person VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(256) COMMENT '公司地址',
    tenant_id INT NOT NULL DEFAULT 0 COMMENT '租户ID（与ID一致）',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态: 1=启用, 0=停用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物业公司表';

-- ============================================================
-- 社区基础数据表
-- ============================================================

-- 社区表
CREATE TABLE IF NOT EXISTS com_community (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    name VARCHAR(128) NOT NULL COMMENT '社区名称',
    address VARCHAR(256) COMMENT '社区地址',
    province VARCHAR(64) COMMENT '省份',
    city VARCHAR(64) COMMENT '城市',
    district VARCHAR(64) COMMENT '区县',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_tenant_community (tenant_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区表';

-- 楼宇表
CREATE TABLE IF NOT EXISTS com_building (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    community_id INT NOT NULL COMMENT '所属社区ID',
    name VARCHAR(64) NOT NULL COMMENT '楼宇名称',
    total_floors INT COMMENT '总楼层数',
    total_units INT COMMENT '总单元数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_community (tenant_id, community_id),
    INDEX idx_community (community_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='楼宇表';

-- 单元表
CREATE TABLE IF NOT EXISTS com_unit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    building_id INT NOT NULL COMMENT '所属楼宇ID',
    name VARCHAR(64) NOT NULL COMMENT '单元名称',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_building (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='单元表';

-- 房屋表
CREATE TABLE IF NOT EXISTS est_house (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    community_id INT NOT NULL COMMENT '所属社区ID',
    building_id INT NOT NULL COMMENT '所属楼宇ID',
    unit_id INT COMMENT '所属单元ID',
    room_number VARCHAR(32) NOT NULL COMMENT '房号',
    floor INT COMMENT '所在楼层',
    area DECIMAL(10,2) COMMENT '面积（平方米）',
    owner_name VARCHAR(64) COMMENT '业主姓名',
    owner_phone VARCHAR(20) COMMENT '业主电话',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态: 1=未售, 2=已售, 3=已入住',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_community (tenant_id, community_id),
    INDEX idx_building (building_id),
    INDEX idx_owner_phone (owner_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房屋表';

-- ============================================================
-- AI 相关表
-- ============================================================

-- AI 会话表
CREATE TABLE IF NOT EXISTS ai_session (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话唯一标识',
    tenant_id INT NOT NULL COMMENT '租户ID',
    user_id INT NOT NULL COMMENT '用户ID',
    title VARCHAR(128) COMMENT '会话标题',
    agent_type VARCHAR(32) COMMENT '智能体类型',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态: 1=进行中, 2=已结束',
    total_messages INT NOT NULL DEFAULT 0 COMMENT '消息总数',
    total_tokens BIGINT NOT NULL DEFAULT 0 COMMENT '消耗总token数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_user (tenant_id, user_id),
    INDEX idx_tenant_session (tenant_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI会话表';

-- AI 消息表
CREATE TABLE IF NOT EXISTS ai_message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL COMMENT '所属会话ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    role VARCHAR(16) NOT NULL COMMENT '角色: user/assistant/system/tool',
    content TEXT COMMENT '消息内容',
    tool_calls JSON COMMENT '工具调用信息',
    tool_call_id VARCHAR(64) COMMENT '工具调用ID',
    token_count INT NOT NULL DEFAULT 0 COMMENT '消息token数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session (session_id),
    INDEX idx_tenant_session (tenant_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI消息表';

-- AI 调用链路记录表
CREATE TABLE IF NOT EXISTS ai_trace (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL UNIQUE COMMENT '追踪ID',
    tenant_id INT NOT NULL COMMENT '租户ID',
    session_id VARCHAR(64) COMMENT '会话ID',
    user_id INT COMMENT '用户ID',
    agent_type VARCHAR(32) COMMENT '智能体类型',
    model VARCHAR(64) COMMENT '使用的模型',
    prompt_tokens INT NOT NULL DEFAULT 0 COMMENT '提示token数',
    completion_tokens INT NOT NULL DEFAULT 0 COMMENT '生成token数',
    total_cost DECIMAL(16,6) COMMENT '调用成本',
    duration_ms INT COMMENT '耗时（毫秒）',
    status VARCHAR(16) COMMENT '状态: success/failed',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_created (tenant_id, created_at),
    INDEX idx_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI调用链路记录表';

-- AI 审计日志表
CREATE TABLE IF NOT EXISTS ai_audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tenant_id INT NOT NULL COMMENT '租户ID',
    trace_id VARCHAR(64) COMMENT '关联追踪ID',
    action VARCHAR(32) NOT NULL COMMENT '操作类型',
    prompt_version VARCHAR(32) COMMENT '提示词模板版本',
    model VARCHAR(64) COMMENT '模型名称',
    input_summary VARCHAR(256) COMMENT '输入摘要',
    output_summary VARCHAR(256) COMMENT '输出摘要',
    result VARCHAR(16) COMMENT '结果: pass/filtered/rejected',
    created_by INT COMMENT '操作人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_action (tenant_id, action),
    INDEX idx_tenant_created (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI审计日志表';
