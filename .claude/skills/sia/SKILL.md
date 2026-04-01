# Self-Improving Agent — 阶段性复盘工具

## 定位

将开发经验系统化沉淀到 memory，确保同类问题不重复踩坑。与日常 feedback memory 互补：
- **日常**: 遇到问题随手写 feedback memory（已有机制，保持不变）
- **阶段性**: 功能完成后调用本技能，系统化回顾整个开发过程，批量提炼经验

## 触发场景

- 用户说"复盘"、"总结"、"回顾"时
- L2/L3 功能开发完成后
- 用户主动调用 `self-improving-agent`

## 执行流程（5步）

### Step 1: 回顾

梳理本次开发过程：
- 做了什么（功能清单、改动文件）
- 遇到什么问题（报错、阻塞、返工）
- 走了什么弯路（无效尝试、方向错误）
- 哪些决策是正确的（值得保留的做法）

信息来源：当前对话上下文、git log、git diff。

### Step 2: 提炼

从回顾中提炼可复用规则，按 memory 类型分类：
- **feedback**: 开发方法、工具使用、代码风格等经验教训
- **project**: 项目状态、技术决策、架构变更等事实
- **user**: 用户偏好、工作习惯等个人信息
- **reference**: 外部资源、文档链接等指针

每条规则必须包含：规则本身 + **Why**（原因） + **How to apply**（应用场景）

### Step 3: 查重

读取 memory 索引，避免重复：
```
cat .claude/projects/-Users-tanxiaoyi-AI-Project-DeRUCCI-Model-Platform/memory/MEMORY.md
```
- 已有相同主题 → 更新现有 memory 文件
- 全新主题 → 创建新 memory 文件

### Step 4: 写入

按项目 memory 规范写入文件：

**路径**: `.claude/projects/-Users-tanxiaoyi-AI-Project-DeRUCCI-Model-Platform/memory/`

**文件命名**: `{type}_{topic}.md`，如 `feedback_vite_proxy.md`、`project_auth_rewrite.md`

**文件格式**:
```markdown
---
name: {memory name}
description: {一行描述，用于未来判断相关性}
type: {feedback | project | user | reference}
---

{规则/事实}

**Why:** {原因——通常是踩坑经历或用户偏好}

**How to apply:** {何时/何处应用此规则}
```

写入后更新 `MEMORY.md` 索引，保持索引简洁（< 200行）。

### Step 5: 流程审视

如果提炼的经验涉及开发流程本身：
- 建议更新 `CLAUDE.md` 中的相关流程
- 明确指出建议修改的位置和内容
- 等用户确认后再修改

## 复盘输出模板

大功能（L3）使用完整模板，中等功能（L2）可精简：

```markdown
## 复盘：[功能名称]

### 踩坑清单
| 问题 | 根因 | 解法 | 已写入 memory |
|------|------|------|--------------|
| ... | ... | ... | feedback_xxx.md |

### 技术决策回顾
- 正确的选择：...（为什么对）
- 需要调整的：...（下次怎么做）

### 流程改进建议
- （如有）开发流程本身的优化建议

### 写入 memory 汇总
- 新增: feedback_xxx.md — ...
- 更新: project_xxx.md — ...
```

## 注意事项

- 不记录可从代码/git 推导的信息（代码模式、文件路径、架构细节）
- 不记录临时调试信息，只记录可复用的经验
- 相对日期转绝对日期（如"周四" → "2026-03-05"）
- 保持 MEMORY.md 索引精简，不超过 200 行
