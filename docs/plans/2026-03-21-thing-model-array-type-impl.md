# 物模型 Array 类型实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为物模型功能点新增 Array 数组类型支持

**Architecture:** 在 ThingModelPoint Entity 新增 elementType + maxLength 字段；后端验证 array 类型专属逻辑；前端在两个页面的属性编辑区增加 array 类型 UI

**Tech Stack:** Spring Boot (Java) / Vue 3 + Element Plus

---

## Task 1: Backend Entity 新增字段

**Files:**
- Modify: `backend/service/thing-model-service/src/main/java/com/derucci/iot/thingmodel/entity/ThingModelPoint.java`

**Step 1: 新增 elementType 字段**

在 `ThingModelPoint.java` 中，注释更新为：
```java
/**
 * 数据类型：int/float/bool/string/enum/array
 */
```

在 `maxLength` 字段后新增 `elementType` 字段：
```java
/**
 * 数组元素类型（array类型使用）：int/float/string/bool
 */
@Column(length = 20)
private String elementType;
```

并在 getter/setter 区域添加：
```java
public String getElementType() { return elementType; }
public void setElementType(String elementType) { this.elementType = elementType; }
```

**Step 2: Commit**

```bash
cd backend/service/thing-model-service && git add src/main/java/com/derucci/iot/thingmodel/entity/ThingModelPoint.java && git commit -m "feat(thing-model): add elementType field for array type support"
```

---

## Task 2: Backend Service 新增验证逻辑

**Files:**
- Modify: `backend/service/thing-model-service/src/main/java/com/derucci/iot/thingmodel/service/ThingModelPointService.java`

**Step 1: VALID_DATA_TYPES 列表加入 "array"**

修改第 23 行：
```java
private static final List<String> VALID_DATA_TYPES = List.of("int", "float", "bool", "string", "enum", "array");
```

新增常量：
```java
private static final List<String> VALID_ELEMENT_TYPES = List.of("int", "float", "string", "bool");
```

**Step 2: 修改 validateDataType 错误提示**

第 211 行错误提示更新为：
```java
throw new IllegalArgumentException("数据类型必须是int/float/bool/string/enum/array");
```

**Step 3: 在 create() 方法中新增 array 类型验证**

在 `// 验证枚举值` 之后、`// 验证物模型内ID唯一` 之前添加：
```java
// 验证数组类型专属字段（array类型时必填）
if ("array".equals(point.getDataType())) {
    validateArrayValues(point);
}
```

**Step 4: 在 update() 方法中新增 array 类型字段处理**

在 `if (updateData.getEnumValuesJson() != null)` 块之后、`if (updateData.getDefaultValue() != null)` 之前添加：
```java
if (updateData.getElementType() != null) {
    if (!updateData.getElementType().isEmpty() && !VALID_ELEMENT_TYPES.contains(updateData.getElementType())) {
        throw new IllegalArgumentException("数组元素类型必须是int/float/string/bool");
    }
    point.setElementType(updateData.getElementType());
}
if (updateData.getMaxLength() != null) {
    if (updateData.getMaxLength() < 1) {
        throw new IllegalArgumentException("数组最大长度必须 >= 1");
    }
    point.setMaxLength(updateData.getMaxLength());
}
```

**Step 5: 新增 validateArrayValues 验证方法**

在 `validateEnumValues` 方法之后添加：
```java
/**
 * 验证数组类型专属字段
 *
 * @param point 待验证的功能点
 */
private void validateArrayValues(ThingModelPoint point) {
    // elementType 必填
    if (point.getElementType() == null || point.getElementType().isEmpty()) {
        throw new IllegalArgumentException("数组类型必须指定元素类型");
    }
    if (!VALID_ELEMENT_TYPES.contains(point.getElementType())) {
        throw new IllegalArgumentException("数组元素类型必须是int/float/string/bool");
    }
    // maxLength 必填且 >= 1
    if (point.getMaxLength() == null) {
        throw new IllegalArgumentException("数组类型必须指定最大长度");
    }
    if (point.getMaxLength() < 1) {
        throw new IllegalArgumentException("数组最大长度必须 >= 1");
    }
    // defaultValue 格式校验（如果提供了）
    if (point.getDefaultValue() != null && !point.getDefaultValue().isEmpty()) {
        try {
            JsonNode node = objectMapper.readTree(point.getDefaultValue());
            if (!node.isArray()) {
                throw new IllegalArgumentException("数组默认值必须是JSON数组格式");
            }
            if (node.size() > point.getMaxLength()) {
                throw new IllegalArgumentException("数组默认值的元素个数不能超过最大长度");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("数组默认值JSON格式错误");
        }
    }
}
```

**Step 6: Commit**

```bash
cd backend/service/thing-model-service && git add src/main/java/com/derucci/iot/thingmodel/service/ThingModelPointService.java && git commit -m "feat(thing-model): add array type validation in ThingModelPointService"
```

---

## Task 3: Frontend CategoryThingModel.vue 新增 array 类型编辑 UI

**Files:**
- Modify: `frontend/src/views/thingmodel/CategoryThingModel.vue`

### 3.1 在属性 table 的数据类型列新增 array 选项

**Step 1: 修改 dataType 下拉，添加 array 选项**

第 123-129 行，当前代码：
```html
<el-select v-model="row.dataType" placeholder="选择" size="small">
  <el-option label="int" value="int" />
  <el-option label="float" value="float" />
  <el-option label="bool" value="bool" />
  <el-option label="string" value="string" />
  <el-option label="enum" value="enum" />
</el-select>
```

修改为：
```html
<el-select v-model="row.dataType" placeholder="选择" size="small" @change="onDataTypeChange($event, row)">
  <el-option label="int" value="int" />
  <el-option label="float" value="float" />
  <el-option label="bool" value="bool" />
  <el-option label="string" value="string" />
  <el-option label="enum" value="enum" />
  <el-option label="array" value="array" />
</el-select>
```

### 3.2 在 table 中新增 array 专属列（elementType 和 maxLength）

**Step 2: 在单位列之后新增一列用于 array 配置**

在第 141-145 行（单位列）之后添加：
```html
<el-table-column label="数组配置" width="120">
  <template #default="{ row }">
    <template v-if="row.dataType === 'array'">
      <div class="array-config">
        <el-select v-model="row.elementType" placeholder="元素类型" size="small" style="width:70px">
          <el-option label="int" value="int" />
          <el-option label="float" value="float" />
          <el-option label="string" value="string" />
          <el-option label="bool" value="bool" />
        </el-select>
        <el-input-number v-model="row.maxLength" :min="1" :max="1000" size="small" style="width:45px" placeholder="长度" />
      </div>
    </template>
    <template v-else>-</template>
  </template>
</el-table-column>
```

### 3.3 新增可视化数组编辑器（点击编辑时弹出）

**Step 3: 在 el-dialog 创建/编辑弹窗中，为 array 类型属性添加配置区域**

在 `CategoryThingModel.vue` 的 `el-tab-pane label="属性"` 部分，在 `el-table` 之后添加 array 编辑辅助区域。由于表格内编辑空间有限，array 类型的额外配置（elementType、maxLength）在第3.2步的列内直接编辑，默认值则通过点击表格行触发编辑弹窗处理。

考虑到前端改动量，**简化方案**：默认值暂时不在 CategoryThingModel 页面编辑（该页主要用于模板配置），array 专属字段在表格列内直接编辑即可。

**Step 4: 添加 onDataTypeChange 方法**

在 `<script setup>` 中添加：
```javascript
const onDataTypeChange = (dataType, row) => {
  if (dataType === 'array') {
    row.elementType = 'int'
    row.maxLength = 10
  } else {
    row.elementType = null
    row.maxLength = null
  }
}
```

**Step 5: 添加 array-config 样式**

在 `<style scoped>` 中添加：
```css
.array-config {
  display: flex;
  gap: 4px;
  align-items: center;
}
```

**Step 6: Commit**

```bash
cd frontend && git add src/views/thingmodel/CategoryThingModel.vue && git commit -m "feat(thing-model): add array type support in CategoryThingModel property table"
```

---

## Task 4: Frontend ThingModelList.vue 新增 array 类型编辑 UI

**Files:**
- Modify: `frontend/src/views/thingmodel/ThingModelList.vue`

### 4.1 修改属性编辑弹窗中的 dataType 下拉

**Step 1: 在 dataType 下拉中添加 array 选项**

第 177-183 行，修改为：
```html
<el-select v-model="propertyForm.dataType" style="width: 100%">
  <el-option label="整数 (int)" value="int" />
  <el-option label="浮点数 (float)" value="float" />
  <el-option label="字符串 (string)" value="string" />
  <el-option label="布尔值 (bool)" value="bool" />
  <el-option label="枚举 (enum)" value="enum" />
  <el-option label="数组 (array)" value="array" />
</el-select>
```

### 4.2 在属性编辑弹窗中新增 array 类型专属表单项

**Step 2: 在数据类型选择之后、读写类型之前，添加 array 配置区**

在第 184 行 `</el-form-item>` 之后、第 185 行 `<el-row :gutter="20">`（最小值/最大值那行）之前，添加：

```html
<!-- Array类型专属配置 -->
<template v-if="propertyForm.dataType === 'array'">
  <el-row :gutter="20">
    <el-col :span="12">
      <el-form-item label="元素类型 *">
        <el-select v-model="propertyForm.elementType" style="width: 100%">
          <el-option label="整数 (int)" value="int" />
          <el-option label="浮点数 (float)" value="float" />
          <el-option label="字符串 (string)" value="string" />
          <el-option label="布尔值 (bool)" value="bool" />
        </el-select>
      </el-form-item>
    </el-col>
    <el-col :span="12">
      <el-form-item label="最大长度 *">
        <el-input-number v-model="propertyForm.maxLength" :min="1" :max="10000" style="width: 100%" />
      </el-form-item>
    </el-col>
  </el-row>
  <el-form-item label="默认值">
    <div class="array-editor">
      <div class="array-items">
        <div v-for="(item, idx) in arrayDefaultItems" :key="idx" class="array-item">
          <el-input
            v-if="propertyForm.elementType === 'string'"
            v-model="arrayDefaultItems[idx]"
            placeholder="请输入"
            size="small"
            style="width: 100px"
          />
          <el-input-number
            v-else-if="propertyForm.elementType === 'int' || propertyForm.elementType === 'float'"
            v-model="arrayDefaultItems[idx]"
            size="small"
            style="width: 100px"
          />
          <el-switch
            v-else-if="propertyForm.elementType === 'bool'"
            v-model="arrayDefaultItems[idx]"
            size="small"
          />
          <el-button
            type="danger"
            link
            size="small"
            @click="removeArrayItem(idx)"
            :disabled="arrayDefaultItems.length <= 0"
          >
            <AppIcon name="minus" :size="12" />
          </el-button>
        </div>
      </div>
      <el-button
        type="primary"
        link
        size="small"
        @click="addArrayItem"
        :disabled="!propertyForm.maxLength || arrayDefaultItems.length >= propertyForm.maxLength"
      >
        <AppIcon name="plus" :size="12" /> 添加元素
      </el-button>
    </div>
  </el-form-item>
</template>
```

### 4.3 修改最小值/最大值行，当 dataType 为 array 时隐藏

**Step 3: 在 range 配置行上添加 v-if**

第 185-196 行，将 `<el-row :gutter="20">` 修改为：
```html
<el-row v-if="propertyForm.dataType !== 'array'" :gutter="20">
```

### 4.4 在 script 中添加 array 编辑相关逻辑

**Step 4: 在 propertyForm 定义之后添加：**

```javascript
const arrayDefaultItems = ref([])

const addArrayItem = () => {
  if (!propertyForm.maxLength || arrayDefaultItems.value.length >= propertyForm.maxLength) return
  if (propertyForm.elementType === 'bool') {
    arrayDefaultItems.value.push(false)
  } else if (propertyForm.elementType === 'int' || propertyForm.elementType === 'float') {
    arrayDefaultItems.value.push(0)
  } else {
    arrayDefaultItems.value.push('')
  }
}

const removeArrayItem = (index) => {
  arrayDefaultItems.value.splice(index, 1)
}
```

**Step 5: 修改 addProperty 方法，重置 array 相关字段：**

```javascript
const addProperty = () => {
  editingPropertyIndex.value = -1
  Object.assign(propertyForm, {
    identifier: '', name: '', dataType: 'int', min: '', max: '',
    access: '可读可写', unit: '', description: '',
    elementType: null, maxLength: null
  })
  arrayDefaultItems.value = []
  propertyDialogVisible.value = true
}
```

**Step 6: 修改 handleEdit 方法，加载 array 相关字段：**

在 `Object.assign(form, {...})` 中的 properties 部分需要解析 array 的 defaultValue（如果存在的话）。

由于 ThingModelList.vue 的 handleEdit 使用的是前端本地 mock 数据结构，其中 range 是字符串格式。需要确保 confirmProperty 中将 arrayDefaultItems 序列化为 JSON 存入属性对象。

**Step 7: 修改 confirmProperty 方法，序列化 array 默认值：**

在 `confirmProperty` 方法中，`range` 处理保持不变，新增：
```javascript
const confirmProperty = () => {
  const range = propertyForm.min && propertyForm.max
    ? `${propertyForm.min}~${propertyForm.max}` : '-'

  const prop = {
    identifier: propertyForm.identifier,
    name: propertyForm.name,
    dataType: propertyForm.dataType,
    access: propertyForm.access,
    unit: propertyForm.unit,
    range,
    description: propertyForm.description
  }

  // array 类型额外字段
  if (propertyForm.dataType === 'array') {
    prop.elementType = propertyForm.elementType
    prop.maxLength = propertyForm.maxLength
    prop.defaultValue = JSON.stringify(arrayDefaultItems.value)
  }

  if (editingPropertyIndex.value >= 0) {
    form.properties[editingPropertyIndex.value] = prop
  } else {
    form.properties.push(prop)
  }
  propertyDialogVisible.value = false
}
```

**Step 8: 修改 handleEdit，加载 array 相关字段到 propertyForm：**

在 `handleEdit` 的 `Object.assign(form, {...})` 之后，由于 properties 数据是本地 mock，需要从 `row` 中提取 `elementType`、`maxLength`，并从 `defaultValue` 反序列化到 `arrayDefaultItems`。

由于 ThingModelList.vue 的 handleEdit 中 properties 是硬编码的 mock 数据，修改不影响实际功能流程。关键是 `confirmProperty` 方法正确地将 array 类型数据序列化到属性对象。

**Step 9: 添加 array-editor 样式**

在 `<style scoped>` 中添加：
```css
.array-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.array-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.array-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
```

**Step 10: Commit**

```bash
cd frontend && git add src/views/thingmodel/ThingModelList.vue && git commit -m "feat(thing-model): add array type editor UI in ThingModelList property dialog"
```

---

## Task 5: 验证

**Step 1: 启动后端服务**

```bash
cd backend/service/thing-model-service && mvn spring-boot:run
```

**Step 2: 启动前端服务**

```bash
cd frontend && npm run dev
```

**Step 3: 手动测试流程**

1. 登录前端，打开"品类标准模板"页面
2. 选择一个品类，点击"编辑"已有模板或新建模板
3. 在属性表格中，找到数据类型列，选择"array"
4. 验证：elementType 和 maxLength 列出现配置项
5. 选择 elementType=string，maxLength=5
6. 保存模板，验证数据正确保存

**Step 4: API 直接测试**

```bash
# 测试创建 array 类型功能点
curl -X POST http://localhost:8080/thing-model/points \
  -H "Content-Type: application/json" \
  --noproxy '*' \
  -d '{
    "thingModelId": 1,
    "pointId": "test_array",
    "name": "测试数组",
    "pointType": "PROPERTY",
    "dataType": "array",
    "elementType": "int",
    "maxLength": 10,
    "defaultValue": "[1,2,3]",
    "access": "readOnly"
  }'
```

预期：返回 200，创建成功

**Step 5: 错误测试**

```bash
# elementType 为空
curl -X POST http://localhost:8080/thing-model/points \
  -H "Content-Type: application/json" \
  --noproxy '*' \
  -d '{
    "thingModelId": 1,
    "pointId": "test_array_err1",
    "name": "测试数组",
    "pointType": "PROPERTY",
    "dataType": "array",
    "maxLength": 10,
    "access": "readOnly"
  }'
```

预期：返回 400，错误信息 "数组类型必须指定元素类型"

```bash
# maxLength 超出
curl -X POST http://localhost:8080/thing-model/points \
  -H "Content-Type: application/json" \
  --noproxy '*' \
  -d '{
    "thingModelId": 1,
    "pointId": "test_array_err2",
    "name": "测试数组",
    "pointType": "PROPERTY",
    "dataType": "array",
    "elementType": "int",
    "maxLength": 5,
    "defaultValue": "[1,2,3,4,5,6,7]",
    "access": "readOnly"
  }'
```

预期：返回 400，错误信息 "数组默认值的元素个数不能超过最大长度"
