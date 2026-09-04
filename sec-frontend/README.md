# 智慧餐厅后台管理系统 - 前端

## 项目简介
智慧餐厅后台管理系统前端，基于 Vue 3 + Element Plus 构建，提供门店、菜品、订单、会员、库存等全量管理功能。

## 技术栈
- **核心框架**：Vue 3.5+
- **构建工具**：Vite 6
- **UI 组件库**：Element Plus 2.9+
- **状态管理**：Pinia 2.3+（支持持久化）
- **路由**：Vue Router 4.5+
- **网络请求**：Axios 1.7+
- **代码规范**：ESLint 9 + Prettier 3

## 环境要求
- Node.js >= 18.0.0（推荐 20+）
- npm >= 9.0.0 或 pnpm >= 8.0.0

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认端口 5173）
npm run dev

# 打包构建
npm run build

# 预览构建产物
npm run preview
```

## 目录结构

```
sec-frontend/
├── public/              # 静态资源
├── src/
│   ├── api/             # 接口请求（43个模块 + request封装）
│   ├── layout/          # 全局布局（侧边栏+顶部栏）
│   ├── router/          # 路由配置
│   ├── stores/          # Pinia 状态管理
│   ├── styles/          # 全局样式
│   ├── views/           # 页面组件（43个业务模块）
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── .editorconfig        # 编辑器配置
├── .eslintrc.js         # ESLint 配置
├── .prettierrc.json     # Prettier 配置
├── vite.config.js       # Vite 配置
└── package.json
```

## 接口代理
开发环境通过 Vite 代理将 `/api` 请求转发到后端 `http://localhost:8080`。

## 贡献指南
1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/xxx`
3. 提交代码：`git commit -m 'feat: xxx'`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

## Git 分支规范
- `main`：主分支，生产环境代码
- `develop`：开发分支
- `feature/*`：特性分支
- `release/*`：发布分支
- `hotfix/*`：热修复分支
