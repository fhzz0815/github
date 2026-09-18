"""
通用数据契约

定义统一响应格式、分页参数等基础模型。
"""

from typing import Any, Generic, TypeVar

from pydantic import BaseModel, Field

T = TypeVar('T')


class ResponseModel(BaseModel, Generic[T]):
    """统一响应模型"""

    code: int = Field(default=0, description='业务状态码，0=成功')
    message: str = Field(default='success', description='提示信息')
    data: T | None = Field(default=None, description='响应数据')
    details: dict[str, Any] | None = Field(default=None, description='详细信息')


class PaginationParams(BaseModel):
    """分页参数"""

    page: int = Field(default=1, ge=1, description='页码')
    page_size: int = Field(default=20, ge=1, le=100, description='每页条数')


class PageResult(BaseModel, Generic[T]):
    """分页结果"""

    items: list[T] = Field(description='数据列表')
    total: int = Field(description='总条数')
    page: int = Field(description='当前页码')
    page_size: int = Field(description='每页条数')
    total_pages: int = Field(description='总页数')
