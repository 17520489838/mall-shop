# cokaki 电商系统 🛍️

<div align="center">

**一个功能完整、适合学习与二次开发的 Java 单体电商项目**

用户端 🛒 · 管理后台 📊 · 前后端分离 🔗

</div>

---

## 📖 项目概述

Mall 是一个完整的 B2C 电商系统，采用 **前后端分离** 架构，覆盖电商核心业务链路。前端基于 **Vue 3 + Element Plus** 构建响应式界面，后端基于 **Spring Boot 2.7 + MyBatis-Plus** 提供 RESTful API 服务，搭配 **Redis** 缓存与 **MySQL** 持久化存储。

项目定位为**练手项目**，代码结构清晰，适合：

- 🎓 **初学者**学习全栈开发流程与技术栈整合
- 🔧 **开发者**作为脚手架快速搭建电商类应用
- 🚀 **研究者**理解 RBAC 权限、订单流转、支付集成等业务设计

---

## ✨ 功能亮点

### 🛒 用户端（商城前台）

| 模块 | 功能 |
|------|------|
| 用户体系 | 注册、登录、个人信息编辑、密码修改 |
| 商品浏览 | 分类/品牌筛选、关键词搜索、新品/热销/推荐 |
| 商品详情 | 多规格选择、评价查看、收藏/取消收藏 |
| 购物车 | 增删改查、选中/全选、数量调整 |
| 订单流程 | 创建订单 → 支付 → 发货 → 收货 → 退款 |
| 地址管理 | 多地址维护、默认地址设置 |
| 个人中心 | 收藏夹、订单列表、历史评价 |

### 📊 管理后台

| 模块 | 功能 |
|------|------|
| 仪表盘 | 今日订单/销售额、待办事项、销售趋势图表、热销商品 TOP10 |
| 商品管理 | 商品上下架、新增/编辑/删除、分类与品牌维护 |
| 订单管理 | 订单列表/详情、状态流转、发货操作 |
| 用户管理 | 用户列表搜索、启用/禁用 |
| 权限控制 | RBAC 角色管理、菜单权限分配、按钮级权限标识 |
| 系统配置 | 全局 KV 配置管理 |

### 🛡 权限体系

基于 **RBAC（Role-Based Access Control）** 模型，支持：

- **三类型权限**：目录级、菜单级、按钮级，控制粒度直达页面按钮
- **细粒度控制**：后端 `@PreAuthorize` 注解 + 前端菜单动态渲染
- **角色管理**：超级管理员、运营、客服等多角色，支持按角色分配菜单与权限

---

## 🚀 快速开始

### 前置环境

- **JDK 17**+ & Maven 3.6+
- **Node.js 18**+ & pnpm（或 npm）
- **MySQL 8.0**+ & Redis 6+

### 1️⃣ 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS mall CHARACTER SET utf8mb4"

# 导入表结构与种子数据（含默认管理员、角色、菜单、分类、商品）
mysql -u root -p mall < backend/db/init.sql
```

`init.sql` 已包含完整的建表语句与测试数据，开箱即用。

### 2️⃣ 启动后端

```bash
cd backend

# 修改 application.yml 中的 MySQL 和 Redis 连接信息
# 默认管理员: admin / admin123

mvn spring-boot:run
```

后端默认运行在 **8080 端口**，启动后访问 Swagger 文档：

> http://localhost:8080/swagger-ui/index.html

### 3️⃣ 启动前端

```bash
cd frontend

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

前端默认运行在 **3000 端口**，已配置 Vite 代理转发 `/api` 请求到后端：

> http://localhost:3000

### 4️⃣ Docker Compose 一键部署（可选）

```bash
docker compose up -d
```

一键启动 MySQL 8.0、Redis 6、后端（8080）、前端 Nginx（80）四个服务。

### 🔑 默认账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 商城用户 | 注册获取 | 注册时设置 |
| 超级管理员 | admin | admin123 |

### ⚙️ 环境变量

参考 `.env.example`，主要配置项：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| MYSQL_HOST | MySQL 主机 | localhost |
| MYSQL_DATABASE | 数据库名 | mall |
| REDIS_HOST | Redis 主机 | localhost |
| JWT_SECRET | JWT 密钥 | 自定义 |
| JWT_EXPIRATION | 用户 token 有效期 | 24h |

---

## 📁 目录结构

```
mall-system/
├── backend/                          # 后端 Spring Boot 应用
│   ├── db/                           # 数据库脚本
│   │   ├── init.sql                  #   ️ 建表 + 种子数据
│   │   ├── migration_v2.0~v4.0.sql   #   版本迁移脚本
│   │   └── seed_data.sql             #   补充测试数据
│   ├── src/main/java/com/mall/
│   │   ├── common/                   # 公共模块
│   │   │   ├── config/               #   Spring 配置（Security/Redis/MyBatis-Plus）
│   │   │   ├── constant/             #   常量定义
│   │   │   ├── exception/            #   全局异常处理
│   │   │   ├── result/               #   统一响应封装
│   │   │   ├── security/             #   JWT 认证过滤器链
│   │   │   └── utils/                #   工具类（JWT/Redis/AliyunOSS）
│   │   ├── controller/              #   API 控制器
│   │   │   ├── admin/               #   管理后台接口
│   │   │   └── front/               #   用户端接口
│   │   ├── dao/                     #   MyBatis-Plus 数据访问层
│   │   ├── dto/                     #   数据传输对象
│   │   ├── entity/                  #   数据库实体
│   │   ├── service/                 #   业务逻辑层
│   │   │   └── impl/                #   服务实现
│   │   └── vo/                      #   视图对象
│   └── src/main/resources/
│       └── application.yml          #   主配置文件
│
├── frontend/                         # 前端 Vue 3 应用
│   ├── src/
│   │   ├── api/                     #   Axios API 封装
│   │   ├── layout/                  #   布局组件（用户端/管理端）
│   │   ├── router/                  #   路由配置（含懒加载）
│   │   ├── store/                   #   Pinia 状态管理
│   │   ├── utils/                   #   工具函数
│   │   └── views/                   #   页面组件
│   │       ├── front/               #   商城前台
│   │       │   ├── home/            #     首页
│   │       │   ├── product/         #     商品列表/详情
│   │       │   ├── cart/            #     购物车
│   │       │   ├── order/           #     订单确认/列表/详情/支付
│   │       │   ├── user/            #     个人中心/地址/收藏
│   │       │   └── auth/            #     登录/注册
│   │       └── admin/               #   管理后台
│   │           ├── dashboard/       #     仪表盘
│   │           ├── product/         #     商品管理
│   │           ├── order/           #     订单管理
│   │           ├── user/            #     用户管理
│   │           ├── category/        #     分类管理
│   │           └── system/          #     系统配置
│   └── package.json
│
├── docker-compose.yml                # Docker 编排
└── .env.example                      # 环境变量模板
```

---

## 🖼️ 项目截图

> 将截图文件放入仓库根目录的 `screenshots/` 文件夹后替换下方路径。

![商城首页](screenshots/homepage.png)
*商城首页 — 商品推荐展示、分类导航入口*

![商品详情](screenshots/product-detail.png)
*商品详情页 — 规格选择、评价列表、收藏按钮*

![管理后台仪表盘](screenshots/admin-dashboard.png)
*管理后台 — 数据统计卡片、销售趋势图、热销商品排行*

![管理后台订单管理](screenshots/admin-order.png)
*管理后台 — 订单列表搜索、状态筛选、发货操作*

---

## 🛠 技术栈

### 后端

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 2.7.18 |
| MyBatis-Plus | 3.5.5 |
| MySQL | 8.0 |
| Redis | 6.x |
| Spring Security | 2.7.x |
| JWT (jjwt) | 0.9.1 |
| Redisson | 3.18.0 |
| Swagger / OpenAPI | 1.7.0 |
| Hutool | 5.8.26 |
| Aliyun OSS | 3.17.4 |

### 前端

| 技术 | 版本 |
|------|------|
| Vue | 3.4 |
| Vite | 5.1 |
| TypeScript | 5.4 |
| Element Plus | 2.5 |
| Vue Router | 4.3 |
| Pinia | 2.1 |
| Axios | 1.6 |
| ECharts | 6.0 |
| VueUse | 10.9 |

---

## 🤝 贡献指南

欢迎任何形式的贡献！参与方式：

1. **Fork** 本仓库
2. 创建特性分支：`git checkout -b feat/your-feature`
3. 提交改动：`git commit -am 'feat: add some feature'`
4. 推送到分支：`git push origin feat/your-feature`
5. 提交 **Pull Request**

### 规范建议

- Java 代码参考阿里巴巴开发规范
- 提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范（如 `feat:`、`fix:`、`refactor:`）

---

## 📄 许可证

[MIT](LICENSE)

Copyright (c) 2026-present

---

<div align="center">

  ⭐ 如果这个项目对你有帮助，欢迎 Star 支持！

</div>
