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

## Development Standards - 分级开发流程

根据需求规模选择对应流程，避免小改动被重流程拖慢。

### Level 1: 小改动（Bug 修复、样式调整、字段改名）

```
直接修复 → verification-before-completion
```

- 不需要设计流程，改完验证即可
- Bug 优先使用 `systematic-debugging` 技能排查根因
- 改完用 `simplify` 快速审查代码质量（可选）

### Level 2: 中等功能（新增弹窗、加筛选器、对接 API）

```
brainstorming（轻量，理清思路）→ 开发实现 → simplify → verification-before-completion
```

- 使用 `brainstorming` 技能快速梳理需求和方案
- 跳过 PRD 和 UI/UX 设计，brainstorming 产出足够指导开发
- TDD 可选，视复杂度决定
- 改完用 `simplify` 审查代码复用性和质量

### Level 3: 大功能（新模块、架构变更、跨多服务改动）

```
brainstorming → skill-create-prd → ui-ux-pro-max → writing-plans → 并行开发 → code-review → verification
```

- **需求分析**: 使用 `brainstorming` 技能深度探索需求
- **PRD**: 使用 `skill-create-prd` 输出完整需求文档
- **UI/UX**: 使用 `ui-ux-pro-max` 设计界面（涉及前端时）
- **规划**: 使用 `writing-plans` 拆分实施步骤
- **并行开发**: 使用 `dispatching-parallel-agents` 或 `subagent-driven-development` 并行执行独立任务
- **审查**: 使用 `requesting-code-review` 检查实现质量
- **验证**: 使用 `verification-before-completion` 确认通过
- **提交**: 使用 `create-pr` 创建 Pull Request

### 等级判定标准

| 维度 | L1 小改动 | L2 中等功能 | L3 大功能 |
|------|----------|------------|----------|
| 改动文件数 | 1-2 个 | 3-5 个 | 6+ 个 |
| 是否跨服务 | 否 | 通常不跨 | 跨 2+ 服务 |
| 是否新增页面/模块 | 否 | 否（在已有页面内改） | 是 |
| 是否涉及数据库变更 | 否 | 可能加字段 | 新增表/改表结构 |
| 是否影响已有功能 | 不影响 | 局部影响 | 广泛影响 |
| 典型场景 | bug 修复、样式调整、文案改动、字段改名 | 新增弹窗/筛选器、对接单个 API、表单增强 | 新模块、架构变更、新服务、跨服务联调 |

**执行方式**: 每次收到需求时：
1. 快速评估以上维度
2. 给出等级建议 + 一句话理由
3. 用户确认后按对应流程执行

### 通用原则

- 流程服务于效率，按需裁剪，不教条执行
- 截图分析技能（`screenshot-*`）用于逆向还原已有系统，不是常规开发流程
- `simplify` 每次改完代码建议跑一遍，能抓到冗余和质量问题
- TDD 适用于后端服务和核心逻辑，前端 UI 调整不强制
- 详细 TDD 规范见: `docs/TDD/TDD开发规划.md`

## Current Development Progress

### Frontend - Product List Page (已完成) [2026-03-06]

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
