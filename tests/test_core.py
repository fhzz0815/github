"""
基础设施模块单元测试
"""

from app.core.config import Settings
from app.core.constants import ErrorCode, UserType
from app.core.exceptions import (
    AppException,
    NotFoundException,
    UnauthorizedException,
)


class TestConfig:
    """配置管理测试"""

    def test_default_values(self):
        """测试默认配置值"""
        settings = Settings()
        assert settings.APP_NAME == 'smart-community'
        assert settings.APP_VERSION == '0.1.0'
        assert settings.API_V1_PREFIX == '/api/v1'


class TestExceptions:
    """异常定义测试"""

    def test_app_exception(self):
        """测试业务异常基类"""
        exc = AppException(code='TEST', message='测试异常')
        assert exc.code == 'TEST'
        assert exc.message == '测试异常'
        assert exc.status_code == 400

    def test_not_found_exception(self):
        """测试资源不存在异常"""
        exc = NotFoundException('用户', 1)
        assert exc.code == 'NOT_FOUND'
        assert '用户' in exc.message
        assert exc.status_code == 404

    def test_unauthorized_exception(self):
        """测试未授权异常"""
        exc = UnauthorizedException()
        assert exc.code == 'UNAUTHORIZED'
        assert exc.status_code == 401


class TestConstants:
    """常量定义测试"""

    def test_user_types(self):
        """测试用户类型枚举"""
        assert UserType.OWNER == 'OWNER'
        assert UserType.STAFF == 'STAFF'
        assert UserType.PLATFORM == 'PLATFORM'

    def test_error_codes(self):
        """测试错误码定义"""
        assert ErrorCode.SUCCESS == 0
        assert ErrorCode.UNAUTHORIZED == 2001
        assert ErrorCode.AI_QUOTA_EXCEEDED == 5001
