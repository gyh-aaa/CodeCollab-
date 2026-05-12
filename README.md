# CodeCollab 智能项目协作系统

CodeCollab 是一个面向全栈工程师练习的 Vue 3 + Spring Boot 企业级项目，目标是做成轻量版 Jira / 飞书项目 / Trello。

项目会覆盖真实工作中常见的后台系统能力：登录认证、RBAC 权限、组织与项目管理、任务看板、评论附件、实时通知、数据统计、接口文档和 Docker 化部署。

## 技术栈

### 前端

- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios
- ECharts
- VueUse
- Vitest

### 后端

- Java 21
- Spring Boot 3.5.x
- Spring Security
- JWT
- MyBatis-Plus
- MySQL
- Redis
- MinIO
- WebSocket
- OpenAPI / Swagger UI

### 工程化

- Docker Compose
- Maven
- ESLint
- Prettier
- Git

## 目录结构

```text
.
├── backend                 # Spring Boot 后端
├── frontend                # Vue 3 前端
├── docker                  # 容器初始化脚本
├── docs                    # 需求、接口和学习计划
├── docker-compose.yml      # 本地依赖环境
└── README.md
```

## 本地启动

### 0. IDEA 导入方式

推荐用 IDEA 打开项目根目录后，在 Maven 面板点击 Reload All Maven Projects。

如果后端代码仍然大面积标红，右键 [pom.xml](E:/vue_project/VuePractice/pom.xml) 选择 `Add as Maven Project`，或者右键 [backend/pom.xml](E:/vue_project/VuePractice/backend/pom.xml) 选择 `Add as Maven Project`。

项目 JDK 请选择 Java 21。

### 1. 启动基础设施

当前机器还没有检测到 Docker。如果你安装 Docker Desktop 后，可以运行：

```bash
docker compose up -d
```

这会启动：

- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- MinIO: `localhost:9000`
- MinIO Console: `http://localhost:9001`

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

接口文档：

```text
http://localhost:8080/swagger-ui.html
```

如果本机暂时没有安装 Docker / MySQL，可以先使用演示配置启动：

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

`demo` 配置会跳过数据库自动配置，用于先跑通登录、工作台、项目列表和任务看板。

Windows 下也可以直接运行：

```bash
scripts/start-backend-demo.cmd
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

Windows 下也可以直接运行：

```bash
scripts/start-frontend.cmd
```

前端默认地址：

```text
http://localhost:5173
```

## 开发账号

第一阶段使用临时内存账号，后续会替换为数据库用户模块。

```text
用户名：admin
密码：Admin@123456
```

## 阶段目标

第一阶段已经准备：

- 项目需求文档
- 数据库初版模型
- Docker Compose 基础环境
- Spring Boot 基础工程
- JWT 登录骨架
- Vue 3 基础页面和路由

下一阶段开始实现数据库版用户、角色、菜单和权限。
