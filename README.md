# 慕思IoT设备管理平台

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.x-brightgreen" alt="Vue 3">
  <img src="https://img.shields.io/badge/Element%20Plus-2.x-409EFF" alt="Element Plus">
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License">
</p>

慕思IoT设备管理平台是一个面向智能家居产品（智能床垫、智能枕头、电动床等）的物联网设备管理平台，类似于华为DP平台或涂鸦设备开发平台，支持产品从创建到设备接入的全生命周期管理。

## 功能特点

- **产品管理** - 创建、管理产品信息，支持品类分类（智能床垫/电动床/智能枕头）和版本管理，产品状态流转（开发中→已发布）
- **物模型管理** - 标准物模型（属性 + 事件 + 命令），支持品类标准模板机制，一键导入功能点，实时 JSON Schema 预览
- **设备接入** - 设备注册、关联产品（继承物模型），支持品类+产品二级联动选择器，自动生成设备密钥
- **设备监控** - 实时属性面板（按传感器/控制/其他分组）、在线状态脉冲动画、告警时间线
- **OTA管理** - 固件版本管理、升级任务创建与进度监控
- **仪表盘** - 实时统计已发布产品数、设备总数、在线率；SVG 环形图；最近活动时间线（支持刷新）

## 技术栈

### 后端
- **框架**: Spring Boot 3.x（微服务架构，5个独立服务）
- **持久化**: H2 File 模式（开发，重启数据不丢失）/ MySQL 8.0（生产），Spring Data JPA
- **API**: RESTful，Spring MVC

### 前端
- **框架**: Vue 3 (Composition API)
- **UI组件**: Element Plus 2.x
- **构建工具**: Vite（含多服务代理配置）
- **HTTP客户端**: Axios

### 基础设施（规划）
- **数据库**: MySQL + Redis
- **消息队列**: Kafka / RocketMQ
- **协议**: MQTT, CoAP, HTTP

## 项目结构

```
DeRUCCI_Model_Platform/
├── backend/                         # Spring Boot 微服务后端
│   ├── common/common-core/          # 公共模块
│   ├── discovery/eureka-server      # 服务注册 (8761)
│   ├── config/config-server         # 配置中心 (8888)
│   ├── gateway/zuul-gateway         # API网关 (8080)
│   └── service/                     # 业务服务
│       ├── user-service             # 用户服务 (8081)
│       ├── product-service          # 产品服务 (8082)
│       ├── thing-model-service      # 物模型服务 (8083)
│       ├── device-service           # 设备服务 (8084)
│       └── ota-service              # OTA服务 (8085)
├── frontend/                        # Vue 3 前端
│   └── src/
│       ├── api/                     # API接口（按服务拆分）
│       ├── components/              # 公共组件（AppIcon、CategoryIcon、EmptyState 等）
│       ├── views/                   # 页面组件
│       │   ├── Dashboard.vue        # 仪表盘
│       │   ├── product/             # 产品管理
│       │   ├── thingmodel/          # 物模型管理
│       │   ├── device/              # 设备管理
│       │   └── ota/                 # OTA管理
│       ├── router/                  # 路由配置
│       └── stores/                  # Pinia 状态管理
└── docs/                            # 项目文档
    ├── PRD/                         # 产品需求文档
    ├── API/                         # API接口设计
    ├── Database/                    # 数据库设计
    ├── UI/                          # UI原型设计
    ├── Architecture/                # 架构设计（含 .drawio 图表）
    ├── Design/                      # UI/UX 设计规范
    └── TDD/                         # TDD 开发规划
```

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Node.js 18+

> 开发环境使用内嵌 H2 File 数据库，无需安装 MySQL/Redis 即可启动，数据持久保存在各服务的 `data/` 目录。

### 后端启动

```bash
# 分别在各业务服务目录下执行（可并行启动）
cd backend/service/product-service      && mvn spring-boot:run   # 端口 8082
cd backend/service/thing-model-service  && mvn spring-boot:run   # 端口 8083
cd backend/service/device-service       && mvn spring-boot:run   # 端口 8084
cd backend/service/ota-service          && mvn spring-boot:run   # 端口 8085
cd backend/service/user-service         && mvn spring-boot:run   # 端口 8081
```

### 前端启动

```bash
cd frontend
npm install
npm run dev   # 访问 http://localhost:3000
```

前端通过 Vite 代理将 API 请求分发至各后端服务，无需网关即可本地开发调试。

## 服务端口

| 服务 | 端口 | 说明 |
|-----|------|------|
| Frontend | 3000 | Vue 3 开发服务器 |
| User Service | 8081 | 用户管理 |
| Product Service | 8082 | 产品 + 品类管理 |
| Thing Model Service | 8083 | 物模型 + 功能点 |
| Device Service | 8084 | 设备注册与管理 |
| OTA Service | 8085 | 固件版本 + 升级任务 |

## 已实现功能（MVP）

| 模块 | 功能 | 状态 |
|------|------|------|
| 仪表盘 | KPI卡片、SVG环形图、最近活动时间线（可刷新）、快速入口 | ✅ |
| 产品管理 | 产品列表（卡片视图）、新建/编辑/删除、状态发布 | ✅ |
| 产品详情 | 基本信息 + 物模型功能点管理 + 品类模板导入 + JSON预览/导出 | ✅ |
| 品类模板 | 品类标准物模型模板展示与一键导入 | ✅ |
| 物模型 | 功能点 CRUD（属性/事件/命令），数据类型卡片选择器，实时 JSON Schema 预览 | ✅ |
| 设备管理 | 设备列表、注册设备（品类→产品二级选择，自动生成密钥）、详情监控 | ✅ |
| 设备监控 | 在线脉冲动画、属性分组展示、告警时间线 | ✅ |
| OTA管理 | 固件版本列表、升级任务列表 | ✅ |
| 数据持久化 | H2 File 模式，重启数据不丢失 | ✅ |

## 开发规范

本项目遵循以下开发流程：

```
需求 → 头脑风暴(Brainstorming) → PRD完善 → UI/UX设计 → TDD开发 → 测试验证
```

- **TDD 开发**: 先写测试，再写实现代码，详见 [TDD开发规划](docs/TDD/TDD开发规划.md)
- **代码规范**: Google Java Style Guide + ESLint
- **提交规范**: Conventional Commits

详细规范见 [CLAUDE.md](CLAUDE.md)

## 文档

- [产品需求文档 (PRD)](docs/PRD/)
- [UI 设计规范](docs/UI/UI_Design_v1.0.md)
- [UI/UX 设计规范（专业版）](docs/Design/UI_UX设计规范_ProMax.md)
- [API 接口设计](docs/API/)
- [数据库设计](docs/Database/)
- [架构设计](docs/Architecture/)

## 许可证

MIT License
