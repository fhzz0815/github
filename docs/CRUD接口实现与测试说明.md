# 智慧餐厅后台管理系统 — CRUD 接口实现与测试说明

> 本文档由数据库字典与后端代码自动生成，覆盖全部 43 个业务模块的增删改查（CRUD）接口。
> 测试结论：43 个模块 × 5 类操作（新增 / 分页列表 / 详情 / 修改 / 删除）共 215 项检查全部通过；13 项参数校验与异常处理检查全部通过。

## 一、通用约定

| 项 | 说明 |
|---|---|
| 基础地址 | `/smart_restaurant/api/v1`（开发期前端通过 Vite 代理转发） |
| 请求格式 | `application/json; charset=utf-8` |
| 认证方式 | 请求头 `Authorization: Bearer <token>`，登录成功后返回；不带/带错返回 401 |
| 统一返回 | `{ "code": 0, "message": "操作成功", "data": ... }`，`code=0` 表示成功 |
| 分页返回 | `data` 为 `{ "list": [...], "total": 总数, "page": 当前页, "size": 每页条数 }` |
| 删除方式 | 含 `is_deleted` 字段的表为**逻辑删除**（is_deleted=1，列表/详情自动过滤），其余为**物理删除** |

### 五个标准接口（每个模块路径前缀为下表 `接口路径`）

| 操作 | 方法 | 地址 | 输入参数 | 成功输出 |
|---|---|---|---|---|
| 分页列表 | GET | `/{path}?page=1&size=10` | query：`page`(默认1)、`size`(默认10)；其余实体字段可作为模糊/等值查询条件 | `Result<PageResult>`，data.list 为记录数组、data.total 为总数 |
| 详情 | GET | `/{path}/{id}` | path：记录主键 id | `Result<Entity>`，不存在时 data 为 null |
| 新增 | POST | `/{path}` | body：实体 JSON，必填字段见各模块说明 | `Result`，code=0 成功 |
| 修改 | PUT | `/{path}/{id}` | path：id；body：需要更新的字段（仅更新非空字段） | `Result`，code=0 成功 |
| 删除 | DELETE | `/{path}/{id}` | path：id | `Result`，code=0 成功 |

### 统一状态码 / 错误处理

| code | HTTP | 含义 | 触发场景 |
|---|---|---|---|
| 0 | 200 | 操作成功 | 正常增删改查 |
| 401 | 401 | 未登录/登录失效 | 缺少 token、token 错误或过期 |
| 1001 | 400 | 参数错误 | 必填项缺失、JSON 格式错误、日期格式不对、编号传成字母 |
| 1002 | 200 | 用户不存在 | 登录账号不存在 |
| 1003 | 400 | 数据重复 | 唯一键冲突（如用户名、编号、编码已存在） |
| 1004 | 200 | 密码错误 | 登录密码不正确 |
| 500 | 500 | 系统繁忙 | 未预期的服务端异常（已全局兜底，不外泄堆栈） |

### 约束与限制

- 所有写接口均做动态 SQL：新增只插入非空字段（有默认值/自增的字段无需传），修改只更新非空字段。
- 日期字段类型分三种，格式传错会返回 1001：日期 `yyyy-MM-dd`、时间 `HH:mm:ss`、日期时间 `yyyy-MM-dd HH:mm:ss`。
- 唯一键冲突返回 1003；NOT NULL 必填缺失返回 1001；查询不存在的 id 返回 code=0 且 data=null（不报错）。
- 数据库未启用外键约束（应用层维护关联），删除父记录不会级联，联表关系由业务保证。

## 二、各模块明细

下表“新增必填参数”为数据库中 `NOT NULL` 且无默认值、非自增的字段（其余字段可选）。

### sys_role（sys_role）

- 接口路径前缀：`/api/v1/sysRoles`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：roleCode（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | roleName | string | 角色名称 |
  | roleCode | string | 角色编码 |

### sys_permission（sys_permission）

- 接口路径前缀：`/api/v1/sysPermissions`
- 删除方式：物理删除
- 唯一约束：permissionCode（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | permissionName | string | 权限名称 |
  | permissionCode | string | 权限编码（接口/按钮标识） |

### sys_role_permission（sys_role_permission）

- 接口路径前缀：`/api/v1/sysRolePermissions`
- 删除方式：物理删除
- 唯一约束：roleId + permissionId（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | roleId | integer(关联ID) | 角色ID |
  | permissionId | integer(关联ID) | 权限ID |

### sys_user（sys_user）

- 接口路径前缀：`/api/v1/sysUsers`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：username（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | username | string | 登录账号（手机号） |
  | password | string | 登录密码（生产用bcrypt加密存储） |
  | roleId | integer(关联ID) | 角色ID |

### staff_schedule（staff_schedule）

- 接口路径前缀：`/api/v1/staffSchedules`
- 删除方式：物理删除
- 唯一约束：staffId + workDate（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | staffId | integer(关联ID) | 员工ID |
  | workDate | date (yyyy-MM-dd) | 排班日期 |

### staff_login_log（staff_login_log）

- 接口路径前缀：`/api/v1/staffLoginLogs`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | staffId | integer(关联ID) | 员工ID |

### store（store）

- 接口路径前缀：`/api/v1/stores`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：storeNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeNo | string | 门店编号 |
  | storeName | string | 门店名称 |

### store_payment_setting（store_payment_setting）

- 接口路径前缀：`/api/v1/storePaymentSettings`
- 删除方式：物理删除
- 唯一约束：storeId + payType（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | payType | string | 支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金 |

### member_category（member_category）

- 接口路径前缀：`/api/v1/memberCategories`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | categoryName | string | 类别名称 |

### member（member）

- 接口路径前缀：`/api/v1/members`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：openid；memberNo；phone（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | （无强制必填字段） | | |

### member_address（member_address）

- 接口路径前缀：`/api/v1/memberAddresses`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | contactName | string | 联系人姓名 |
  | contactPhone | string | 联系人电话 |
  | detailAddress | string | 详细地址 |

### member_bank_card（member_bank_card）

- 接口路径前缀：`/api/v1/memberBankCards`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | cardNo | string | 银行卡号 |

### member_balance_record（member_balance_record）

- 接口路径前缀：`/api/v1/memberBalanceRecords`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | changeAmount | number | 变动金额（正=收入 负=支出） |
  | balanceAfter | number | 变动后余额 |
  | changeType | string | 变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付 |

### member_points_record（member_points_record）

- 接口路径前缀：`/api/v1/memberPointsRecords`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | pointsChange | integer | 积分变动（正=增加 负=扣减） |
  | pointsAfter | integer | 变动后积分 |
  | changeType | string | 变动类型 CONSUME消费获得 EXCHANGE积分兑换 REFUND退款扣回 ACTIVITY活动赠送 |

### member_recharge_record（member_recharge_record）

- 接口路径前缀：`/api/v1/memberRechargeRecords`
- 删除方式：物理删除
- 唯一约束：rechargeNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | rechargeNo | string | 充值单号 |
  | memberId | integer(关联ID) | 客户ID |
  | rechargeAmount | number | 充值金额(元) |

### red_packet（red_packet）

- 接口路径前缀：`/api/v1/redPackets`
- 删除方式：物理删除
- 唯一约束：redPacketNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | redPacketNo | string | 红包编号 |
  | memberId | integer(关联ID) | 客户ID |
  | amount | number | 红包金额(元) |

### dish_category（dish_category）

- 接口路径前缀：`/api/v1/dishCategories`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | categoryName | string | 分类名称 |

### dish_taste（dish_taste）

- 接口路径前缀：`/api/v1/dishTastes`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | tasteName | string | 口味名称 |

### dish_spec（dish_spec）

- 接口路径前缀：`/api/v1/dishSpecs`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | specName | string | 规格名称 |

### ingredient_category（ingredient_category）

- 接口路径前缀：`/api/v1/ingredientCategories`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | categoryName | string | 类别名称 |

### ingredient（ingredient）

- 接口路径前缀：`/api/v1/ingredients`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | ingredientName | string | 原料名称 |

### dish（dish）

- 接口路径前缀：`/api/v1/dishes`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | categoryId | integer(关联ID) | 菜品分类ID |
  | dishName | string | 菜品名称 |
  | price | number | 单价(元) |

### dish_ingredient_rel（dish_ingredient_rel）

- 接口路径前缀：`/api/v1/dishIngredientRels`
- 删除方式：物理删除
- 唯一约束：dishId + ingredientId（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dishId | integer(关联ID) | 菜品ID |
  | ingredientId | integer(关联ID) | 原料ID |

### dish_review（dish_review）

- 接口路径前缀：`/api/v1/dishReviews`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | dishId | integer(关联ID) | 菜品ID |
  | memberId | integer(关联ID) | 客户ID |

### table_type（table_type）

- 接口路径前缀：`/api/v1/tableTypes`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | typeName | string | 桌型名称 |

### dining_table（dining_table）

- 接口路径前缀：`/api/v1/diningTables`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：storeId + tableNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | tableTypeId | integer(关联ID) | 桌型ID |
  | tableNo | string | 桌号 |

### queue（queue）

- 接口路径前缀：`/api/v1/queues`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | queueNo | string | 排队号(如A001) |

### reservation（reservation）

- 接口路径前缀：`/api/v1/reservations`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | memberId | integer(关联ID) | 客户ID |
  | contactName | string | 联系人姓名 |
  | contactPhone | string | 联系电话 |
  | reservationDate | date (yyyy-MM-dd) | 预约日期 |
  | reservationTime | time (HH:mm:ss) | 预约时间 |

### cart（cart）

- 接口路径前缀：`/api/v1/carts`
- 删除方式：物理删除
- 唯一约束：memberId + storeId + dishId + specName + tasteName（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | storeId | integer(关联ID) | 门店ID |
  | dishId | integer(关联ID) | 菜品ID |
  | price | number | 加入时单价 |

### orders（orders）

- 接口路径前缀：`/api/v1/orders`
- 删除方式：逻辑删除（is_deleted=1）
- 唯一约束：orderNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | orderNo | string | 订单号 |
  | storeId | integer(关联ID) | 门店ID |
  | memberId | integer(关联ID) | 客户ID |
  | orderType | integer(枚举状态,0/1...) | 订单类型 1堂食 2外卖 3自取 |

### order_detail（order_detail）

- 接口路径前缀：`/api/v1/orderDetails`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | orderId | integer(关联ID) | 订单ID |
  | dishId | integer(关联ID) | 菜品ID |
  | dishName | string | 菜品名称快照 |
  | dishPrice | number | 成交单价(元) |

### order_status_log（order_status_log）

- 接口路径前缀：`/api/v1/orderStatusLogs`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | orderId | integer(关联ID) | 订单ID |
  | toStatus | integer(枚举状态,0/1...) | 新状态 |

### payment_record（payment_record）

- 接口路径前缀：`/api/v1/paymentRecords`
- 删除方式：物理删除
- 唯一约束：payNo；idempotencyKey（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | payNo | string | 支付流水号 |
  | orderId | integer(关联ID) | 订单ID |
  | payType | string | 支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付 |
  | amount | number | 支付金额(元) |

### refund（refund）

- 接口路径前缀：`/api/v1/refunds`
- 删除方式：物理删除
- 唯一约束：refundNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | refundNo | string | 退单号 |
  | orderId | integer(关联ID) | 订单ID |
  | amount | number | 退款金额(元) |

### coupon（coupon）

- 接口路径前缀：`/api/v1/coupons`
- 删除方式：逻辑删除（is_deleted=1）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | couponName | string | 优惠券名称 |

### member_coupon（member_coupon）

- 接口路径前缀：`/api/v1/memberCoupons`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | memberId | integer(关联ID) | 客户ID |
  | couponId | integer(关联ID) | 优惠券ID |

### dish_stock（dish_stock）

- 接口路径前缀：`/api/v1/dishStocks`
- 删除方式：物理删除
- 唯一约束：storeId + dishId（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | dishId | integer(关联ID) | 菜品ID |

### ingredient_stock（ingredient_stock）

- 接口路径前缀：`/api/v1/ingredientStocks`
- 删除方式：物理删除
- 唯一约束：storeId + ingredientId（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | ingredientId | integer(关联ID) | 原料ID |

### stock_check_dish（stock_check_dish）

- 接口路径前缀：`/api/v1/stockCheckDishes`
- 删除方式：物理删除
- 唯一约束：checkNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | checkNo | string | 盘点单号 |
  | storeId | integer(关联ID) | 门店ID |
  | dishId | integer(关联ID) | 菜品ID |

### stock_check_ingredient（stock_check_ingredient）

- 接口路径前缀：`/api/v1/stockCheckIngredients`
- 删除方式：物理删除
- 唯一约束：checkNo（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | checkNo | string | 盘点单号 |
  | storeId | integer(关联ID) | 门店ID |
  | ingredientId | integer(关联ID) | 原料ID |

### printer（printer）

- 接口路径前缀：`/api/v1/printers`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | storeId | integer(关联ID) | 门店ID |
  | printerName | string | 打印机名称 |

### receipt_template（receipt_template）

- 接口路径前缀：`/api/v1/receiptTemplates`
- 删除方式：物理删除
- 唯一约束：templateType（重复提交返回 1003）
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | templateType | string | 类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值 |
  | templateName | string | 模板名称 |

### feedback（feedback）

- 接口路径前缀：`/api/v1/feedbacks`
- 删除方式：物理删除
- 新增必填参数：

  | 字段 | 类型 | 含义 |
  |---|---|---|
  | content | string | 反馈内容 |

