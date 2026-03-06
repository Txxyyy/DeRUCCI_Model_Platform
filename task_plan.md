# 物模型平台开发任务计划

## 目标
完善PRD文档，并按照TDD范式开发慕思物模型管理平台

## 当前阶段
- [x] 阅读PRD文档
- [x] 阅读UI/UX设计规范
- [x] 了解现有代码结构

## 待完成任务

### Phase 1: PRD完善 (completed)
- [x] 1.1 补充物模型模板详细定义（智能床垫基础模板、进阶模板等）
- [x] 1.2 统一产品品类定义（智能床垫/电动床/智能枕头）
- [x] 1.3 补充PID生成规则和产品型号格式规范
- [x] 1.4 完善数据模型中的字段定义

### Phase 2: 前端UI调整 (completed)
- [x] 2.1 按照PRD调整产品列表页面（品类、通信方式）
- [x] 2.2 按照UI/UX规范调整样式
- [x] 2.3 实现产品创建表单的完整字段
- [ ] 2.4 实现物模型管理界面（待开发）

### Phase 3: 后端TDD开发 (in_progress)
- [x] 3.1 产品管理API TDD开发 (Product实体更新、ProductService更新、PidGeneratorService创建)
- [x] 3.2 物模型模板实体和服务创建 (ThingModelPoint、ThingModelTemplate实体)
- [ ] 3.3 物模型功能点管理API TDD开发

### 完成的代码修改
- PRD完善: 添加物模型模板详细定义 (5个模板的完整属性/事件/命令)
- 前端更新: ProductList.vue按PRD调整
- 后端更新: Product实体增加pid/model/brand字段，ProductService增加PRD验证逻辑
- 物模型服务: ThingModelPoint、ThingModelTemplate实体及服务

### Phase 2: 前端UI调整 (pending)
- [ ] 2.1 按照PRD调整产品列表页面（品类、通信方式）
- [ ] 2.2 按照UI/UX规范调整样式
- [ ] 2.3 实现产品创建表单的完整字段
- [ ] 2.4 实现物模型管理界面

### Phase 3: 后端TDD开发 (pending)
- [ ] 3.1 产品管理API TDD开发
- [ ] 3.2 物模型管理API TDD开发
- [ ] 3.3 物模型模板API TDD开发

### Phase 4: 测试与验证 (pending)
- [ ] 4.1 运行测试验证功能
- [ ] 4.2 对照PRD验收标准验证

## 关键发现
1. PRD已相当完善，但缺少物模型模板的详细定义
2. 现有代码与PRD存在不一致：
   - 产品品类：PRD定义智能床垫/电动床/智能枕头，代码用床垫/枕头/沙发/椅子
   - 通信方式：PRD定义MQTT/BLE，代码有MQTT/CoAP/HTTP
   - 缺少PID、产品型号、品牌等字段
3. 项目使用Spring Cloud微服务 + Vue 3

## 进度日志
| 日期 | 内容 | 状态 |
|-----|------|------|
| 2026-03-05 | 阅读PRD和UI/UX规范 | done |
| 2026-03-05 | 完善PRD - 添加物模型模板详细定义 | done |
| 2026-03-05 | 前端代码调整 - ProductList.vue | done |
| 2026-03-05 | 后端TDD开发 - ProductService更新 | done |
| 2026-03-05 | 后端编译验证通过 | done |
| 2026-03-05 | 物模型服务 - ThingModelPoint、ThingModelTemplate实体 | done |
| 2026-03-05 | 物模型服务 - ThingModelPointService、ThingModelTemplateService | done |
| 2026-03-05 | 物模型Controller - API接口完善 | done |
| 2026-03-05 | PRD完善 v1.1 (按Product Manager Toolkit模板) | done |
| 2026-03-05 | UI设计原型图 | done |
| 2026-03-05 | TDD开发 - 品类物模型页面 | done |
| 2026-03-05 | TDD开发 - 模板应用功能 | done |
| 2026-03-05 | TDD开发 - 侧边栏菜单更新 | done |
| 2026-03-05 | TDD开发 - 产品详情页（含物模型管理） | done |
| 2026-03-05 | TDD开发 - 模板选择功能 | done |
| 2026-03-05 | TDD开发 - 物模型JSON导出功能 | done |
| 2026-03-05 | 编译验证通过 | done |
