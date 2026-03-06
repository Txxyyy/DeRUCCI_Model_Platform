# 慕思IoT设备管理平台

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.x-green" alt="Vue 3">
  <img src="https://img.shields.io/badge/License-MIT-blue" alt="License">
</p>

慕思IoT设备管理平台是一个用于管理智能家居产品（智能床垫、智能枕头等）的物联网设备管理平台，类似于华为DP平台或涂鸦设备开发平台。

## 功能特点

- **产品管理** - 创建、管理产品信息，支持分类和版本管理
- **物模型管理** - 标准物模型（属性+事件+命令），支持模板机制
- **设备接入** - 设备注册、认证、消息路由，支持多种协议
- **OTA管理** - 固件版本管理、升级任务、进度监控

## 技术栈

### 后端
- **框架**: Spring Boot 3.x (微服务架构)
- **服务注册**: Eureka Server
- **API网关**: Zuul Gateway
- **配置中心**: Spring Cloud Config

### 前端
- **框架**: Vue 3
- **状态管理**: Pinia
- **UI组件**: Element Plus
- **构建工具**: Vite
- **HTTP客户端**: Axios

### 基础设施
- **数据库**: MySQL + Redis
- **消息队列**: Kafka/RocketMQ
- **协议**: MQTT, CoAP, HTTP

## 项目结构

```
DeRUCCI_Model_Platform/
├── backend/                     # Spring Boot后端
│   ├── common/common-core/       # 公共模块
│   ├── discovery/eureka-server  # 服务注册 (8761)
│   ├── config/config-server     # 配置中心 (8888)
│   ├── gateway/zuul-gateway     # API网关 (8080)
│   └── service/                 # 业务服务
│       ├── user-service         # 用户服务 (8081)
│       ├── product-service      # 产品服务 (8082)
│       ├── thing-model-service  # 物模型服务 (8083)
│       ├── device-service       # 设备服务 (8084)
│       └── ota-service          # OTA服务 (8085)
├── frontend/                    # Vue 3前端
│   └── src/
│       ├── api/                 # API接口
│       ├── views/               # 页面组件
│       ├── router/              # 路由配置
│       └── stores/              # Pinia状态管理
└── docs/                        # 项目文档
    ├── PRD/                     # 产品需求文档
    ├── API/                     # API接口设计
    ├── Database/                # 数据库设计
    ├── UI/                      # UI原型设计
    └── Architecture/            # 架构设计
```

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+

### 后端启动

```bash
# 构建后端
cd backend
mvn clean install

# 启动服务（按顺序）
# 1. 启动Eureka Server
cd discovery/eureka-server
mvn spring-boot:run

# 2. 启动各个业务服务
cd service/product-service
mvn spring-boot:run
```

### 前端启动

```bash
# 安装依赖
cd frontend
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 服务端口

| 服务 | 端口 |
|-----|------|
| Eureka Server | 8761 |
| API Gateway | 8080 |
| Config Server | 8888 |
| User Service | 8081 |
| Product Service | 8082 |
| Thing Model Service | 8083 |
| Device Service | 8084 |
| OTA Service | 8085 |

## 开发规范

本项目遵循以下开发规范：

- **需求开发流程**: 头脑风暴 → PRD完善 → UI/UX设计 → 开发实现 → 测试验证
- **TDD开发**: 先写测试，再写实现代码
- **代码规范**: 遵循Google Java Style Guide 和 ESLint

详细规范见 [CLAUDE.md](CLAUDE.md)

## 文档

- [产品需求文档 (PRD)](docs/PRD/DeRUCCI_物模型平台%20PRD_v1.1.md)
- [UI设计规范](docs/UI/UI_Design_v1.0.md)
- [API接口设计](docs/API/API接口设计_v1.0.md)
- [数据库设计](docs/Database/数据库设计_v1.0.md)
- [部署架构设计](docs/Architecture/部署架构设计.md)

## 贡献

欢迎提交Issue和Pull Request。

## 许可证

MIT License
