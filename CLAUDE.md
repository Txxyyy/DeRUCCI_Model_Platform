# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## Project Overview

**慕思IoT设备管理平台** - 物联网设备管理平台，用于慕思智能家居产品（智能床垫、智能枕头等）的设备管理和快速接入。

核心功能：产品管理 | 物模型管理（属性+事件+命令） | 设备接入 | OTA升级 | 认证鉴权

## Tech Stack

- **Backend**: Spring Boot 微服务（6 个服务，H2 持久化）
- **Frontend**: Vue 3 + Element Plus + Pinia
- **当前未实现**: Redis / Kafka / API Gateway / Spring Cloud（架构图中有但尚未落地）

## Common Commands

```bash
# Backend（各服务目录下）
mvn spring-boot:run
mvn clean install        # common 模块改动后必须先 install
mvn fmt:format          # 格式化 Java 代码（首次单独 commit）
mvn fmt:check           # CI 格式检查

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

开发流程规范（分级流程 v3.1）详见 `docs/plans/2026-03-20-dev-workflow-optimization-design.md`。

核心原则：**流程服务于质量，不服务于流程本身。轻重匹配，避免形式主义。**

快速入口：用户说"跳过流程" → 允许，commit message 注明。

## 编码约定

> 完整规范（含工具配置）见 `docs/standards/coding-standards.md`。

### Java 后端

- Entity：Lombok `@Data` + JPA，字段顺序 id → 业务字段 → 状态 → 时间戳
- Controller 只做参数校验和转发，业务逻辑放 Service
- 参考范例：`device-service` 的 `DeviceService.java`、`DeviceController.java`

### Vue 前端

- 组件使用 `<script setup>` + Composition API
- 布局优先用 flex，不用 `el-row` 栅格（防止按钮换行变形）
- API 调用统一走 `src/api/` 目录，字段命名以后端 Entity 为准，不自行起别名
- 权限控制：路由 meta + `v-permission` 指令，状态管理：Pinia（`src/stores/`）

### API 响应格式

统一使用 `Result<T>` 封装（`common-core`）：
- 成功：`Result.success(data)` → `{code: 200, message: "success", data: ..., timestamp: ...}`
- 分页：`Result.success(data, page, pageSize, total)`
- 错误：`Result.error(code, message)` → 常用 401 / 403 / 404 / 500
