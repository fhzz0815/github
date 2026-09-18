"""
全局常量定义

包含枚举值、错误码、状态码等不可变常量。
"""


# ============================================================
# 用户类型
# ============================================================
class UserType:
    """用户类型枚举"""

    OWNER = 'OWNER'  # 业主
    STAFF = 'STAFF'  # 物业员工
    PLATFORM = 'PLATFORM'  # 平台运营


# ============================================================
# 客户端类型
# ============================================================
class ClientType:
    """客户端类型枚举"""

    OWNER_APP = 'OWNER_APP'  # 业主端 APP
    OWNER_MINI = 'OWNER_MINI'  # 业主端小程序
    STAFF_APP = 'STAFF_APP'  # 物业端 APP
    ADMIN_WEB = 'ADMIN_WEB'  # 物业管理中台
    OPS_WEB = 'OPS_WEB'  # SaaS 运营平台


# ============================================================
# 业务状态码
# ============================================================
class ErrorCode:
    """统一错误码定义"""

    # 通用 (1xxx)
    SUCCESS = 0
    UNKNOWN_ERROR = 1000
    VALIDATION_ERROR = 1001

    # 鉴权 (2xxx)
    UNAUTHORIZED = 2001
    TOKEN_EXPIRED = 2002
    FORBIDDEN = 2003
    INVALID_TOKEN = 2004

    # 租户 (3xxx)
    TENANT_REQUIRED = 3001
    TENANT_MISMATCH = 3002

    # 业务 (4xxx)
    NOT_FOUND = 4001
    CONFLICT = 4002
    DUPLICATE = 4003

    # AI (5xxx)
    AI_QUOTA_EXCEEDED = 5001
    AI_MODEL_UNAVAILABLE = 5002
    AI_CONTENT_FILTERED = 5003
    AI_TIMEOUT = 5004


# ============================================================
# 数据权限级别
# ============================================================
class DataScope:
    """数据权限范围"""

    ALL = 'ALL'  # 全部数据
    COMMUNITY = 'COMMUNITY'  # 本社区
    DEPARTMENT = 'DEPARTMENT'  # 本部门及下级
    SELF = 'SELF'  # 本人


# ============================================================
# 其他常量
# ============================================================
PLATFORM_TENANT_ID = 0  # 平台租户 ID
MAX_PAGE_SIZE = 100  # 分页最大条数
DEFAULT_PAGE_SIZE = 20  # 默认分页条数
