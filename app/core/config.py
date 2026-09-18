"""
应用配置管理

使用 pydantic-settings 从环境变量和 .env 文件加载配置。
所有配置项通过 settings 单例访问。
"""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """应用配置类 - 全部配置从环境变量读取"""

    # === 应用基础配置 ===
    APP_NAME: str = 'smart-community'
    APP_VERSION: str = '0.1.0'
    APP_ENV: str = 'dev'  # dev / test / staging / prod
    DEBUG: bool = True
    SECRET_KEY: str = 'change-me-in-production'
    API_V1_PREFIX: str = '/api/v1'
    CORS_ORIGINS: list[str] = ['*']

    # === 数据库 (MySQL 8) ===
    DB_HOST: str = 'localhost'
    DB_PORT: int = 3306
    DB_USER: str = 'root'
    DB_PASSWORD: str = ''
    DB_NAME: str = 'smart_community'
    DB_POOL_SIZE: int = 10
    DB_MAX_OVERFLOW: int = 20
    DB_SLAVE_HOST: str | None = None
    DB_SLAVE_PORT: int | None = None

    @property
    def DATABASE_URL(self) -> str:
        """主库连接 URL"""
        return f'mysql+pymysql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}?charset=utf8mb4'

    @property
    def DATABASE_URL_SLAVE(self) -> str | None:
        """从库连接 URL（读负载均衡）"""
        if self.DB_SLAVE_HOST:
            return f'mysql+pymysql://{self.DB_USER}:{self.DB_PASSWORD}@{self.DB_SLAVE_HOST}:{self.DB_SLAVE_PORT}/{self.DB_NAME}?charset=utf8mb4'
        return None

    # === 缓存 (Redis 7) ===
    REDIS_HOST: str = 'localhost'
    REDIS_PORT: int = 6379
    REDIS_PASSWORD: str | None = None
    REDIS_DB: int = 0
    REDIS_SENTINEL_MASTER: str | None = None
    REDIS_SENTINEL_NODES: str | None = None

    @property
    def REDIS_URL(self) -> str:
        if self.REDIS_PASSWORD:
            return f'redis://:{self.REDIS_PASSWORD}@{self.REDIS_HOST}:{self.REDIS_PORT}/{self.REDIS_DB}'
        return f'redis://{self.REDIS_HOST}:{self.REDIS_PORT}/{self.REDIS_DB}'

    # === 消息队列 ===
    RABBITMQ_HOST: str = 'localhost'
    RABBITMQ_PORT: int = 5672
    RABBITMQ_USER: str = 'guest'
    RABBITMQ_PASSWORD: str = 'guest'

    # === LLM 模型配置 ===
    LLM_PROVIDER: str = 'openai'
    OPENAI_API_KEY: str | None = None
    OPENAI_BASE_URL: str = 'https://api.openai.com/v1'
    OPENAI_MODEL: str = 'gpt-4o'
    EMBEDDING_MODEL: str = 'text-embedding-3-small'
    RERANK_MODEL: str | None = None

    # === 向量库 ===
    VECTOR_STORE_TYPE: str = 'pgvector'
    VECTOR_STORE_HOST: str = 'localhost'
    VECTOR_STORE_PORT: int = 5432

    # === JWT ===
    JWT_SECRET_KEY: str = 'change-me'
    JWT_ACCESS_TOKEN_EXPIRE_SECONDS: int = 7200
    JWT_REFRESH_TOKEN_EXPIRE_SECONDS: int = 1209600

    # === 日志 ===
    LOG_LEVEL: str = 'DEBUG'
    LOG_FORMAT: str = 'json'  # json / console

    # === Celery ===
    CELERY_BROKER_URL: str = 'redis://localhost:6379/1'
    CELERY_RESULT_BACKEND: str = 'redis://localhost:6379/2'

    # === AI 配额与限流 ===
    AI_TOKEN_LIMIT_PER_DAY: int = 1_000_000
    AI_COST_ALERT_THRESHOLD: float = 100.0
    AI_RATE_LIMIT_PER_MINUTE: int = 30

    model_config = SettingsConfigDict(
        env_file='.env',
        env_file_encoding='utf-8',
        case_sensitive=True,
        extra='ignore',
    )


# 全局单例配置
settings = Settings()
