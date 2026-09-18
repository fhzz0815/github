"""
社区管理相关模型

包含物业公司、社区、楼宇、单元、房屋等核心业务模型。
"""

from sqlalchemy import Integer, SmallInteger, String
from sqlalchemy.orm import Mapped, mapped_column

from app.models.base import Base, TenantMixin, TimestampMixin


class Company(Base, TenantMixin, TimestampMixin):
    """物业公司表"""

    __tablename__ = 'saas_company'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(128), nullable=False, comment='公司名称')
    contact_person: Mapped[str] = mapped_column(String(64), comment='联系人')
    contact_phone: Mapped[str] = mapped_column(String(20), comment='联系电话')
    address: Mapped[str] = mapped_column(String(256), comment='公司地址')
    status: Mapped[int] = mapped_column(SmallInteger, default=1, comment='状态: 1=启用, 0=停用')


class Community(Base, TenantMixin, TimestampMixin):
    """社区表"""

    __tablename__ = 'com_community'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(128), nullable=False, comment='社区名称')
    address: Mapped[str] = mapped_column(String(256), comment='社区地址')
    province: Mapped[str] = mapped_column(String(64), comment='省份')
    city: Mapped[str] = mapped_column(String(64), comment='城市')
    district: Mapped[str] = mapped_column(String(64), comment='区县')
    status: Mapped[int] = mapped_column(SmallInteger, default=1, comment='状态')


class Building(Base, TenantMixin, TimestampMixin):
    """楼宇表"""

    __tablename__ = 'com_building'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    community_id: Mapped[int] = mapped_column(Integer, nullable=False, comment='所属社区ID')
    name: Mapped[str] = mapped_column(String(64), nullable=False, comment='楼宇名称')
    total_floors: Mapped[int] = mapped_column(Integer, comment='总楼层数')
    total_units: Mapped[int] = mapped_column(Integer, comment='总单元数')


class Unit(Base, TenantMixin, TimestampMixin):
    """单元表"""

    __tablename__ = 'com_unit'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    building_id: Mapped[int] = mapped_column(Integer, nullable=False, comment='所属楼宇ID')
    name: Mapped[str] = mapped_column(String(64), nullable=False, comment='单元名称')


class House(Base, TenantMixin, TimestampMixin):
    """房屋表"""

    __tablename__ = 'est_house'

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    community_id: Mapped[int] = mapped_column(Integer, nullable=False, comment='所属社区ID')
    building_id: Mapped[int] = mapped_column(Integer, nullable=False, comment='所属楼宇ID')
    unit_id: Mapped[int] = mapped_column(Integer, comment='所属单元ID')
    room_number: Mapped[str] = mapped_column(String(32), nullable=False, comment='房号')
    floor: Mapped[int] = mapped_column(Integer, comment='所在楼层')
    area: Mapped[float] = mapped_column(comment='面积（平方米）')
    owner_name: Mapped[str] = mapped_column(String(64), comment='业主姓名')
    owner_phone: Mapped[str] = mapped_column(String(20), comment='业主电话')
    status: Mapped[int] = mapped_column(
        SmallInteger, default=1, comment='状态: 1=未售, 2=已售, 3=已入住'
    )
