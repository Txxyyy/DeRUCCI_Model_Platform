# 慕思物模型平台 - TDD开发规划

> 基于TDD开发范式的详细实施规划，确保开发过程严格遵循Red-Green-Refactor原则。

---

## 1. 项目架构概述

### 1.1 微服务模块划分

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              项目模块划分                                            │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                     │
│  基础服务 (先开发)                                                                  │
│  ├── user-service          用户中心服务                                             │
│  └── common               公共模块                                                  │
│       ├── common-core     核心公共类                                                │
│       ├── common-redis   Redis封装                                                 │
│       ├── common-security 安全模块                                                  │
│       └── common-swagger Swagger封装                                               │
│                                                                                     │
│  核心业务服务                                                                       │
│  ├── product-service      产品管理服务                                              │
│  ├── thing-model-service 物模型服务                                                 │
│  ├── device-service      设备接入服务                                              │
│  └── ota-service        OTA服务                                                   │
│                                                                                     │
│  支撑服务                                                                            │
│  ├── gateway             API网关                                                   │
│  └── message-service     消息推送服务                                              │
│                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技术栈

| 层级 | 技术 | 版本 |
|-----|------|------|
| 后端 | Spring Boot | 3.2.x |
| 后端 | Spring Cloud | 2023.0.x |
| 前端 | Vue 3 | 3.4.x |
| 前端 | Element Plus | 2.5.x |
| 数据库 | MySQL | 8.0.x |
| 缓存 | Redis | 7.2.x |
| 消息队列 | Kafka | 3.6.x |

---

## 2. TDD开发顺序规划

### 2.1 开发阶段划分

```
阶段一：基础设施 (Week 1-2)
    │
    ├── 1.1 公共模块 (common)
    │   ├── 通用响应封装
    │   ├── 异常处理
    │   ├── 工具类
    │   └── 常量定义
    │
    └── 1.2 用户服务 (user-service)
        ├── 用户登录/登出
        ├── 权限校验
        └── 角色管理

阶段二：核心业务-产品模块 (Week 3)
    │
    ├── 2.1 产品分类管理
    │   ├── 分类列表查询
    │   ├── 创建分类
    │   └── 删除分类
    │
    └── 2.2 产品管理
        ├── 产品CRUD
        ├── 产品搜索
        └── 产品上下架

阶段三：核心业务-物模型模块 (Week 4)
    │
    ├── 3.1 物模型基础
    │   ├── 物模型CRUD
    │   ├── 属性管理
    │   ├── 事件管理
    │   └── 命令管理
    │
    └── 3.2 物模型模板
        ├── 模板列表
        └── 基于模板创建

阶段四：核心业务-设备模块 (Week 5-6)
    │
    ├── 4.1 设备注册
    │   ├── 单个注册
    │   └── 批量导入
    │
    ├── 4.2 设备认证
    │   ├── 密钥生成
    │   └── 认证校验
    │
    └── 4.3 设备管理
        ├── 设备列表
        ├── 设备详情
        └── 设备命令

阶段五：核心业务-OTA模块 (Week 7)
    │
    ├── 5.1 固件管理
    │   ├── 固件上传
    │   └── 固件列表
    │
    └── 5.2 升级任务
        ├── 创建任务
        ├── 任务执行
        └── 进度监控

阶段六：集成与前端 (Week 8-10)
    │
    ├── 6.1 API网关
    │   ├── 路由配置
    │   └── 限流熔断
    │
    └── 6.2 前端开发
        ├── 产品管理页面
        ├── 物模型页面
        ├── 设备管理页面
        └── OTA管理页面
```

---

## 3. TDD测试策略

### 3.1 测试金字塔

```
                    ┌─────────────┐
                    │   E2E测试   │  少量端到端测试
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
    ┌────┴────┐    ┌────┴────┐    ┌────┴────┐
    │集成测试  │    │集成测试  │    │集成测试  │
    │(Service)│    │(Service)│    │(Service)│
    └────┬────┘    └────┬────┘    └────┬────┘
         │                 │                 │
    ┌────┴────┐    ┌────┴────┐    ┌────┴────┐
    │单元测试  │    │单元测试  │    │单元测试  │
    │(Util)   │    │(Entity)│    │(VO/DTO)│
    └─────────┘    └─────────┘    └─────────┘

    大量单元测试    适量集成测试    少量E2E测试
```

### 3.2 测试覆盖率目标

| 层级 | 覆盖率目标 | 说明 |
|-----|-----------|------|
| 工具类 (Util) | 90%+ | 核心逻辑必须覆盖 |
| 实体类 (Entity) | 80%+ | 业务逻辑必须覆盖 |
| 服务类 (Service) | 70%+ | 接口逻辑必须覆盖 |
| Controller | 60%+ | 接口必须覆盖 |
| 整体 | 70%+ | 整体覆盖率要求 |

---

## 4. 详细TDD实施计划

### 阶段一：公共模块 (Common)

#### 1.1 通用响应封装 (Result)

**RED - 编写失败测试**
```java
// test: ResultTest.java
@Test
void should_return_success_when_create_success() {
    // Given
    String data = "test";

    // When
    Result<String> result = Result.success(data);

    // Then
    assertThat(result.getCode()).isEqualTo(200);
    assertThat(result.getData()).isEqualTo(data);
}

@Test
void should_return_error_when_create_error() {
    // Given
    String message = "error message";

    // When
    Result<Void> result = Result.error(message);

    // Then
    assertThat(result.getCode()).isEqualTo(500);
    assertThat(result.getMessage()).isEqualTo(message);
}
```

**GREEN - 最小代码实现**
```java
// 最小实现通过测试
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.data = data;
        return result;
    }

    // ... getters/setters
}
```

**REFACTOR - 重构优化**
- 添加泛型支持
- 添加时间戳
- 完善注释

---

#### 1.2 业务异常类 (BusinessException)

**RED - 编写失败测试**
```java
// test: BusinessExceptionTest.java
@Test
void should_throw_exception_with_code() {
    // Given
    int errorCode = 40001;
    String message = "参数错误";

    // When & Then
    BusinessException exception = assertThrows(
        BusinessException.class,
        () -> { throw new BusinessException(errorCode, message); }
    );

    assertThat(exception.getCode()).isEqualTo(errorCode);
    assertThat(exception.getMessage()).isEqualTo(message);
}
```

---

#### 1.3 工具类测试

| 工具类 | 测试方法 | 优先级 |
|-------|---------|-------|
| StringUtils | isBlank, isNotBlank, trim | P0 |
| DateUtils | format, parse, now | P0 |
| ValidationUtils | isEmail, isPhone, isUrl | P0 |
| Md5Utils | encrypt | P1 |
| JwtUtils | generate, parse, validate | P0 |

---

### 阶段二：产品模块 (Product Service)

#### 2.1 产品实体测试

**RED - 产品实体测试**
```java
// test: ProductTest.java
@Test
void should_create_product_with_required_fields() {
    // Given
    String name = "智能床垫";
    String category = "bed";

    // When
    Product product = Product.builder()
        .name(name)
        .category(category)
        .build();

    // Then
    assertThat(product.getName()).isEqualTo(name);
    assertThat(product.getCategory()).isEqualTo(category);
    assertThat(product.getStatus()).isEqualTo("draft");
}

@Test
void should_fail_when_name_is_blank() {
    // Given
    String name = "";

    // When & Then
    assertThrows(ConstraintViolationException.class, () -> {
        Product product = Product.builder()
            .name(name)
            .category("bed")
            .build();
    });
}
```

#### 2.2 产品服务测试

| 服务方法 | 测试用例 | 优先级 |
|---------|---------|-------|
| createProduct | 成功创建、草稿状态、必填校验 | P0 |
| updateProduct | 成功更新、名称重复、状态校验 | P0 |
| deleteProduct | 草稿删除、已发布下架 | P0 |
| getProductById | 正常查询、不存在返回null | P0 |
| listProducts | 分页、搜索、筛选 | P0 |
| publishProduct | 发布成功、重复发布 | P1 |

#### 2.3 产品Controller测试

```java
// test: ProductControllerTest.java
@Test
void should_return_product_when_create_success() {
    // Given
    ProductDTO dto = ProductDTO.builder()
        .name("智能床垫")
        .category("bed")
        .thingModelId(1L)
        .build();

    // When
    mockMvc.perform(post("/api/v1/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.name").value("智能床垫"))
        .andExpect(jsonPath("$.data.status").value("draft"));
}
```

---

### 阶段三：物模型模块 (ThingModel Service)

#### 3.1 物模型结构测试

**测试结构**
```
ThingModel (物模型)
├── ThingModelProperty (属性)
├── ThingModelEvent (事件)
└── ThingModelCommand (命令)
```

#### 3.2 测试用例矩阵

| 实体 | 测试场景 | 优先级 |
|-----|---------|-------|
| ThingModel | 创建、发布、版本管理 | P0 |
| ThingModelProperty | CRUD、标识符校验、数据类型 | P0 |
| ThingModelEvent | 告警事件、信息事件 | P0 |
| ThingModelCommand | 同步/异步命令 | P1 |
| ThingModelTemplate | 模板列表、基于模板创建 | P1 |

#### 3.3 物模型校验测试

```java
// test: ThingModelValidatorTest.java
@Test
void should_fail_when_property_identifier_duplicate() {
    // Given
    ThingModelProperty property1 = ThingModelProperty.builder()
        .identifier("temperature")
        .name("温度")
        .build();
    ThingModelProperty property2 = ThingModelProperty.builder()
        .identifier("temperature")
        .name("目标温度")
        .build();

    // When & Then
    assertThatThrownBy(() -> validateProperty(property1, property2))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("标识符重复");
}
```

---

### 阶段四：设备模块 (Device Service)

#### 4.1 设备注册测试

| 测试场景 | 描述 | 优先级 |
|---------|------|-------|
| 单个注册成功 | 正常创建设备 | P0 |
| SN码重复 | 唯一性校验 | P0 |
| 产品不存在 | 外键关联校验 | P0 |
| 密钥生成 | 自动生成PK/DK/DS | P0 |
| 批量导入成功 | Excel导入 | P0 |
| 批量导入部分失败 | 错误行处理 | P1 |

#### 4.2 设备认证测试

```java
// test: DeviceAuthTest.java
@Test
void should_authenticate_success_with_correct_keys() {
    // Given
    String productKey = "PKtest";
    String deviceKey = "DKtest";
    String deviceSecret = "DStest";

    // When
    boolean result = deviceAuthService.authenticate(productKey, deviceKey, deviceSecret);

    // Then
    assertThat(result).isTrue();
}

@Test
void should_authenticate_fail_with_wrong_secret() {
    // Given
    String wrongSecret = "wrong";

    // When
    boolean result = deviceAuthService.authenticate("PKtest", "DKtest", wrongSecret);

    // Then
    assertThat(result).isFalse();
}
```

#### 4.3 设备影子测试

```java
// test: DeviceShadowTest.java
@Test
void should_update_shadow_when_device_online() {
    // Given
    Long deviceId = 1L;
    Map<String, Object> properties = Map.of("temperature", 25);

    // When
    deviceShadowService.updateReported(deviceId, properties);

    // Then
    DeviceShadow shadow = deviceShadowService.getByDeviceId(deviceId);
    assertThat(shadow.getReportedProperties()).containsEntry("temperature", 25);
}
```

---

### 阶段五：OTA模块 (OTA Service)

#### 5.1 固件管理测试

| 测试场景 | 优先级 |
|---------|-------|
| 固件上传成功 | P0 |
| 版本号格式校验 | P0 |
| 版本号重复校验 | P0 |
| 固件删除 | P1 |

#### 5.2 升级任务测试

```java
// test: OtaTaskTest.java
@Test
void should_create_task_success() {
    // Given
    CreateTaskRequest request = CreateTaskRequest.builder()
        .name("升级任务")
        .firmwareId(1L)
        .targetType("product")
        .targetProductId(1L)
        .strategyType("immediate")
        .build();

    // When
    OtaTask task = otaTaskService.createTask(request);

    // Then
    assertThat(task.getName()).isEqualTo("升级任务");
    assertThat(task.getStatus()).isEqualTo("pending");
}

@Test
void should_update_progress_when_device_upgraded() {
    // Given
    Long taskId = 1L;
    Long deviceId = 1L;

    // When
    otaTaskService.updateDeviceProgress(taskId, deviceId, "success");

    // Then
    OtaTaskDevice device = otaTaskService.getTaskDevice(taskId, deviceId);
    assertThat(device.getStatus()).isEqualTo("success");
}
```

---

## 5. 测试数据管理

### 5.1 测试数据策略

| 类型 | 策略 | 示例 |
|-----|------|------|
| 公共数据 | @BeforeAll 初始化 | 分类数据 |
| 私有数据 | @BeforeEach 创建 | 产品数据 |
| 外部依赖 | Mock | 第三方服务 |

### 5.2 测试数据Builder

```java
// 测试数据Builder
public class ProductTestDataBuilder {
    public static Product buildDefaultProduct() {
        return Product.builder()
            .name("测试产品")
            .category("bed")
            .status("draft")
            .build();
    }

    public static Product buildPublishedProduct() {
        return Product.builder()
            .name("已发布产品")
            .category("bed")
            .status("published")
            .build();
    }
}
```

---

## 6. TDD开发流程规范

### 6.1 每个任务的TDD流程

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                         TDD开发流程                                         │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  1. 分析需求 → 明确要实现的功能                                              │
│         │                                                                    │
│         ▼                                                                    │
│  2. 编写测试 (RED)                                                         │
│     • 在 test/ 目录编写测试                                                │
│     • 运行测试确认失败                                                     │
│     • 确认失败原因正确                                                     │
│         │                                                                    │
│         ▼                                                                    │
│  3. 实现代码 (GREEN)                                                       │
│     • 最小实现通过测试                                                     │
│     • 不添加额外功能                                                       │
│         │                                                                    │
│         ▼                                                                    │
│  4. 运行测试 (GREEN)                                                       │
│     • 确认测试通过                                                         │
│     • 确认其他测试不被影响                                                 │
│         │                                                                    │
│         ▼                                                                    │
│  5. 重构 (REFACTOR)                                                        │
│     • 清理重复代码                                                        │
│     • 优化命名                                                            │
│     • 保持测试通过                                                        │
│         │                                                                    │
│         ▼                                                                    │
│  6. 提交代码                                                              │
│     • 测试通过                                                             │
│     • 代码规范                                                             │
│     • 无警告                                                              │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 测试命名规范

```java
// 命名格式: should_预期行为_在_条件下
// 示例:
void should_return_product_list_when_query_with_pagination();
void should_create_product_success_when_valid_input();
void should_throw_exception_when_name_is_blank();
void should_return_error_when_product_not_found();
```

---

## 7. 验收标准

### 7.1 开发完成标准

- [ ] 每个业务方法都有对应的单元测试
- [ ] 所有测试在开发前编写并失败过
- [ ] 代码实现后所有测试通过
- [ ] 测试覆盖率达标 (单元70%+)
- [ ] 无代码规范警告
- [ ] 所有集成测试通过

### 7.2 测试质量标准

- [ ] 测试名称清晰描述测试内容
- [ ] 每个测试只测试一个行为
- [ ] 测试使用真实数据而非Mock(除非必要)
- [ ] 测试具有独立性(不依赖执行顺序)
- [ ] 测试具有可读性

---

## 8. 开发检查清单

### 每日开发检查

```
□ 编写测试前先分析需求
□ 测试失败后才编写实现代码
□ 实现代码只满足测试需求
□ 运行所有测试确认无回归
□ 重构后确认测试仍然通过
□ 提交前检查代码规范
```

### 代码审查检查

```
□ 测试覆盖了核心逻辑
□ 测试名称清晰
□ 测试具有独立性
□ 失败消息明确
□ 无硬编码测试数据
```

---

## 9. 实施建议

### 9.1 开发节奏

| 周 | 内容 | 测试数量目标 |
|----|------|------------|
| Week 1 | 公共模块 | 50+ |
| Week 2 | 用户服务 | 30+ |
| Week 3 | 产品模块 | 80+ |
| Week 4 | 物模型模块 | 80+ |
| Week 5-6 | 设备模块 | 100+ |
| Week 7 | OTA模块 | 60+ |
| Week 8-10 | 集成测试 | 50+ |

### 9.2 工具推荐

| 工具 | 用途 |
|-----|------|
| JUnit 5 | 单元测试 |
| Mockito | Mock对象 |
| AssertJ | 断言库 |
| Testcontainers | 数据库测试 |
| Spring Test | 集成测试 |
| MockMvc | Controller测试 |

---

> 本规划严格遵循TDD开发范式，每个功能的开发都需要经历RED(编写失败测试)→GREEN(最小实现)→REFACTOR(重构)的循环过程。
