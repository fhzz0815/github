# 员工管理系统层级权限控制实施方案

## 背景与目标

**问题**：当前系统仅有 JWT 登录校验，任何登录用户都能查看/修改所有员工数据，无层级权限控制。

**目标**：基于角色等级 + 门店归属实现三层级权限控制：
- **总店长 (level 99)**：全国员工完整 CRUD
- **店长 (level 50)**：本门店低等级员工完整 CRUD；跨门店同级仅查看基本信息
- **普通员工 (level 10)**：仅跨门店同级基本信息可见，不可修改任何人

**范围**：员工管理模块(sysUsers)做完整层级校验；角色/权限/角色权限模块仅总店长可访问；订单/菜品等业务模块按门店隔离（次要）。

## 实施步骤

### 第一步：数据库变更

**新文件** [add_role_level.sql](file:///d:/TraeProject/Smart%20Restaurant/database/add_role_level.sql)

给 `sys_role` 表加 `level` 列并回填5个预置角色等级：
- 总店长 GENERAL_MANAGER = 99
- 店长 STORE_MANAGER = 50
- 服务员/收银/后厨 = 10

同步更新 [smart_restaurant.sql](file:///d:/TraeProject/Smart%20Restaurant/database/smart_restaurant.sql) 中 `sys_role` 建表和 INSERT 语句（含 level 列），保证全新安装也能正确初始化。

### 第二步：后端实体与映射

**修改** [SysRoleEntity.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/entity/SysRoleEntity.java)
- 在 `roleCode` 后加 `private Integer level;`（角色等级 99/50/10）

**修改** [SysUserEntity.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/entity/SysUserEntity.java)
- 加 4 个非数据库列字段（联表回显用）：`roleLevel`、`roleName`、`roleCode`、`storeName`

**修改** [SysRoleMapper.xml](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/resources/mapper/SysRoleMapper.xml)
- `BaseResultMap`、`Base_Column_List`、`insert`、`update` 加入 `level` 字段

### 第三步：后端权限核心组件

**新文件** `sec-backend/src/main/java/com/iwe3/sec/common/LoginUser.java`
- POJO：userId, storeId, roleId, roleLevel, roleCode, username
- `@Data @Builder @NoArgsConstructor @AllArgsConstructor`

**新文件** `sec-backend/src/main/java/com/iwe3/sec/common/UserDataScope.java`
- POJO：allStores(总店长)、storeId+includeSameLevelCrossStore(店长)、levelOnly(普通员工)、currentLevel

**新文件** [PermissionChecker.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/common/PermissionChecker.java)
- `@Component`，注入 SysUserMapper + SysRoleMapper
- 关键方法：
  - `current()`：从请求属性取 `LoginUser`
  - `currentUserId() / currentStoreId() / currentRoleLevel()`
  - `assertGeneralManager()`：仅总店长可通过，否则抛 `BusinessException(403, "无权限，仅总店长可操作")`
  - `assertCanManageUser(target)`：判断能否增改删目标员工
    - 自己改自己：放行（profile 自助编辑场景）
    - 总店长：全放行
    - 店长：需同门店且目标等级严格低于自己
    - 普通员工：拒绝
  - `assertCanViewUserDetail(target)`：判断能否查看详情
    - 自己、总店长、本门店的店长、跨门店同级：放行
    - 否则拒绝
  - `buildUserDataScope()`：构造列表查询数据范围
  - `shouldMaskTarget(target)`：是否需要脱敏（非本门店或非管理者）
  - `maskBasicInfo(u)`：脱敏敏感字段（password/email/idCard/lastLoginTime/username/avatar 等置空，仅保留 id/realName/staffNo/phone/roleId/storeId）
- 常量：`LEVEL_GENERAL_MANAGER=99`、`LEVEL_STORE_MANAGER=50`

### 第四步：JWT 拦截器装载 LoginUser

**修改** [JwtInterceptor.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/common/JwtInterceptor.java)
- 注入 `SysUserMapper` 和 `SysRoleMapper`
- `preHandle` 在 `userId = jwtUtil.getUserId(token)` 之后：
  - 查 `SysUserEntity`，若不存在或已删除返回 401
  - 查 `SysRoleEntity` 取 `level` 和 `roleCode`
  - 构造 `LoginUser` 存入 `request.setAttribute("loginUser", loginUser)`
  - 同时保留原有 `currentUserId` 属性以向后兼容

### 第五步：员工 Mapper 新增数据范围查询

**修改** [SysUserMapper.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/mapper/SysUserMapper.java)
- 加方法：`List<SysUserEntity> selectListByDataScope(@Param("query") SysUserEntity query, @Param("scope") UserDataScope scope)`

**修改** [SysUserMapper.xml](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/resources/mapper/SysUserMapper.xml)
- 加 `DetailResultMap`（继承 BaseResultMap，加 roleLevel/roleName/roleCode/storeName 4 个字段）
- 加 `selectListByDataScope` SQL：LEFT JOIN `sys_role` 和 `store`，按 scope 条件过滤：
  - `allStores=true`：不加门店/等级过滤
  - `levelOnly != null`：`r.level = #{scope.levelOnly}`
  - `includeSameLevelCrossStore=true`：`(u.store_id = #{scope.storeId} OR r.level = #{scope.currentLevel})`
  - 否则：`u.store_id = #{scope.storeId}`
- 修改 `selectById` 使用 `DetailResultMap` + JOIN，让登录后 `sysUserApi.get(userId)` 自动返回 roleLevel

### 第六步：员工 Service 改造

**修改** [SysUserServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/SysUserServiceImpl.java)
- 注入 `PermissionChecker`
- `list`：用 `buildUserDataScope()` + `selectListByDataScope`，对每行 `shouldMaskTarget` 判断后 `maskBasicInfo` 脱敏
- `getById`：`assertCanViewUserDetail` 校验后脱敏
- `add`：`assertCanManageUser` 校验；店长强制 `storeId` 为本人门店
- `update`：先查 existing，`assertCanManageUser(existing)` 校验；若改 roleId 还需校验目标等级
- `remove`：先查 existing，`assertCanManageUser(existing)` 校验；额外禁止删除自己

### 第七步：角色/权限模块仅总店长可访问

**修改** 这3个 ServiceImpl，在 `list/getById` 也加 `assertGeneralManager()`（彻底锁死）：
- [SysRoleServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/SysRoleServiceImpl.java)
- [SysPermissionServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/SysPermissionServiceImpl.java)
- [SysRolePermissionServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/SysRolePermissionServiceImpl.java)

### 第八步：业务模块门店隔离（可选，本次实施）

**修改** 4 个核心业务 ServiceImpl，加门店校验（模板：注入 PermissionChecker，list 强制 storeId 为本人门店，getById/update/remove 校验目标数据 storeId 与本人一致）：
- [OrdersServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/OrdersServiceImpl.java)
- [DishServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/DishServiceImpl.java)
- [DiningTableServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/DiningTableServiceImpl.java)
- [MemberServiceImpl.java](file:///d:/TraeProject/Smart%20Restaurant/sec-backend/src/main/java/com/iwe3/sec/service/impl/MemberServiceImpl.java)

### 第九步：前端用户状态扩展

**修改** [user.js](file:///d:/TraeProject/Smart%20Restaurant/sec-frontend/src/stores/user.js)
- state 加 `roleLevel`、`storeId`
- getters 加 `isGeneralManager`、`isStoreManager`、`canManageUsers`、`canManageSystem`
- `setUserInfo(info)` 同步 `roleLevel = info?.roleLevel`、`storeId = info?.storeId`
- `logout` 清空新字段

### 第十步：前端菜单按角色过滤

**修改** [layout/index.vue](file:///d:/TraeProject/Smart%20Restaurant/sec-frontend/src/layout/index.vue)
- 加 `GM_ONLY_PATHS = ['/sysRoles', '/sysPermissions', '/sysRolePermissions', '/stores']`
- 加 `canSeeMenu(path)`：GM_ONLY 路径仅总店长可见
- 用 `computed` 生成 `filteredMenuGroups`，模板里把 `menuGroups` 改为 `filteredMenuGroups`

### 第十一步：前端路由守卫

**修改** [router/index.js](file:///d:/TraeProject/Smart%20Restaurant/sec-frontend/src/router/index.js)
- 给 `/sysRoles`、`/sysPermissions`、`/sysRolePermissions`、`/stores` 4 个路由的 meta 加 `requireGM: true`
- `beforeEach` 守卫扩展：若 `to.meta.requireGM` 且 `!userStore.isGeneralManager` 则 `next('/dashboard')`

### 第十二步：员工列表按权限显隐按钮

**修改** [sysUsers/index.vue](file:///d:/TraeProject/Smart%20Restaurant/sec-frontend/src/views/sysUsers/index.vue)
- 顶部 import `useUserStore`，引入 `canManageUsers` getter
- 新增按钮加 `v-if="canManageUsers"`
- 操作列改为 `v-if="canEdit(row)"` 显隐编辑按钮，`v-if="canDelete(row)"` 显隐删除按钮，都隐藏时显示"仅查看"标签
- `canEdit(row)`：总店长全可；店长仅本门店且 row.roleLevel 严格低于自己；普通员工不可
- 门店筛选项 `v-if="userStore.isGeneralManager"`（非总店长隐藏）
- 表单里"角色ID"输入框改为下拉选择（用 searchOptions.roles），店长新增时禁用总店长/店长选项
- 表单里"所属门店ID"输入框改为下拉选择（用 searchOptions.stores），店长新增时锁定为本门店

## 关键设计要点

1. **不使用 AOP/注解**：直接在 Service 层调用 `PermissionChecker` 方法，便于排查
2. **脱敏而非隐藏**：列表对无权查看的敏感字段置空（password/email/idCard 等），保留基本信息
3. **店长跨门店同级可见**：通过 `includeSameLevelCrossStore` 模式 SQL 实现
4. **自己改自己放行**：`assertCanManageUser` 顶部加 `Objects.equals(me.getUserId(), target.getId())` 短路
5. **禁止删除自己**：`remove` 额外加 `Objects.equals(currentUserId, id)` 校验
6. **防御性校验**：前端隐藏按钮 + 后端 Service 校验双保险
7. **复用现有模式**：BusinessException(403, msg) + GlobalExceptionHandler + Result.error(403, msg)

## 验证方案

### 测试账号（数据库已预置）

| 用户名 | 角色 | level | storeId |
|--------|------|-------|---------|
| 13800000001 | 总店长 | 99 | NULL |
| 13800000002 | 店长张 | 50 | 1 |
| 13800000003 | 店长李 | 50 | 2 |
| 13800000011 | 服务员小李 | 10 | 1 |
| 13800000021 | 收银小赵 | 10 | 1 |

密码统一：`123456`

### 总店长验证（level 99）
- `GET /sysUsers` 返回全部员工，敏感字段可见
- 可 CRUD 任意员工（含其他店长）
- 可访问 `/sysRoles /sysPermissions /sysRolePermissions /stores`
- 前端菜单显示全部 8 个分组

### 店长张验证（level 50, storeId=1）
- `GET /sysUsers` 返回本门店1员工 + 跨门店店长(脱敏)
- `GET /sysUsers/5`（本门店服务员）成功，完整详情
- `GET /sysUsers/3`（其他门店店长）成功，但脱敏
- `GET /sysUsers/1`（总店长）403
- `POST /sysUsers` 店长2失败（不能操作同级）
- `POST /sysUsers` storeId=2 失败（被强制改为1）
- `PUT /sysUsers/5` 改 roleId=2 失败（不能提权到同级）
- `DELETE /sysUsers/3` 403
- `GET /sysRoles` 403
- 前端菜单隐藏 门店管理/角色管理/权限管理/角色权限
- 前端 /sysRoles 路由跳转回 /dashboard
- 列表中 row.id=3,4（跨门店同级）无编辑/删除按钮

### 服务员小李验证（level 10, storeId=1）
- `GET /sysUsers` 仅返回 level=10 员工（跨门店），全部脱敏
- `GET /sysUsers/6`（同级）成功但脱敏
- `GET /sysUsers/2`（店长）403
- `POST/PUT/DELETE /sysUsers` 全部 403
- 前端新增按钮隐藏，所有编辑/删除按钮隐藏，显示"仅查看"

### 回归测试
- 所有5个账号能正常登录
- 个人中心 `/profile` 各角色都能编辑自己信息、修改密码
- 既有 `run_crud_test.js` 用总店长 token 仍通过
- 头像、字符集、登录时间功能不受影响
