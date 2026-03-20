# 慕思IoT设备管理平台 PRD

**Date**: 2026-03-20
**Author**: 产品团队
**Status**: 已完成MVP
**Version**: 2.0

---

## 1. Executive Summary

### 1.1 问题陈述

随着慕思智能化战略的推进，越来越多的智能产品（智能床垫、智能枕头、智能睡眠监测设备等）需要接入统一的管理平台。当前的设备接入缺乏标准化管理，导致：
- 每款产品都需要单独开发接入协议
- 设备数据格式不统一，难以统一分析
- 新产品接入周期长，成本高

### 1.2 解决方案

建设一个**通用物模型管理平台**，实现产品的标准化管理和快速接入。平台类比华为DP平台、涂鸦设备开发平台，提供：
- 产品信息的统一管理
- 物模型的标准化定义（由产品经理维护）
- 设备的快速接入与管理
- 固件OTA升级
- 用户与权限管理
- 实时设备监控与仪表盘

### 1.3 业务价值

| 价值 | 说明 |
|-----|------|
| **标准化** | 统一的物模型规范，设备数据格式标准化 |
| **快速接入** | 基于模板快速创建新产品的物模型，缩短接入周期 |
| **统一管理** | 集中管理所有产品、设备、固件版本 |
| **可扩展** | 支持多种协议和设备类型扩展 |
| **实时监控** | 设备状态实时监控，告警及时响应 |
| **OTA升级** | 固件批量升级，进度可视化追踪 |

### 1.4 时间规划

| 阶段 | 周次 | 主要交付物 | 状态 |
|-----|------|-----------|------|
| Phase 1 | 1周 | 项目初始化、技术选型、架构设计 | ✅ 已完成 |
| Phase 2 | 2周 | 产品管理（创建/列表/详情/发布） | ✅ 已完成 |
| Phase 3 | 2周 | 物模型管理（功能点/模板/版本/导出） | ✅ 已完成 |
| Phase 4 | 1周 | 品类管理、物模型模板 | ✅ 已完成 |
| Phase 5 | 2周 | 设备管理（注册/列表/监控/上下线） | ✅ 已完成 |
| Phase 6 | 1周 | OTA管理（固件仓库/升级任务） | ✅ 已完成 |
| Phase 7 | 1周 | 用户管理、认证鉴权 | ✅ 已完成 |
| Phase 8 | 1周 | 仪表盘（KPI卡片/环形图/Timeline） | ✅ 已完成 |
| Phase 9 | 待定 | 真实MQTT设备接入、命令下发对接 | 📋 规划中 |
| Phase 10 | 待定 | 固件文件真实上传存储、批量设备导入 | 📋 规划中 |
| Phase 11 | 待定 | 告警系统对接、设备监控真实数据 | 📋 规划中 |
| Phase 12 | 待定 | 测试优化、Beta发布 | 📋 规划中 |

---

## 2. Problem Definition

### 2.1 当前问题

| 问题 | 影响 | 严重程度 |
|-----|------|---------|
| 每款产品单独开发接入协议 | 开发重复工作 | 高 |
| 设备数据格式不统一 | 数据分析困难 | 高 |
| 新产品接入周期长 | 上市时间延迟 | 高 |
| 缺乏标准物模型模板 | 产品经理无法快速定义 | 中 |
| 设备状态黑盒 | 运维被动响应 | 中 |
| 固件升级手工操作 | 效率低易出错 | 中 |

### 2.2 目标用户

| 用户角色 | 使用场景 | 需求 |
|---------|---------|------|
| **系统管理员** | 全部功能管理、用户权限配置 | 最高权限 |
| **产品经理** | 创建产品、定义物模型、管理产品信息 | **维护品类物模型模板** |
| 研发工程师 | 查看物模型规范，开发设备端固件、APP开发 | 获取物模型JSON |
| 运维人员 | 监控设备状态、管理OTA升级 | 设备管理、固件升级 |
| 测试工程师 | 验证物模型功能、测试设备接入 | 测试用例 |

### 2.3 关键角色说明

**产品经理**是品类物模型模板的维护者：
- 在「品类物模型」页面创建和维护各品类的标准物模型模板
- 创建产品时，选择对应品类的模板
- 模板包含属性、事件、命令的定义

---

## 3. Solution Overview

### 3.1 产品定位

慕思内部使用的物联网设备管理平台，类比：
- 华为DP平台
- 涂鸦设备开发平台

核心能力：
1. 产品管理（创建、列表、详情、发布）
2. 物模型管理（功能点、模板、版本、导出）
3. 设备接入与管理（注册、认证、上下线、监控）
4. OTA管理（固件仓库、升级任务）
5. 用户与权限管理
6. 仪表盘（数据可视化）

### 3.2 MVP已完成功能

**已实现范围**：
- ✅ 产品管理（创建、列表、详情、发布、删除）
- ✅ 物模型管理（功能点CRUD、品类模板、导入、导出JSON、版本管理）
- ✅ 品类管理（创建、列表、编辑、删除）
- ✅ 设备管理（注册、列表、编辑、删除、上下线模拟、设备监控）
- ✅ OTA管理（固件仓库、升级任务创建/监控）
- ✅ 用户管理（用户CRUD、状态管理）
- ✅ 认证鉴权（JWT Token登录）
- ✅ 仪表盘（KPI卡片、设备状态环形图、活动Timeline）
- ✅ 全局异常处理
- ✅ 前后端5个微服务全部打通（H2数据库）

**成功标准**：
- ✅ 产品经理能够创建和维护品类物模型模板
- ✅ 研发工程师能够导出物模型JSON用于开发
- ✅ 产品创建流程在5分钟内完成
- ✅ 设备能够注册并显示在设备列表
- ✅ OTA升级任务能够创建并显示进度

### 3.3 Out of Scope

本次MVP不考虑：
- 设备实际接入（仅物模型定义和模拟数据）
- 设备监控实时数据（WebSocket）
- OTA差分升级
- 多语言支持
- Redis/Kafka/InfluxDB等中间件集成

---

## 4. User Stories & Requirements

### 4.1 用户故事

#### 系统管理员

```
作为系统管理员
我想管理所有用户账号和权限
以便控制不同角色的访问范围

验收标准：
- [ ] 可以查看用户列表
- [ ] 可以创建/编辑/删除用户
- [ ] 可以启用/禁用用户账号
```

#### 产品经理

```
作为产品经理
我想维护品类物模型模板
以便快速创建新产品时选择标准模板

验收标准：
- [ ] 可以创建物模型模板
- [ ] 可以为模板添加属性/事件/命令
- [ ] 可以编辑和删除模板
- [ ] 可以查看模板列表
```

```
作为产品经理
我想创建新产品并选择物模型模板
以便快速定义产品能力

验收标准：
- [ ] 可以填写产品基本信息（名称、型号、品牌、品类）
- [ ] 系统自动生成PID
- [ ] 可以选择通信方式（MQTT/BLE）
- [ ] 可以选择物模型模板
- [ ] 可以查看产品列表
- [ ] 可以发布产品
```

#### 研发工程师

```
作为研发工程师
我想导出物模型JSON
以便开发APP和固件

验收标准：
- [ ] 可以导出物模型JSON文件
- [ ] JSON格式符合标准规范
- [ ] 包含完整的属性、事件、命令定义
```

#### 运维人员

```
作为运维人员
我想管理设备状态和OTA升级
以便确保设备正常运行

验收标准：
- [ ] 可以注册新设备
- [ ] 可以查看设备列表和状态
- [ ] 可以监控设备上下线
- [ ] 可以创建设备OTA升级任务
- [ ] 可以监控升级进度
```

### 4.2 功能需求

#### 4.2.1 产品管理

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| 产品创建 | P0 | ✅ 已完成 | 创建新产品，自动生成PID |
| 产品列表 | P0 | ✅ 已完成 | 卡片展示、筛选、搜索 |
| 产品详情 | P0 | ✅ 已完成 | 查看和编辑产品信息 |
| 产品发布 | P1 | ✅ 已完成 | 发布产品到已发布状态 |
| 产品删除 | P1 | ✅ 已完成 | 仅草稿状态可删除 |
| 品类管理 | P0 | ✅ 已完成 | 品类CRUD |

#### 4.2.2 物模型管理

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| 功能点管理 | P0 | ✅ 已完成 | 添加/编辑/删除属性、事件、命令 |
| 数据类型 | P0 | ✅ 已完成 | 支持int/float/bool/string/enum |
| 取值范围 | P0 | ✅ 已完成 | 数值类型支持min/max |
| 枚举值 | P0 | ✅ 已完成 | enum类型支持枚举选项 |
| 版本管理 | P1 | ✅ 已完成 | 物模型多版本管理 |
| JSON导出 | P0 | ✅ 已完成 | 导出物模型用于开发 |
| 品类模板 | P0 | ✅ 已完成 | 产品经理维护的模板 |

#### 4.2.3 设备管理

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| 设备注册 | P0 | ✅ 已完成 | 注册新设备，自动生成deviceKey |
| 设备列表 | P0 | ✅ 已完成 | 表格展示、筛选、搜索 |
| 设备详情 | P0 | ✅ 已完成 | 查看设备信息和属性 |
| 设备编辑 | P0 | ✅ 已完成 | 编辑设备基本信息 |
| 设备删除 | P1 | ✅ 已完成 | 删除设备 |
| 设备监控 | P1 | ✅ 已完成 | 设备属性实时显示（模拟） |
| 上下线模拟 | P1 | ✅ 已完成 | 模拟设备上下线状态 |

#### 4.2.4 OTA管理

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| 固件列表 | P0 | ✅ 已完成 | 固件仓库，展示版本/状态 |
| 固件详情 | P0 | ✅ 已完成 | 查看固件信息 |
| 固件创建 | P0 | ✅ 已完成 | 创建固件元数据 |
| 固件发布 | P1 | ✅ 已完成 | 发布固件 |
| 升级任务列表 | P0 | ✅ 已完成 | 任务列表、状态筛选 |
| 升级任务创建 | P0 | ✅ 已完成 | 创建OTA升级任务 |
| 升级任务监控 | P0 | ✅ 已完成 | 进度条、成功/失败统计 |

#### 4.2.5 用户与权限

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| 用户列表 | P0 | ✅ 已完成 | 用户CRUD |
| 用户创建 | P0 | ✅ 已完成 | 创建用户 |
| 用户编辑 | P0 | ✅ 已完成 | 编辑用户信息 |
| 用户删除 | P1 | ✅ 已完成 | 删除用户 |
| 用户启用/禁用 | P1 | ✅ 已完成 | 状态管理 |
| 登录认证 | P0 | ✅ 已完成 | JWT Token认证 |

#### 4.2.6 仪表盘

| 功能 | 优先级 | 状态 | 说明 |
|-----|-------|------|------|
| KPI卡片 | P0 | ✅ 已完成 | 产品数/设备数/在线数/任务数 |
| 设备状态分布 | P0 | ✅ 已完成 | 环形图 |
| 活动Timeline | P1 | ✅ 已完成 | 最近活动记录 |

### 4.3 非功能需求

| 需求 | 标准 | 状态 |
|-----|------|------|
| 性能 | 页面加载 < 2秒 | ✅ 已达成 |
| 可用性 | 支持主流浏览器 | ✅ 已达成 |
| 安全性 | 权限控制、数据校验 | ✅ 基础达成 |
| 可扩展性 | 模块化设计 | ✅ 已达成 |

---

## 5. Design & User Experience

### 5.1 页面结构

```
┌─────────────────────────────────────────────────────────────────────┐
│  顶部导航栏 (Header)                                                  │
│  [Logo]  首页  产品管理  物模型  设备管理  OTA升级  用户管理   [用户] │
├────────────┬────────────────────────────────────────────────────────┤
│            │                                                         │
│  左侧菜单   │                    主内容区                            │
│  (Sidebar) │                  (Main Content)                       │
│            │                                                         │
│  - 首页    │                                                         │
│  - 产品管理 │                                                         │
│    - 产品列表                                                         │
│    - 产品详情                                                         │
│    - 品类管理                                                         │
│  - 物模型  │                                                         │
│    - 品类物模型 ⭐(产品经理)                                          │
│    - 我的模型                                                         │
│  - 设备    │                                                         │
│    - 设备列表                                                         │
│    - 设备监控                                                         │
│  - OTA     │                                                         │
│    - 固件列表                                                         │
│    - 升级任务                                                         │
│  - 用户管理 │                                                         │
│            │                                                         │
└────────────┴────────────────────────────────────────────────────────┘
```

### 5.2 核心页面

| 页面 | 状态 | 说明 |
|-----|------|------|
| 登录页 | ✅ 已完成 | JWT认证 |
| 仪表盘 | ✅ 已完成 | KPI卡片、环形图、Timeline |
| 产品列表 | ✅ 已完成 | 卡片展示、筛选（品类、通信方式、状态）、搜索 |
| 产品创建/编辑 | ✅ 已完成 | 表单（名称、型号、品牌、品类、通信方式） |
| 产品详情 | ✅ 已完成 | 基本信息 + 物模型管理面板 |
| 品类管理 | ✅ 已完成 | 品类CRUD |
| 品类物模型 | ✅ 已完成 | 模板列表、创建/编辑模板 |
| 物模型详情 | ✅ 已完成 | 功能点管理（属性/事件/命令 Tab切换） |
| 设备列表 | ✅ 已完成 | 表格展示、筛选（产品、状态）、搜索 |
| 设备详情/监控 | ✅ 已完成 | 设备属性、命令下发（模拟） |
| 固件列表 | ✅ 已完成 | 固件仓库 |
| 升级任务列表 | ✅ 已完成 | 任务列表、进度监控 |
| 用户列表 | ✅ 已完成 | 用户CRUD |

### 5.3 色彩系统

参考 `docs/Design/UI_UX设计规范_ProMax.md`：

| 用途 | 色值 |
|-----|------|
| 主色 | #2563EB |
| 成功 | #10B981 |
| 警告 | #F59E0B |
| 错误 | #EF4444 |
| 文字主色 | #0F172A |
| 背景色 | #F8FAFC |

### 5.4 设备状态色彩

| 状态 | 颜色 |
|-----|------|
| 在线 | #10B981 |
| 离线 | #EF4444 |
| 警告 | #F59E0B |
| 禁用 | #94A3B8 |

---

## 6. Technical Specifications

### 6.1 技术栈

| 层级 | 技术 | 状态 |
|-----|------|------|
| 前端 | Vue 3 + Element Plus + Pinia + Vue Router | ✅ 已使用 |
| 后端 | Spring Boot 3.2 微服务 (6个服务) | ✅ 已实现 |
| 数据库 | H2文件模式（开发环境） | ✅ 已使用 |
| 认证 | JWT Token | ✅ 已实现 |

### 6.2 后端服务架构

| 服务 | 端口 | 核心职责 | 数据库 |
|------|------|----------|--------|
| user-service | 8081 | 用户管理、角色权限 | user-db |
| product-service | 8082 | 产品CRUD、品类管理 | product-db |
| thing-model-service | 8083 | 物模型CRUD、模板管理 | thing-model-db |
| device-service | 8084 | 设备管理、设备监控 | device-db |
| ota-service | 8085 | OTA固件管理、升级任务 | ota-db |
| auth-service | 8086 | 认证鉴权、JWT签发 | user-db（共享） |

### 6.3 数据模型

#### 产品实体 (product-service)

```java
@Entity
@Table(name = "product")
class Product {
    Long id;
    String pid;          // PID_系统生成
    String name;         // 产品名称
    String model;        // 产品型号 DR-M001
    String brand;        // 产品品牌
    String category;     // 品类
    String protocol;     // MQTT/BLE
    ProductStatus status; // DEPLOYING/PUBLISHED
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
```

#### 产品分类 (product-service)

```java
@Entity
@Table(name = "product_category")
class ProductCategory {
    Long id;
    String name;
    String description;
    String icon;
    LocalDateTime createTime;
}
```

#### 物模型 (thing-model-service)

```java
@Entity
@Table(name = "thing_model")
class ThingModel {
    Long id;
    Long productId;
    String version;     // v1.0.0
    ThingModelStatus status; // DRAFT/PUBLISHED
    String propertiesJson;
    String eventsJson;
    String commandsJson;
    String changeLog;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}

@Entity
@Table(name = "thing_model_point")
class ThingModelPoint {
    Long id;
    Long thingModelId;
    String pointId;     // 物模型ID
    String name;
    String pointType;   // PROPERTY/EVENT/COMMAND
    String dataType;    // int/float/bool/string/enum
    String access;      // readOnly/readWrite
    String unit;
    String rangeJson;   // 取值范围
    String enumValuesJson;
    String defaultValue;
    String description;
}

@Entity
@Table(name = "thing_model_template")
class ThingModelTemplate {
    Long id;
    String name;
    String code;
    String category;
    Boolean system;     // false（用户创建）
    String description;
    LocalDateTime createTime;
}
```

#### 设备 (device-service)

```java
@Entity
@Table(name = "device")
class Device {
    Long id;
    String name;
    String deviceKey;   // DK_系统生成
    String serialNumber;
    Long productId;
    String productName;
    Long thingModelId;
    DeviceStatus status; // ONLINE/OFFLINE/DISABLED
    String firmwareVersion;
    String properties;
    LocalDateTime lastOnlineTime;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
```

#### 固件 (ota-service)

```java
@Entity
@Table(name = "firmware")
class Firmware {
    Long id;
    String version;
    Long productId;
    String productName;
    String fileUrl;
    Long fileSize;
    FirmwareStatus status; // DRAFT/PUBLISHED/DEPRECATED
    String changeLog;
    LocalDateTime createTime;
}

@Entity
@Table(name = "ota_task")
class OtaTask {
    Long id;
    String name;
    Long firmwareId;
    String targetVersion;
    Long totalDevices;
    Long successCount;
    Long failCount;
    OtaTaskStatus status; // PENDING/RUNNING/COMPLETED/CANCELLED
    String description;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
```

#### 用户 (user-service)

```java
@Entity
@Table(name = "sys_user")
class User {
    Long id;
    String username;
    String password;
    String name;
    String email;
    String phone;
    UserStatus status;  // NORMAL/DISABLED
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
```

### 6.4 API端点概览

| 端点前缀 | 服务 | 核心功能 |
|---------|------|----------|
| /api/users | user-service | 用户CRUD |
| /api/products | product-service | 产品CRUD |
| /api/categories | product-service | 品类CRUD |
| /api/thing-models | thing-model-service | 物模型CRUD |
| /api/thing-model | thing-model-service | 模板管理 |
| /api/devices | device-service | 设备CRUD |
| /api/ota | ota-service | OTA管理 |
| /api/auth | auth-service | 登录认证 |

---

## 7. Known Limitations

### 7.1 MVP阶段限制

| 项目 | 当前状态 | 生产方案 |
|------|----------|----------|
| 数据库 | H2文件模式 | 切换MySQL集群 |
| 设备命令下发 | setTimeout模拟 | 对接EMQX MQTT |
| 固件上传 | 仅元数据 | 对接MinIO存储 |
| 设备监控 | 模拟数据 | 对接Kafka + InfluxDB |
| 用户权限 | 简单角色 | RBAC完整实现 |
| 缓存 | 无 | Redis缓存 |
| 消息队列 | 无 | Kafka |

---

## 8. Future Roadmap

### Phase 9: 真实设备接入
- MQTT Broker (EMQX) 集成
- 真实设备三元组认证
- 真实命令下发与响应

### Phase 10: 文件存储与批量操作
- MinIO文件存储集成
- 固件文件真实上传
- Excel批量设备导入

### Phase 11: 实时数据与告警
- Kafka消息队列集成
- InfluxDB时序数据存储
- WebSocket实时推送
- 告警规则引擎

### Phase 12: 生产化
- MySQL数据库迁移
- Docker/K8s部署
- CI/CD流水线
- 自动化测试覆盖

---

## 9. Risks & Mitigations

| 风险 | 概率 | 影响 | 缓解策略 |
|-----|------|------|----------|
| 需求变更 | 高 | 中 | 敏捷迭代，定期确认 |
| 技术难度 | 中 | 中 | 技术预研 |
| 资源不足 | 中 | 高 | 预留buffer |
| 设备接入兼容性 | 中 | 高 | 提前与硬件团队对接 |

---

*最后更新: 2026-03-20*
