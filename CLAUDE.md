# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**慕思IoT设备管理平台 (DeRUCCI IoT Device Management Platform)** - 物联网设备管理平台，类似于华为DP平台或涂鸦设备开发平台，用于慕思智能家居产品（智能床垫、智能枕头等）的设备管理和快速接入。

## Core Features

1. **产品管理** - 创建、管理产品信息，支持分类和版本管理
2. **物模型管理** - 标准物模型（属性+事件+命令），支持模板机制
3. **设备接入** - 设备注册、认证、消息路由，支持多种协议
4. **OTA管理** - 固件版本管理、升级任务、进度监控

## Tech Stack

- **Backend**: Spring Boot (微服务架构)
- **Frontend**: Vue 3
- **Database**: MySQL + Redis
- **Message Queue**: Kafka/RocketMQ
- **Protocol**: MQTT, CoAP, HTTP

## Common Commands

```bash
# Backend
./mvnw clean install     # 构建后端
./mvnw spring-boot:run  # 启动开发服务器

# Frontend
npm install             # 安装依赖
npm run dev             # 启动开发服务器
npm run build           # 构建生产版本
npm run lint            # 代码检查

# Docker
docker-compose up -d    # 启动所有服务
```

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Web Portal                        │
├─────────────────────────────────────────────────────┤
│  产品管理服务  │  物模型服务  │  设备接入服务  │ OTA服务 │
├─────────────────────────────────────────────────────┤
│              API Gateway / Spring Cloud              │
├─────────────────────────────────────────────────────┤
│    Redis (缓存)    │    Kafka (消息队列)             │
├─────────────────────────────────────────────────────┤
│                 MySQL (持久化)                       │
└─────────────────────────────────────────────────────┘
```

## Key Design Principles

- 物模型采用标准结构：属性(Properties) + 事件(Events) + 命令(Commands)
- 物模型支持模板机制，预置标准模板
- 设备接入层抽象多种协议（MQTT/CoAP/HTTP）
- OTA支持差分升级和回滚机制

## UI/UX Design

请参考以下设计规范:
- `docs/Design/UI_UX设计规范_ProMax.md` - **最终版 (推荐)** - 基于UI/UX Pro Max技能的专业设计规范
- `docs/Design/UI_UX设计规范_Pro.md` - 高级专业版
- `docs/Design/UI_UX设计规范.md` - 基础设计规范

## Documentation Structure

```
docs/
├── PRD/                    # 产品需求文档
├── UI/                     # UI原型设计
├── API/                    # API接口设计
├── Database/               # 数据库设计
├── ThingModel/             # 物模型模板库
├── Architecture/           # 部署架构设计
│   ├── 系统架构图_v1.drawio      # 系统架构图 (可编辑)
│   └── 设备接入架构图_v1.drawio # 设备接入架构图
├── Technical/              # 技术选型说明
├── Security/               # 安全设计文档
├── Test/                   # 测试用例文档
├── Design/                 # UI/UX设计规范
└── TDD/                   # TDD开发规划
    └── TDD开发规划.md     # TDD开发规范和实施计划
```

## Development需求开发 Standards

### 流程 (必须遵循)

本项目要求在实现任何新功能之前，必须按照以下完整流程进行：

```
需求 → 头脑风暴(Brainstorming) → PRD完善 → UI/UX设计 → 开发实现 → 测试验证
```

#### 1. 需求分析阶段 (Brainstorming)
- **必须使用 skill**: `superpowers:brainstorming`
- 在开始任何新功能开发前，必须先使用头脑风暴技能评估需求
- 探索用户意图、需求细节和设计方案
- 产出: 需求分析报告

#### 2. PRD完善阶段
- **必须使用 skill**: `skill-create-prd`
- 根据头脑风暴结果完善产品需求文档
- 包含: 功能点详细描述、字段定义、验证规则、交互流程、API设计
- 产出: 更新的PRD文档

#### 3. UI/UX设计阶段
- **必须使用 skill**: `ui-ux-pro-max`
- 根据PRD设计用户界面
- 产出: UI原型设计文档

#### 4. 开发实现阶段
- **必须遵循**: 严格按照PRD和UI设计文档进行开发
- **必须使用 TDD**: 先写测试，再写实现代码

#### 5. 测试验证阶段
- 运行测试确保功能正确
- 对照PRD验证实现完整性

### TDD开发范式

本项目同时采用 **TDD (Test-Driven Development)** 开发范式：
- 编写测试 → 运行测试失败 → 编写最小代码通过测试 → 重构
- 详细规范见: `docs/TDD/TDD开发规划.md`

## Quick Start (TDD)

```bash
# 1. 编写测试 (RED)
# 在 test/ 目录创建测试文件

# 2. 运行测试确认失败
./mvnw test -Dtest=ProductServiceTest

# 3. 实现代码 (GREEN)
# 编写最小代码通过测试

# 4. 运行测试确认通过
./mvnw test -Dtest=ProductServiceTest

# 5. 重构 (REFACTOR)
# 清理代码，保持测试通过
```

## Current Development Progress

### Frontend - Product List Page (已完成)

**文件**: `frontend/src/views/product/ProductList.vue`

**功能特点**:
- 顶部Tab分类筛选（全部/智能床垫/电动床/智能枕头）
- 右侧筛选器（通信方式、状态、搜索）
- 4列卡片网格布局
- 添加产品卡片固定在第一个位置
- 产品卡片显示：设备图标（SVG）、产品名称、PID、型号、创建时间、通信方式标签、状态标签

**卡片设计**:
- 简洁紧凑布局，带阴影和hover效果
- 顶部：设备类型图标 + 状态标签（MQTT/BLE + 开发中/已发布）
- 中间：产品名称（加粗）
- 底部：元信息列表（PID/型号/创建时间）

**相关文件**:
- `frontend/src/views/product/ProductList.vue` - 产品列表页面
- `frontend/src/api/product.js` - 产品API
- `docs/UI/UI_Design_v1.0.md` - UI设计规范

### Backend - Product Service (已完成)

**相关文件**:
- `backend/service/product-service/.../ProductController.java`
- `backend/service/product-service/.../ProductService.java`
- `backend/service/product-service/.../Product.java`

**产品状态**:
- DEVELOPING (开发中) - 产品创建后初始状态，可编辑
- PUBLISHED (已发布) - 发布后锁定，不可编辑

### Frontend - API配置

**API地址**: `http://localhost:8082` (直连product-service)
- `frontend/src/api/index.js` - axios配置
- `frontend/src/api/product.js` - 产品API接口
