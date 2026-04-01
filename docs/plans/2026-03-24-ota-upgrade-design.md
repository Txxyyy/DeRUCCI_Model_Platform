# OTA 升级方案设计

> 版本: v1.0 | 日期: 2026-03-24 | 状态: 已确认

## 1. 概述

### 1.1 背景

慕思IoT平台已有基础的固件管理和OTA任务CRUD，但缺少设备通信链路、实际固件下载、进度上报等核心能力。本方案定义完整的OTA升级流程，覆盖手动升级、自动升级、循环测试三种模式。

### 1.2 设计决策

| 编号 | 决策 | 理由 |
|------|------|------|
| D1 | 设备级任务模型（非批次+设备两层） | APP驱动单设备升级为主，模型简单直接 |
| D2 | 固件发布不主动推送 | 升级由APP检查更新或设备定时检查触发，平台不主动推送 |
| D3 | 固件存OSS，固定下载链接 | 简单可靠，无需临时Token |
| D4 | SHA256签名验证 | 保证固件完整性，设备下载后校验 |
| D5 | 循环OTA为同版本反复刷 | 验证OTA流程本身的稳定性 |
| D6 | 自动升级时机由设备端判断 | 设备根据使用状态（凌晨/无人使用）自行决定 |
| D7 | 固件四阶段生命周期 | DRAFT→TESTING→PUBLISHED→DEPRECATED，测试阶段精准推送，发布后全量覆盖 |

## 2. 数据模型

### 2.1 Firmware（固件）— 改造现有实体

| 字段 | 类型 | 说明 | 变更 |
|------|------|------|------|
| id | Long | 主键 | 已有 |
| name | String | 固件名称 | 已有 |
| version | String | 版本号（语义化 "1.2.0"） | 已有 |
| versionCode | Integer | 数字版本号（单调递增，用于比对） | **新增** |
| productId | Long | 所属产品 | 已有 |
| fileUrl | String | OSS固定下载链接 | 已有 |
| fileSize | Long | 文件大小(bytes) | 已有 |
| sha256Checksum | String | SHA256签名 | **新增（替代fileMd5）** |
| description | String | 更新日志 | 已有 |
| status | Enum | DRAFT / TESTING / PUBLISHED / DEPRECATED | **修改（新增TESTING）** |
| testDeviceIds | String(JSON) | 测试阶段指定的设备ID列表 | **新增** |
| createTime | Timestamp | 创建时间 | 已有 |
| updateTime | Timestamp | 更新时间 | 已有 |

### 2.2 OtaTask（升级任务）— 重新设计为设备级

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| deviceId | Long | 目标设备 |
| firmwareId | Long | 目标固件 |
| fromVersion | String | 升级前版本 |
| toVersion | String | 目标版本 |
| status | Enum | PENDING / PUSHING / DOWNLOADING / INSTALLING / SUCCESS / FAILED / CANCELED |
| progress | Integer | 0-100 |
| taskType | Enum | MANUAL / AUTO / LOOP_TEST |
| errorMessage | String | 失败原因 |
| createdAt | Timestamp | 创建时间 |
| completedAt | Timestamp | 完成时间 |

### 2.3 OtaLoopConfig（循环测试配置）— 新增

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| deviceId | Long | 测试设备 |
| firmwareId | Long | 反复刷的固件 |
| maxCycles | Integer | 最大循环次数（0=无限） |
| intervalSeconds | Integer | 每轮间隔秒数 |
| currentCycle | Integer | 当前已完成轮次 |
| status | Enum | RUNNING / PAUSED / COMPLETED / STOPPED |
| createdAt | Timestamp | 创建时间 |

循环测试每轮创建一条 `OtaTask`（taskType=LOOP_TEST），平台检测到 SUCCESS 后等待 interval 再创建下一轮。

## 3. 固件生命周期

```
DRAFT（草稿）
  │ 管理员上传固件，填写版本号/更新日志等
  │ 可修改信息、重新上传
  v
TESTING（测试中）
  │ 管理员点"开始测试"，录入测试设备标识
  │ 只有指定的测试设备能检测到此固件
  │ 支持循环OTA测试
  v
PUBLISHED（已发布）
  │ 管理员点"正式发布"
  │ 该产品(PID)下所有设备都可升级
  │ APP检查更新时可见，无需指定设备号
  v
DEPRECATED（已废弃）
  │ 有更新版本发布后，旧版本标记废弃
  │ 不出现在检查更新结果中，历史记录保留
```

## 4. 核心流程

### 4.1 固件发布流程

```
管理员上传固件 → 填写版本号/versionCode/更新日志 → 选择目标产品
    → 平台存储到OSS，记录fileUrl + 计算SHA256
    → 状态 DRAFT

管理员点"开始测试" → 录入测试设备ID → 状态 TESTING
    → 仅指定测试设备可见

管理员点"正式发布" → 状态 PUBLISHED
    → 仅标记可用，不推送任何通知
    → APP下次检查更新时会发现新版本
    → 设备如果开启了自动升级，定时轮询平台检查新版本
```

### 4.2 APP手动升级

```
APP 调用 GET /api/ota/check?deviceId=xxx
    → 平台查设备当前firmwareVersion + 该产品最新可用固件
      - PUBLISHED固件：所有设备可见
      - TESTING固件：仅testDeviceIds中的设备可见
    → 返回 {hasUpdate, firmware: {version, size, changelog}}

用户点"立即升级"
    → APP 调用 POST /api/ota/tasks {deviceId, firmwareId, taskType: MANUAL}
    → 平台创建 OtaTask(PENDING)
    → 平台通过 MQTT /{PID}/{DK}/ota/notify 推送升级指令
    → OtaTask 状态 → PUSHING

设备收到 → HTTP GET 下载固件 → 上报进度 ota/progress
    → 平台更新 OtaTask 的 progress 和 status
    → APP 轮询 GET /api/ota/tasks/{id} 展示进度

设备升级完成 → 上报 {status: SUCCESS, version: "1.2.0"}
    → 平台更新 OtaTask → SUCCESS
    → 更新 Device.firmwareVersion
```

### 4.3 自动升级

```
用户在APP设备设置中开启 autoUpgrade = true
    → APP 调用 PUT /api/devices/{id}/settings {autoUpgrade: true}
    → 平台存储到Device表

设备定时调用平台API检查新版本
    → GET /api/device/ota/check?pid=xxx&dk=xxx&currentVersion=xxx
    → 发现新版本 + autoUpgrade=true → 设备自行判断时机下载升级
    → 升级过程中通过 ota/progress 上报进度
    → 平台收到进度后自动创建 OtaTask(taskType=AUTO) 记录
```

### 4.4 循环OTA测试

```
管理员在平台选择设备 → 开启循环OTA
    → 配置: firmwareId, maxCycles, intervalSeconds
    → 创建 OtaLoopConfig(RUNNING)

循环引擎（平台定时任务）:
  1. 查所有RUNNING的OtaLoopConfig
  2. 检查上一轮OtaTask是否完成
     - SUCCESS → 等待interval → 创建下一轮OtaTask
       → 推送 ota/notify（带 allowReinstall=true，跳过版本比对）
     - FAILED → OtaLoopConfig → STOPPED，记录失败轮次
  3. currentCycle >= maxCycles → OtaLoopConfig → COMPLETED
```

## 5. API 设计

### 5.1 固件管理 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ota/firmwares` | 创建固件（上传到OSS，状态DRAFT） |
| PUT | `/api/ota/firmwares/{id}` | 编辑固件信息（仅DRAFT状态可编辑） |
| POST | `/api/ota/firmwares/{id}/test` | 开始测试（DRAFT→TESTING，需传testDeviceIds） |
| POST | `/api/ota/firmwares/{id}/publish` | 正式发布（TESTING→PUBLISHED） |
| POST | `/api/ota/firmwares/{id}/deprecate` | 废弃（PUBLISHED→DEPRECATED） |
| GET | `/api/ota/firmwares` | 固件列表（支持按产品、状态筛选） |
| GET | `/api/ota/firmwares/{id}` | 固件详情 |
| DELETE | `/api/ota/firmwares/{id}` | 删除固件（仅DRAFT状态可删） |

### 5.2 OTA 升级 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/ota/check?deviceId=xxx` | 检查更新（APP调用） |
| POST | `/api/ota/tasks` | 创建升级任务（APP触发手动升级） |
| GET | `/api/ota/tasks/{id}` | 查询任务状态/进度（APP轮询） |
| GET | `/api/ota/tasks?deviceId=xxx` | 查设备的升级历史 |
| POST | `/api/ota/tasks/{id}/cancel` | 取消升级任务 |

### 5.3 循环测试 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/ota/loop-tests` | 创建循环测试（指定设备、固件、次数、间隔） |
| GET | `/api/ota/loop-tests/{id}` | 查看循环测试状态和每轮结果 |
| POST | `/api/ota/loop-tests/{id}/stop` | 停止循环测试 |
| GET | `/api/ota/loop-tests` | 循环测试列表 |

### 5.4 设备端 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/device/ota/check?pid=xxx&dk=xxx&currentVersion=xxx` | 设备检查更新（自动升级模式用） |
| GET | `/api/ota/firmwares/{id}/download` | 下载固件包（OSS固定链接） |

### 5.5 MQTT 消息

| Topic | 方向 | 触发时机 |
|-------|------|---------|
| `/{PID}/{DK}/ota/notify` | 平台→设备 | APP触发升级 / 循环测试触发 |
| `/{PID}/{DK}/ota/progress` | 设备→平台 | 设备上报下载/安装进度 |

**ota/notify 消息格式：**

```json
{
  "id": "ota_001",
  "version": "1.0",
  "method": "thing.ota.notify",
  "params": {
    "firmwareId": 42,
    "targetVersion": "2.1.0",
    "fileSize": 524288,
    "sha256": "a1b2c3d4...",
    "downloadUrl": "https://oss.example.com/firmware/42.bin",
    "allowReinstall": false
  }
}
```

**ota/progress 消息格式：**

```json
{
  "id": "ota_001",
  "version": "1.0",
  "method": "thing.ota.progress",
  "params": {
    "status": "DOWNLOADING",
    "percent": 65,
    "errorCode": 0
  }
}
```

## 6. 安全设计

| 环节 | 措施 |
|------|------|
| 固件完整性 | 上传时平台计算SHA256存库，设备下载后校验，不匹配则丢弃并上报FAILED |
| 下载鉴权 | 固件下载链接固定（OSS），设备需通过已认证的MQTT连接获取下载地址 |
| 防降级 | 正常模式下versionCode必须大于当前版本；循环测试模式下跳过此检查（allowReinstall=true） |
| 设备端校验 | 设备写入OTA-B分区后校验，失败则不切换分区，保持原版本运行 |

## 7. 错误处理

### 7.1 OTA 失败处理

| 场景 | 设备行为 | 平台行为 |
|------|---------|---------|
| 下载失败（网络中断） | 支持断点续传，重试3次 | OtaTask保持DOWNLOADING |
| SHA256校验失败 | 丢弃固件，上报FAILED | OtaTask → FAILED，记录errorMessage |
| 安装失败（写入异常） | 不切换分区，保持原版本 | OtaTask → FAILED |
| 升级后启动失败 | 自动回滚到OTA-A分区 | 设备上报旧版本，平台检测到版本未变 → FAILED |
| 超时（30分钟无进度） | — | 平台定时任务检测，OtaTask → FAILED |

### 7.2 循环测试异常处理

| 场景 | 处理 |
|------|------|
| 某轮失败 | OtaLoopConfig → STOPPED，记录失败轮次和原因 |
| 设备离线 | 暂停循环，设备重新上线后继续 |
| 管理员手动停止 | OtaLoopConfig → STOPPED |
| 达到maxCycles | OtaLoopConfig → COMPLETED |

## 8. 与现有系统的关系

### 8.1 依赖的已有能力

- **设备认证体系**：一机一密 + MQTT Token（见《设备接入与通信链路设计方案》）
- **MQTT通信链路**：ota/notify 和 ota/progress Topic 已在通信方案中定义
- **设备端SDK**：已预留 `derucci_ota_set_handler` 和 `derucci_ota_report_progress` 接口
- **A/B双分区**：模组Flash已规划OTA-A/OTA-B分区，支持回滚

### 8.2 需要改造的现有代码

| 模块 | 变更 |
|------|------|
| Firmware Entity | 新增 versionCode、sha256Checksum、testDeviceIds；status 新增 TESTING；移除 fileMd5 |
| OtaTask Entity | 重新设计为设备级任务，移除 totalCount/successCount/failCount |
| OtaService | 重写升级逻辑，新增检查更新、循环测试 |
| OtaController | 新增测试/发布/废弃端点，新增检查更新API |
| 数据库迁移 | 新增迁移脚本修改表结构，新增 t_ota_loop_config 表 |
| 前端 | 固件管理页面适配新状态流转，新增循环测试页面 |
