# 任务记忆

> CLAUDE.md 是项目规范，本文档是当前任务上下文。开新会话时优先阅读。

## 进行中

（暂无）

## 已完成

### [已完成] 架构重构：微服务 → 模块化单体

#### 目标
将 6 个微服务合并为模块化单体，支持 PostgreSQL 和 K8s 部署。

#### 进展
- [x] Phase 1：6 个微服务合并为 5 个业务模块 + app 启动模块
- [x] Phase 1：解决 AuthUserEntity / User 同表映射冲突
- [x] Phase 1：JWT whitelist 缩小范围（只放行 login/refresh）
- [x] Phase 1：统一 CORS、GlobalExceptionHandler 到 app 层
- [x] Phase 1：前端 Vite proxy 从 7 条简化为 1 条
- [x] Phase 1：删除 gateway/eureka/config-server 废弃模块
- [x] Phase 2：PostgreSQL 支持（Flyway 迁移脚本 V1-V6 + application-prod.yml）
- [x] Phase 2：Docker（backend/frontend Dockerfile + docker-compose.yml）
- [x] Phase 2：Kubernetes manifests（namespace/configmap/secret/statefulset/deployment/ingress）
- [x] 全量 API 端到端验证（认证/用户/产品/物模型/设备/OTA 全部通过）
- [x] CLAUDE.md 更新（Tech Stack、Commands、强制读取流程规范和编码规范）
- [x] 前端 bug 修复：enumValues.value.forEach 类型错误（对象格式兼容）
- [x] 前端 bug 修复：品类模板范围显示 + 操作列布局

#### 决策记录

**D1**：合并为模块化单体而非保留微服务
- 原因：6 个服务无服务间通信，gateway/eureka/config-server 未启用，微服务只增加复杂度
- 模块边界保持清晰（独立 Maven module + 独立包），未来可按需拆分

**D2**：删除 AuthUserEntity，AuthService 直接使用 User entity
- 原因：两个 entity 映射同一张 t_user 表，合并后 Hibernate 会报错
- UserRepository.findByUsername() 已存在，AuthUserRepository 完全冗余

**D3**：JWT whitelist 从 `/api/auth/**` 缩小为 `/api/auth/login` + `/api/auth/refresh`
- 原因：原白名单太宽，/auth/me 和 /auth/users/*/permissions 被放行后 AuthContext 为 null，导致 401/403
- 这是原微服务架构就存在的隐藏 bug，合并后暴露

**D4**：dev profile 禁用 Flyway
- 原因：Flyway 和 H2 的 ddl-auto:update 冲突，产生循环依赖

#### 约束
- 开发环境用 H2（`application-dev.yml`），生产用 PostgreSQL（`application-prod.yml`）
- 部署前必须修改 `k8s/secret.yml` 和 `.env` 中的占位密码
- common 模块改动后仍需 `mvn install`（模块间依赖未变）

### [已完成] 品类物模型功能

#### 目标
修复产品物模型导入品类模板报错，补齐品类模板发布功能。

#### 进展
- [x] 前端 API 拆分：`thingModel.js` → `productThingModel.js` + `categoryTemplate.js`
- [x] 品类模板 entity 添加 `status` 字段（DRAFT / PUBLISHED / DEPRECATED）
- [x] 后端 `ThingModelTemplateService.publish()` 方法 + `PUT /thing-model/templates/{id}/publish` 接口
- [x] 前端导入流程修复：`getThingModels({ category })` → `getTemplatesByCategory(category)`
- [x] 导入弹窗过滤：只显示 `status === 'PUBLISHED'` 的模板
- [x] CategoryThingModel.vue 添加发布按钮和状态列
- [x] CategoryTemplateEdit.vue 保存/加载 API 修正（createTemplate / updateTemplate / getTemplateById）
- [x] 导入字段映射修复：模板 JSON 结构（`{identifier, dataType: {type, specs}, accessMode}`）到 `ThingModelPoint` 的正确字段映射

#### 决策记录

**D1**：前端 API 文件按领域拆分为 `productThingModel.js` + `categoryTemplate.js`
- 原因：`thingModelApi` 混合了产品物模型和品类模板两套 API，命名耦合导致开发时极易混用接口（品类模板创建页面调用了产品物模型接口）
- 教训：API 文件按领域分离，命名语义对齐后端 Controller，避免"看起来对但实际错"

**D2**：品类模板状态用 DRAFT / PUBLISHED / DEPRECATED 枚举，而非布尔 `isPublished`
- 原因：与 `ThingModel.status` 枚举对齐，扩展性强，未来可支持下架/禁用等状态

**D3**：发布后的模板不可编辑（前端限制，后端 update 方法未处理 status）
- 原因：保持模板只读语义，避免发布后内容不一致

**D4**：品类模板 JSON 结构和 `ThingModelPoint` 字段命名不对齐，导致导入时字段映射完全错误
- 教训1：`makePointJson` 输出的结构（`{identifier, dataType: {type, specs}, accessMode}`）和 `ThingModelPoint`（`{pointId, dataType, access}`）语义相同但命名不同，导入时必须做字段映射
- 教训2：事件/命令的 `dataType` 在模板里是对象 `{type: 'alarm'}`, 后端期望的是字符串 `'alarm'`

#### 待解决
（无）

#### 约束
- 品类模板必须先发布（PUBLISHED）才能被产品导入
- 编辑已发布模板不影响已导入产品的数据（模板 JSON 仅用于导入，不做联动更新）
- 模板 JSON 结构（`makePointJson` 输出）和 `ThingModelPoint` 字段命名不同，导入时必须映射：identifier→pointId、dataType.type→dataType、accessMode→access、specs.unit→unit
