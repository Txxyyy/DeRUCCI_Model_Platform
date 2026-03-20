# 开发流程规范 v3.1

> 核心原则：**流程服务于质量，不服务于流程本身。轻重匹配，避免形式主义。**

---

## 1. 定级（每次需求的第一步）

收到需求后，先评估再动手：

| 维度 | L1 小改动 | L2 中等功能 | L3 大功能 |
|------|----------|------------|----------|
| 改动文件数 | 1-2 个 | 3-8 个 | 8+ 个 |
| 跨前后端 | 否 | 可能 | 通常是 |
| 跨服务 | 否 | 可能 1-2 个 | 2+ 服务 |
| 新增页面/模块 | 否 | 否 | 是 |
| 数据库变更 | 否 | 可能加字段 | 新增表/改结构 |
| TDD | 不强制（Service 层 bug 除外） | **必须** | **必须** |
| 典型场景 | 样式/文案/单字段改名 | 弹窗/API对接/权限修复 | 新模块/新服务/架构变更 |

**硬性规则**:
- 前后端联动 → 至少 L2 + TDD
- 权限/安全相关 → 至少 L2 + TDD
- 跨 2+ 服务 → 至少 L2（可能 L3）

**升级机制**: 开发中发现复杂度超预期 → 暂停，升级等级，只补缺失步骤

**用户快捷通道**: 用户明确说"跳过流程/直接改"→ 允许，但在 commit message 中注明跳过了哪些步骤

**多需求处理**: 多需求同时到达 → 各自定级，L1 先清掉；紧急 bug 可打断 L2/L3（暂存进度后处理）

**执行**: 判定等级 + 一句话理由 → 告知用户 → 确认后执行

---

## 2. 分级流程

### L1: 纯小改动（单端、1-2 文件）

```
systematic-debugging（bug时）→ 修复 → simplify → verification → 踩坑时：sia + memory
```

L1 bug 涉及 Service 层 → 补回归测试。纯样式/文案豁免。

### L2: 中等功能

```
brainstorming → RED → GREEN → REFACTOR(simplify)
→ web-design-guidelines（前端时）→ verification → commit
→ sia 复盘 → memory 写入
```

**brainstorming 交付物**（≤3 文件可直接写在对话里，>3 文件写 plan 文件）:
- 影响范围：后端文件 + 前端文件 + 需验证的已有流程
- 验收标准：列出"做完后什么条件算通过"（RED 阶段直接从这里推导测试用例）
- 风险点 + 相关 feedback 检查

**管道回退**: 后续步骤发现前序有问题 → 回退更新交付物（标注原因），不从头重走

### L3: 大功能

```
brainstorming → PRD(skill-create-prd) → UI/UX(ui-ux-pro-max) → writing-plans
→ RED → 并行开发(GREEN) → code-review + simplify(REFACTOR)
→ web-design-guidelines（前端时）→ verification → commit
→ sia 完整复盘 → memory 写入
```

**对话切分建议**: PRD+设计 → 实现+测试 → review+验证，每段开始从 plan 文件恢复上下文

---

## 3. TDD 规范

**自动化测试（JUnit）vs 手动验证（curl）是两回事，都要做**:
- TDD 阶段写 JUnit 测试 → RED/GREEN/REFACTOR 循环
- verification 阶段做 curl 端到端验证 → 确认真实环境可用

**必须写测试的场景**: Service 层逻辑 | Controller 权限/参数校验 | L1 Service 层 bug

**每个测试必须覆盖**: 正常路径 + 权限边界（ADMIN/USER/未登录401）+ 异常路径 + 回归保护（mvn test 全绿）

**RGR 节奏**: 一个循环只聚焦一个功能点。RED(1-3 用例) → GREEN(最小实现) → REFACTOR(消除重复) → commit

**测试基础设施**: 项目首次执行 TDD 时，先搭建测试配置（test application.yml、测试基类、mock 数据工具），作为 L2/L3 的前置步骤

**自审偏差缓解**: AI 审自己的代码有认知盲区（same-model coupling），缓解方式：
- simplify/code-review 时强制对照 P0 检查清单逐项过，不做泛泛审查
- L3 的 code-review 在新对话中执行（干净上下文，减少确认偏误）

---

## 4. 检查点

P0 = 每次必查（对应高频 BUG），P1 = 按场景触发。

**编码前**:
- `[P0]` 读取后端 Entity 字段，确认前端字段名对齐
- `[P0]` 读取相关 feedback memory
- `[P1]` 改 common 模块 → 先 mvn install

**编码中**:
- `[P0]` update() 覆盖所有前端可编辑字段
- `[P0]` 新增接口或改动 ≥3 字段 → 写字段映射表（单字段改动在 commit message 注明）
- `[P0]` Controller 端点有 @RequirePermission
- `[P1]` 前端按钮有 v-permission
- `[P1]` 每完成独立模块就 commit

**编码后**:
- `[P0]` 重启受影响的后端服务（必须在 curl 验证之前）
- `[P0]` curl 验证所有受影响流程（增/改/删/查）
- `[P0]` mvn test 全绿
- `[P0]` 所有角色验证（ADMIN + USER + 未登录 401）
- `[P1]` simplify 检查代码质量

**回滚**: verification 多处失败且修复成本高 → git revert 到稳定 commit → 重新分析根因 → 重走 RGR

---

## 5. 复盘与 Memory

| 等级 | 触发 | 范围 |
|------|------|------|
| L1 | 踩坑时 | 根因 + 遗漏检查 |
| L2 | 必选 | 方案合理性 + TDD 覆盖 + 意外问题 |
| L3 | 必选 | 架构决策 + 跨服务经验 + 流程偏差 |

**Memory 写入时机**: commit 后复盘时统一写入。开发中先在 plan 文件标 `[MEMORY]`，复盘时判断是否沉淀。
**例外**: 用户纠正做法 → 立即写入，不等复盘。
**写入原则**: 只记非显而易见的经验，含 Why + How to apply，先查重再写。

---

## 6. 流程瘦身

- 连续 5 次未触发的 P1 → 降级为"仅相关场景检查"
- 连续 3 次 L2 复盘无意外 → 复盘简化为一句话
- P0 不参与瘦身
- 已降级项再次出问题 → 立即恢复，重置计数

---

## 7. 基础设施要求

以下为流程正常运转的前提，属一次性搭建：
- **pre-commit hook**: 提交前自动跑 `mvn test`，测试不过不允许提交
- **测试基础设施**: test application.yml、测试基类、mock 工具（首次 TDD 时搭建）

---

*v3.1 (2026-03-20): 新增验收标准作为 brainstorming 必要产出（spec→test→code 闭环）、同模型自审偏差缓解措施、pre-commit hook 基础设施要求。*
