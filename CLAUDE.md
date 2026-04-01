---
model: minimax-cn-coding-plan/MiniMax-M2.7
---

# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project Overview

**慕思IoT设备管理平台** - 物联网设备管理平台，用于慕思智能家居产品（智能床垫、智能枕头等）的设备管理和快速接入。

核心功能：产品管理 | 物模型管理（属性+事件+命令） | 设备接入 | OTA升级 | 认证鉴权

## Tech Stack

- **Backend**: Spring Boot 模块化单体（5 个业务模块 + app 启动模块，H2 开发 / PostgreSQL 生产）
- **Frontend**: Vue 3 + Element Plus + Pinia
- **部署**: Docker Compose（本地）/ Kubernetes（生产），Flyway 管理数据库迁移
- **当前未实现**: Redis / Kafka / MQTT（架构图中有但尚未落地）

## Common Commands

```bash
# Backend（backend/ 目录下）
mvn spring-boot:run -pl app -am   # 启动单体应用（端口 8080）
mvn clean install                  # 全量构建
mvn clean install -DskipTests      # 跳过测试构建
mvn fmt:format                     # 格式化 Java 代码（首次单独 commit）
mvn fmt:check                      # CI 格式检查

# Frontend
cd frontend && npm install
npm run dev              # 端口 3000
npm run build
npm run lint             # ESLint 检查 + 自动修复
npm run format           # Prettier 格式化
```

## Key Info

- **默认账号**: admin / admin123
- **UI/UX 设计规范**: `docs/Design/UI_UX设计规范_ProMax.md`（最终版）

## Development Standards

**每次开始任务前，必须先读取以下文档，再动手：**
- **开发流程规范**：`docs/plans/2026-03-20-dev-workflow-optimization-design.md`（定级、TDD、检查点）

核心原则：**流程服务于质量，不服务于流程本身。轻重匹配，避免形式主义。**

快速入口：用户说"跳过流程" → 允许，commit message 注明。

## 编码约定

**每次开始任务前，必须先读取：`docs/standards/coding-standards.md`**

## 任务记忆

当前任务的进度、决策记录、待解决事项 → 见 [PROJECT_TASKS.md](./PROJECT_TASKS.md)

**每次接手任务时，必须先阅读 PROJECT_TASKS.md 了解当前进展。开发过程中主动维护：做决策时记录理由，完成节点时更新进展，遇到卡点时写入待解决。**
