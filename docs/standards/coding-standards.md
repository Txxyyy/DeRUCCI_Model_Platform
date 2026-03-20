# 编码规范

本规范分两部分：**工具自动执行**（ESLint / Prettier / google-java-format）和**需人工遵守的架构约定**。

---

## 工具自动执行

### 前端：ESLint 9 + Prettier

**工具链**：ESLint 9 (flat config) + eslint-plugin-vue + eslint-config-prettier + Prettier 3

**配置说明**：
- `eslint.config.js` — ESLint 9 flat config，包含 Vue 和 JS 推荐规则，关闭 `vue/multi-word-component-names`，`no-unused-vars` 降为 warn
- `.prettierrc` — `semi: false`, `singleQuote: true`, `trailingComma: "none"`, `printWidth: 100`
- `.prettierignore` — 忽略 node_modules / dist / *.min.js

**命令**：
```bash
cd frontend
npm install                  # 首次安装依赖
npm run lint                 # ESLint 检查 + 自动修复
npm run format               # Prettier 格式化
npx eslint .                 # 检查（lint 后可加 --fix 自动修复）
npx prettier --check "src/**/*.{vue,js}"  # 检查格式
```

### 后端：google-java-format (fmt-maven-plugin)

**工具**：Spotify fmt-maven-plugin (v2.29)，底层使用 Google Java Format

**命令**：
```bash
cd backend
mvn fmt:format               # 自动格式化所有 Java 文件
mvn fmt:check                # CI 检查（格式不符返回非0）
mvn fmt:check -pl service/device-service   # 单模块检查
```

**注意**：首次执行 `fmt:format` 会改动大量文件，应作为独立 commit。

---

## 需人工遵守的架构约定

### Java 后端

- **Javadoc**：所有类 / 方法 / 字段 / 枚举必须包含中文 Javadoc
- **方法注释**：一句话描述 + `@param`（要具体）+ `@return`
- **命名**：类名大驼峰，方法名小驼峰动词开头，常量全大写下划线
- **包结构**：`com.derucci.iot.{服务名}.{层}`
- **Entity**：Lombok `@Data` + JPA，字段顺序 id → 业务字段 → 状态 → 时间戳
- **Controller**：只做参数校验和转发，业务逻辑放 Service
- **update() 方法**：必须覆盖所有可编辑字段，不能遗漏（feedback 规则）
- **参考范例**：`device-service` 的 `DeviceService.java`、`DeviceController.java`

### Vue 前端

- **组件**：`<script setup>` + Composition API
- **布局**：优先用 flex，不用 `el-row` 栅格（防止按钮换行变形）
- **API 调用**：统一走 `src/api/` 目录，字段命名以后端 Entity 为准
- **权限控制**：路由 meta + `v-permission` 指令
- **状态管理**：Pinia（`src/stores/`）

### API 响应格式

统一使用 `Result<T>` 封装（`common-core`）：
- 成功：`Result.success(data)` → `{code: 200, message: "success", data: ..., timestamp: ...}`
- 分页：`Result.success(data, page, pageSize, total)`
- 错误：`Result.error(code, message)` → 常用 401 / 403 / 404 / 500
