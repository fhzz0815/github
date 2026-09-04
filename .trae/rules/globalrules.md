---
alwaysApply: true
---
项目总体开发规则：

1. 整体架构模式：
- 整个项目必须严格遵循前后端分离的开发模式。
- 前端与后端通过 RESTful API 接口进行 JSON 数据交互，禁止在后端渲染页面。

2. 前端技术栈规范：
- 核心框架：必须采用 Vue 3.0 进行单页面应用（SPA）开发。
- 状态管理：必须使用 Pinia 进行全局状态管理。
- 路由管理：必须使用 Vue Router 进行页面路由控制。
- 网络请求：必须使用 Axios 进行前后端接口通信。
- UI 组件：必须配合 Vue 3.0 相关的配套 UI 组件库进行页面渲染。

3. 后端技术栈规范：
- 核心框架：必须采用 Spring Boot 4.1.0 版本构建后端服务。
- 持久层框架：必须配合 MyBatis 框架进行数据库操作与数据持久化。
- 数据库连接池：必须采用 Druid 作为数据库连接池，利用其内置功能进行 SQL 监控与性能分析。
- 日志管理：必须采用 Logback 作为默认日志框架，配合 SLF4J 门面进行日志输出，实现控制台与文件的分级管理。
- 消息队列：必须使用 Spring Boot Starter AMQP 集成 RabbitMQ，实现异步消息处理。
- 接口文档：必须采用 SpringDoc OpenAPI 3 技术栈（引入 springdoc-openapi-starter-webmvc-ui 依赖），全面替代已淘汰的 SpringFox，用于自动生成在线 API 文档。

4. 数据库与缓存规范：
- 关系型数据库：必须采用 MySQL 作为核心数据存储数据库。
- 缓存与分布式：必须采用 Redis 数据库配合 Redisson 框架来实现缓存管理及分布式功能。

5. 构建与依赖管理规范 (Maven)：
- 构建工具：必须使用 Maven 进行项目构建和依赖管理。
- Java 版本：项目必须使用 Java 21 进行编译和运行。
- 编码规范：项目源代码必须使用 UTF-8 编码。
- 依赖管理：
    - 必须通过 properties 标签统一管理关键依赖的版本号。
    - 必须使用 mybatis-spring-boot-starter 集成 MyBatis。
    - 必须使用 pagehelper-spring-boot-starter 作为 MyBatis 的分页插件。
    - 必须使用 druid-spring-boot-4-starter 集成 Druid 连接池。
    - 必须使用 redisson-spring-boot-starter 集成 Redisson。
    - 必须使用 lombok 简化实体类代码。
    - 必须使用 hutool-all 作为通用工具类库。
    - 必须使用 guava 作为辅助工具类库。
- 编译插件：必须配置 maven-compiler-plugin 插件，并开启 parameters 参数，以保留方法参数名，便于 Spring MVC 进行参数绑定。

6. 包命名规范：
- 包名必须采用反向域名 + 项目名称的结构，项目名称作为包名的第3层。
- 以当前项目为例，基础包名必须以 com.iwe3.sec 开头（com 为顶级域，iwe3 为组织/公司，sec 为项目名称）。
- 包名必须全部使用小写英文字母，禁止使用大写字母、空格、连字符或下划线。
- 后续层级根据架构分层命名，例如：com.iwe3.sec.controller、com.iwe3.sec.service、com.iwe3.sec.mapper。
- 主启动类命名：主启动类的名称必须采用“项目名称 + Application”的格式（例如：SecApplication），且必须放置在基础包（com.iwe3.sec）的根目录下。