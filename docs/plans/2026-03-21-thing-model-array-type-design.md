# 物模型 Array 类型支持设计方案

**日期**: 2026-03-21
**状态**: 已批准

---

## 1. 需求概述

为物模型（Thing Model）功能点新增 **Array 数组类型** 支持，允许用户定义基础类型数组（如 `Array<int>`、`Array<string>` 等）。

### 需求确认

- **元素类型**: 基础类型数组（int、float、string、bool）
- **长度约束**: 最大长度（maxLength），可变长度但有上限
- **默认值编辑**: 可视化数组编辑器（动态增删元素）

---

## 2. 数据结构设计

### 2.1 Backend Entity 扩展

**文件**: `backend/service/thing-model-service/src/main/java/com/derucci/iot/thingmodel/entity/ThingModelPoint.java`

新增字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `elementType` | String(20) | 数组元素类型：int/float/string/bool |
| `maxLength` | Integer | 数组最大长度，≥ 1 |

`dataType` 字段新增 `"array"` 到有效类型列表。

### 2.2 VALID_DATA_TYPES 扩展

**文件**: `backend/service/thing-model-service/src/main/java/com/derucci/iot/thingmodel/service/ThingModelPointService.java`

```java
private static final List<String> VALID_DATA_TYPES = List.of("int", "float", "bool", "string", "enum", "array");
```

### 2.3 数据结构示例

```json
{
  "pointId": "heartRateHistory",
  "name": "心率历史",
  "pointType": "PROPERTY",
  "dataType": "array",
  "elementType": "int",
  "maxLength": 100,
  "defaultValue": "[72, 75, 78]",
  "unit": "bpm",
  "description": "最近100条心率记录"
}
```

---

## 3. 后端验证逻辑

### 3.1 Array 类型验证规则

1. **elementType 必填** — 当 dataType 为 array 时，elementType 不能为空
2. **elementType 合法** — 必须在 `["int", "float", "string", "bool"]` 范围内
3. **maxLength 有效** — ≥ 1
4. **defaultValue 格式** — 必须是合法 JSON 数组，且长度 ≤ maxLength

### 3.2 验证方法新增

```java
validateArrayValues()  // 验证 array 类型的专属字段
```

---

## 4. 前端交互设计

### 4.1 类型选择器

- 下拉选项增加 `"array"` 选项
- 选择后动态显示 array 专属配置区

### 4.2 Array 配置区

```
┌─ 元素类型 ─────────┐  ┌─ 最大长度 ────────┐
│ int                │  │ [10]              │
└────────────────────┘  └───────────────────┘

┌─ 默认值（可视化编辑器）─────────────────────┐
│  [72] [75] [78]                        [+] │
│  点击 [+] 添加元素                        [-] │
└─────────────────────────────────────────────┘
```

**交互说明**：

- **elementType 下拉**: int / float / string / bool
- **maxLength 输入**: 正整数，最小 1
- **可视化编辑器**:
  - 初始状态显示一个空输入框
  - 点击 `[+]` 添加元素（最多不超过 maxLength）
  - 点击 `[-]` 删除元素
  - 元素输入框根据 elementType 渲染（int/float 显示数字输入，string 显示文本输入，bool 显示开关）

---

## 5. 涉及文件清单

| 层级 | 文件路径 | 改动内容 |
|------|---------|---------|
| Backend Entity | `thing-model-service/.../entity/ThingModelPoint.java` | + elementType, + maxLength 字段 |
| Backend Service | `thing-model-service/.../service/ThingModelPointService.java` | + array 验证逻辑 |
| Frontend API | `frontend/src/api/thingModel.js` | 无需改动 |
| Frontend Form | `frontend/src/views/thingmodel/ThingModelList.vue` | + array 类型编辑 UI |
| Frontend Category | `frontend/src/views/thingmodel/CategoryThingModel.vue` | + array 类型编辑 UI |

---

## 6. 实现顺序

1. **Backend**: Entity 新增字段 → Service 新增验证逻辑
2. **Frontend**: ThingModelList.vue 新增 array 编辑 UI
3. **Frontend**: CategoryThingModel.vue 新增 array 编辑 UI
4. **验证**: 创建包含 array 类型的物模型点，验证保存/回显/编辑完整流程
