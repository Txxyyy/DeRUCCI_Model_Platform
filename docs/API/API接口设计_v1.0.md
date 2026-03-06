# 慕思物模型平台 - API接口设计规范

## 1. API设计原则

### 1.1 通用约定

- **协议**: HTTPS
- **Base URL**: `https://api.derucci-iot.com/v1`
- **认证**: Bearer Token (JWT)
- **Content-Type**: `application/json`
- **编码**: UTF-8
- **时间格式**: ISO 8601 (e.g., `2024-01-15T14:30:00Z`)

### 1.2 响应格式

**成功响应**
```json
{
  "code": 0,
  "message": "success",
  "data": { ... },
  "timestamp": "2024-01-15T14:30:00Z"
}
```

**错误响应**
```json
{
  "code": 40001,
  "message": "参数错误",
  "details": { ... },
  "timestamp": "2024-01-15T14:30:00Z"
}
```

### 1.3 分页格式
```json
{
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "total": 100,
    "totalPages": 5
  }
}
```

---

## 2. 产品管理模块 API

### 2.1 产品列表

**GET** `/products`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认20 |
| category | string | 否 | 产品分类筛选 |
| status | string | 否 | 状态: draft/published |
| keyword | string | 否 | 搜索关键词 |

**Response**
```json
{
  "code": 0,
  "data": {
    "items": [
      {
        "id": "prod_001",
        "name": "智能床垫",
        "category": "bed",
        "description": "慕思智能床垫",
        "images": ["https://..."],
        "thingModelId": "tm_001",
        "thingModelName": "智能床垫基础模板",
        "deviceCount": 50,
        "status": "published",
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-15T00:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100
    }
  }
}
```

### 2.2 创建产品

**POST** `/products`

**Request Body**
```json
{
  "name": "智能床垫",
  "category": "bed",
  "description": "慕思智能床垫",
  "images": ["https://..."],
  "thingModelId": "tm_001",
  "firmwareId": "fw_001",
  "remark": "备注信息"
}
```

**Response**
```json
{
  "code": 0,
  "data": {
    "id": "prod_001",
    "name": "智能床垫",
    "status": "draft",
    "createdAt": "2024-01-15T14:30:00Z"
  }
}
```

### 2.3 获取产品详情

**GET** `/products/{id}`

**Response**
```json
{
  "code": 0,
  "data": {
    "id": "prod_001",
    "name": "智能床垫",
    "category": "bed",
    "description": "慕思智能床垫",
    "images": ["https://..."],
    "thingModelId": "tm_001",
    "thingModel": { ... },
    "firmwareId": "fw_001",
    "firmware": { ... },
    "deviceCount": 50,
    "status": "published",
    "remark": "备注",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T00:00:00Z"
  }
}
```

### 2.4 更新产品

**PUT** `/products/{id}`

**Request Body**
```json
{
  "name": "智能床垫 Pro",
  "category": "bed",
  "description": "慕思智能床垫Pro版",
  "images": ["https://..."],
  "thingModelId": "tm_002",
  "firmwareId": "fw_002",
  "remark": "备注"
}
```

### 2.5 发布产品

**POST** `/products/{id}/publish`

**Response**
```json
{
  "code": 0,
  "message": "产品已发布"
}
```

### 2.6 下架产品

**POST** `/products/{id}/unpublish`

### 2.7 删除产品

**DELETE** `/products/{id}`

---

## 3. 物模型管理模块 API

### 3.1 物模型列表

**GET** `/thing-models`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| type | string | 否 | 类型: custom/template |
| keyword | string | 否 | 搜索关键词 |

### 3.2 获取物模型详情

**GET** `/thing-models/{id}`

**Response**
```json
{
  "code": 0,
  "data": {
    "id": "tm_001",
    "name": "智能床垫基础模板",
    "version": "1.0.0",
    "description": "适用于智能床垫的基础物模型",
    "type": "template",
    "properties": [
      {
        "id": "prop_001",
        "identifier": "current_temperature",
        "name": "当前温度",
        "dataType": "int",
        "valueRange": { "min": -40, "max": 80 },
        "unit": "℃",
        "accessMode": "readOnly"
      }
    ],
    "events": [
      {
        "id": "event_001",
        "identifier": "temperature_alarm",
        "name": "温度告警",
        "eventType": "alarm",
        "outputData": [
          { "identifier": "temperature", "name": "温度", "dataType": "int" },
          { "identifier": "threshold", "name": "阈值", "dataType": "int" }
        ]
      }
    ],
    "commands": [
      {
        "id": "cmd_001",
        "identifier": "setTemperature",
        "name": "设置温度",
        "inputData": [
          { "identifier": "temperature", "name": "温度", "dataType": "int", "required": true }
        ],
        "invokeMode": "sync"
      }
    ],
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T00:00:00Z"
  }
}
```

### 3.3 创建物模型

**POST** `/thing-models`

**Request Body**
```json
{
  "name": "智能床垫基础模板",
  "description": "适用于智能床垫的基础物模型",
  "type": "custom",
  "properties": [
    {
      "identifier": "current_temperature",
      "name": "当前温度",
      "dataType": "int",
      "valueRange": { "min": -40, "max": 80 },
      "unit": "℃",
      "accessMode": "readOnly",
      "defaultValue": 25
    }
  ],
  "events": [ ... ],
  "commands": [ ... ]
}
```

### 3.4 更新物模型

**PUT** `/thing-models/{id}`

### 3.5 发布物模型

**POST** `/thing-models/{id}/publish`

### 3.6 物模型版本列表

**GET** `/thing-models/{id}/versions`

### 3.7 获取物模型版本详情

**GET** `/thing-models/{id}/versions/{version}`

### 3.8 基于模板创建物模型

**POST** `/thing-models/from-template`

**Request Body**
```json
{
  "templateId": "tm_template_001",
  "name": "我的智能床垫物模型",
  "properties": [ ... ],  // 可选自定义属性
  "events": [ ... ],
  "commands": [ ... ]
}
```

---

## 4. 设备管理模块 API

### 4.1 设备列表

**GET** `/devices`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| productId | string | 否 | 产品ID |
| status | string | 否 | 状态: normal/abnormal |
| onlineStatus | string | 否 | 在线: online/offline |
| keyword | string | 否 | 搜索设备名/SN |

### 4.2 设备详情

**GET** `/devices/{id}`

**Response**
```json
{
  "code": 0,
  "data": {
    "id": "dev_001",
    "name": "卧室床垫-01",
    "productId": "prod_001",
    "productName": "智能床垫",
    "sn": "SN123456789",
    "onlineStatus": "online",
    "status": "normal",
    "firmwareVersion": "v1.2.3",
    "lastOnlineTime": "2024-01-15T14:30:00Z",
    "deviceSecret": "******",
    "createdAt": "2024-01-01T00:00:00Z",
    "properties": {
      "current_temperature": 25,
      "target_temperature": 26,
      "hardness_level": 5
    }
  }
}
```

### 4.3 注册设备

**POST** `/devices`

**Request Body**
```json
{
  "name": "卧室床垫-01",
  "productId": "prod_001",
  "sn": "SN123456789",
  "remark": "备注"
}
```

### 4.4 批量注册设备

**POST** `/devices/batch`

**Request Body**
```json
{
  "devices": [
    { "name": "卧室床垫-01", "productId": "prod_001", "sn": "SN001" },
    { "name": "卧室床垫-02", "productId": "prod_001", "sn": "SN002" }
  ]
}
```

### 4.5 更新设备

**PUT** `/devices/{id}`

### 4.6 删除设备

**DELETE** `/devices/{id}`

### 4.7 获取设备密钥

**GET** `/devices/{id}/credentials`

### 4.8 重置设备密钥

**POST** `/devices/{id}/reset-secret`

### 4.9 获取设备属性

**GET** `/devices/{id}/properties`

### 4.10 更新设备属性

**PUT** `/devices/{id}/properties`

**Request Body**
```json
{
  "properties": {
    "target_temperature": 26,
    "sleep_mode": "deep"
  }
}
```

### 4.11 获取设备日志

**GET** `/devices/{id}/logs`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| type | string | 否 | 日志类型: property/event/command |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 4.12 发送设备命令

**POST** `/devices/{id}/commands`

**Request Body**
```json
{
  "command": "setTemperature",
  "params": { "temperature": 26 }
}
```

**Response**
```json
{
  "code": 0,
  "data": {
    "commandId": "cmd_exec_001",
    "status": "pending"
  }
}
```

### 4.13 命令执行结果

**GET** `/device-commands/{commandId}`

---

## 5. OTA管理模块 API

### 5.1 固件列表

**GET** `/firmwares`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| productId | string | 否 | 产品ID |
| status | string | 否 | 状态: testing/stable/deprecated |
| keyword | string | 否 | 搜索版本号 |

### 5.2 固件详情

**GET** `/firmwares/{id}`

### 5.3 上传固件

**POST** `/firmwares/upload`

**Request (multipart/form-data)**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| file | file | 是 | 固件文件 |
| productId | string | 是 | 产品ID |
| version | string | 是 | 版本号 |
| status | string | 是 | 状态: testing/stable |
| changelog | string | 否 | 变更说明 |

### 5.4 删除固件

**DELETE** `/firmwares/{id}`

### 5.5 升级任务列表

**GET** `/ota/tasks`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| productId | string | 否 | 产品ID |
| status | string | 否 | 状态: pending/running/paused/completed/failed |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |

### 5.6 创建升级任务

**POST** `/ota/tasks`

**Request Body**
```json
{
  "name": "智能床垫v1.2.3版本升级",
  "firmwareId": "fw_001",
  "targetType": "product",
  "targetProductId": "prod_001",
  "strategy": {
    "type": "batch",
    "batchSize": 5,
    "interval": 10
  },
  "description": "本次升级优化了温度传感器精度"
}
```

**targetType 说明**
- `product`: 整个产品
- `devices`: 指定设备列表

**strategy.type 说明**
- `immediate`: 立即升级
- `timed`: 定时升级 (需指定 startTime)
- `batch`: 分批升级
- `cautious`: 谨慎升级

### 5.7 升级任务详情

**GET** `/ota/tasks/{id}`

**Response**
```json
{
  "code": 0,
  "data": {
    "id": "task_001",
    "name": "智能床垫v1.2.3版本升级",
    "firmwareId": "fw_001",
    "firmwareVersion": "v1.2.3",
    "targetType": "product",
    "targetProductId": "prod_001",
    "targetDeviceCount": 50,
    "strategy": { "type": "batch", "batchSize": 5, "interval": 10 },
    "status": "running",
    "progress": {
      "total": 50,
      "success": 15,
      "failed": 5,
      "pending": 25,
      "running": 5,
      "percentage": 30
    },
    "failedDevices": [
      { "deviceId": "dev_005", "reason": "固件下载失败", "failedAt": "2024-01-15T14:30:00Z" }
    ],
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T14:30:00Z"
  }
}
```

### 5.8 暂停升级任务

**POST** `/ota/tasks/{id}/pause`

### 5.9 恢复升级任务

**POST** `/ota/tasks/{id}/resume`

### 5.10 取消升级任务

**POST** `/ota/tasks/{id}/cancel`

### 5.11 回滚升级任务

**POST** `/ota/tasks/{id}/rollback`

### 5.12 获取升级任务设备列表

**GET** `/ota/tasks/{id}/devices`

**Query Parameters**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| status | string | 否 | 状态: success/failed/pending/running |

---

## 6. 公共接口 API

### 6.1 产品分类列表

**GET** `/categories`

**Response**
```json
{
  "code": 0,
  "data": [
    { "id": "bed", "name": "智能床垫", "icon": "bed" },
    { "id": "pillow", "name": "智能枕头", "icon": "pillow" },
    { "id": "monitor", "name": "监测设备", "icon": "monitor" },
    { "id": "other", "name": "其他", "icon": "other" }
  ]
}
```

### 6.2 物模型模板列表

**GET** `/thing-model-templates`

### 6.3 数据类型枚举

**GET** `/enums/data-types`

**Response**
```json
{
  "code": 0,
  "data": {
    "dataTypes": [
      { "value": "int", "label": "整数" },
      { "value": "float", "label": "浮点数" },
      { "value": "bool", "label": "布尔" },
      { "value": "string", "label": "字符串" },
      { "value": "enum", "label": "枚举" },
      { "value": "array", "label": "数组" },
      { "value": "object", "label": "对象" }
    ],
    "accessModes": [
      { "value": "readOnly", "label": "只读" },
      { "value": "writeOnly", "label": "可写" },
      { "value": "readWrite", "label": "可读可写" }
    ],
    "invokeModes": [
      { "value": "sync", "label": "同步" },
      { "value": "async", "label": "异步" }
    ],
    "eventTypes": [
      { "value": "alarm", "label": "告警" },
      { "value": "info", "label": "信息" },
      { "value": "diagnostic", "label": "诊断" }
    ]
  }
}
```

### 6.4 上传文件

**POST** `/files/upload`

**Request (multipart/form-data)**
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| file | file | 是 | 文件 |
| type | string | 是 | 类型: image/firmware |

---

## 7. 错误码定义

| 错误码 | 说明 |
|--------|------|
| 40001 | 参数错误 |
| 40002 | 缺少必填参数 |
| 40003 | 参数格式错误 |
| 40101 | 未授权 |
| 40102 | Token过期 |
| 40301 | 无权限访问 |
| 40401 | 资源不存在 |
| 40901 | 资源已存在 |
| 50001 | 服务器内部错误 |
| 50002 | 服务暂不可用 |

---

## 8. WebSocket 推送

### 8.1 设备状态变更

**Topic**: `/devices/{deviceId}/status`

```json
{
  "type": "status_change",
  "deviceId": "dev_001",
  "onlineStatus": "online",
  "timestamp": "2024-01-15T14:30:00Z"
}
```

### 8.2 设备属性上报

**Topic**: `/devices/{deviceId}/properties`

```json
{
  "type": "property_report",
  "deviceId": "dev_001",
  "properties": {
    "current_temperature": 25
  },
  "timestamp": "2024-01-15T14:30:00Z"
}
```

### 8.3 设备事件上报

**Topic**: `/devices/{deviceId}/events`

```json
{
  "type": "event_report",
  "deviceId": "dev_001",
  "event": {
    "identifier": "temperature_alarm",
    "name": "温度告警",
    "outputData": {
      "temperature": 85,
      "threshold": 80
    }
  },
  "timestamp": "2024-01-15T14:30:00Z"
}
```

### 8.4 OTA升级进度

**Topic**: `/ota/tasks/{taskId}/progress`

```json
{
  "type": "ota_progress",
  "taskId": "task_001",
  "progress": {
    "success": 16,
    "failed": 5,
    "pending": 24,
    "running": 5
  },
  "timestamp": "2024-01-15T14:30:00Z"
}
```
