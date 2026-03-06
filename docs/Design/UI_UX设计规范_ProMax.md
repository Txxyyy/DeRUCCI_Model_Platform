# 慕思物模型平台 - 专业UI/UX设计规范

> 基于UI/UX Pro Max技能生成的专业设计系统，结合大厂管理平台最佳实践。

---

## 1. 设计系统总结

### 1.1 推荐设计模式

| 模式 | 名称 | 适用场景 |
|-----|------|---------|
| **Data-Dense Dashboard** | 数据密集型仪表盘 | 设备监控、数据展示 |
| **Enterprise Gateway** | 企业级入口 | 登录、导航、权限 |
| **Bento Grid** | 卡片网格 | 产品概览、功能模块 |

### 1.2 推荐设计风格

| 风格 | 关键词 | 特点 |
|-----|-------|------|
| **Minimalism & Swiss Style** | 简洁、功能性强、网格布局 | 企业应用首选 |
| **Data-Dense Dashboard** | 数据表格、KPI卡片、紧凑间距 | 运营仪表盘 |
| **Soft UI Evolution** | 柔和阴影、对比度改进 | 现代企业应用 |

---

## 2. 专业色彩系统

### 2.1 推荐色板

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           推荐色彩方案                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  品牌主色 (Trust Blue):                                                 │
│  ├── Primary:     #2563EB (蓝-600)                                    │
│  ├── Primary-Light:  #3B82F6 (蓝-500)                                  │
│  └── Primary-Dark:  #1D4ED8 (蓝-700)                                   │
│                                                                      │
│  功能色:                                                                │
│  ├── 成功:    #10B981 (绿-500)                                        │
│  ├── 警告:    #F59E0B (琥珀-500)                                      │
│  ├── 错误:    #EF4444 (红-500)                                        │
│  └── 信息:    #0EA5E9 (天蓝-500)                                      │
│                                                                      │
│  中性色:                                                                │
│  ├── 标题:    #0F172A (slate-900)                                     │
│  ├── 正文:    #334155 (slate-700)                                      │
│  ├── 次要:    #64748B (slate-500)                                     │
│  ├── 禁用:    #94A3B8 (slate-400)                                     │
│  └── 边框:    #E2E8F0 (slate-200)                                     │
│                                                                      │
│  背景:                                                                  │
│  ├── 页面:    #F8FAFC (slate-50)                                      │
│  ├── 卡片:    #FFFFFF                                                 │
│  └── 深色:    #1E293B (slate-800)                                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 设备状态色彩

```
在线状态:   #10B981  背景: #ECFDF5  (绿-50)
离线状态:   #EF4444  背景: #FEF2F2  (红-50)
警告状态:   #F59E0B  背景: #FEF3C7  (琥珀-50)
未知状态:   #94A3B8  背景: #F8FAFC  (slate-50)
```

---

## 3. 专业字体系统

### 3.1 推荐字体

| 用途 | 字体 | 字重 | 字号 |
|-----|------|-----|------|
| **标题/Code** | Fira Code | 400-700 | 24-32px |
| **正文** | Fira Sans / Inter | 300-600 | 14-16px |
| **数据** | JetBrains Mono | 400-500 | 12-14px |
| **中文** | 思源黑体 / PingFang | 400-600 | 14-16px |

### 3.2 Google Fonts 引用

```css
@import url('https://fonts.googleapis.com/css2?family=Fira+Code:wght@400;500;600;700&family=Fira+Sans:wght@300;400;500;600;700&display=swap');

--font-heading: 'Fira Code', monospace;
--font-body: 'Fira Sans', sans-serif;
--font-mono: 'JetBrains Mono', monospace;
```

---

## 4. 核心组件规范

### 4.1 侧边栏导航

```css
/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background: #1E293B;  /* 深色背景 */
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
}

/* 菜单项 */
.menu-item {
  height: 40px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 150ms ease-out;
}

.menu-item:hover {
  background: rgba(255,255,255,0.08);
}

.menu-item.active {
  background: #2563EB;
  color: white;
}
```

### 4.2 数据表格

```css
/* 表格容器 */
.table-container {
  overflow-x: auto;
}

/* 表头 */
.table-header {
  background: #F8FAFC;
  font-weight: 600;
  color: #64748B;
  height: 44px;
}

/* 表格行 */
.table-row {
  height: 52px;
  border-bottom: 1px solid #F1F5F9;
}

.table-row:hover {
  background: #F8FAFC;
}

/* 斑马纹 (可选) */
.table-row:nth-child(even) {
  background: #FAFBFC;
}
```

### 4.3 卡片组件

```css
/* 基础卡片 */
.card {
  background: #FFFFFF;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  transition: all 200ms ease-out;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* KPI卡片 */
.kpi-card {
  background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%);
  color: white;
  border-radius: 16px;
  padding: 24px;
}
```

### 4.4 表单组件

```css
/* 输入框 */
.input {
  height: 40px;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  padding: 0 16px;
  font-size: 14px;
  transition: all 150ms ease-out;
}

.input:focus {
  border-color: #2563EB;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
  outline: none;
}

.input:disabled {
  background: #F8FAFC;
  cursor: not-allowed;
}

/* 标签 */
.label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  margin-bottom: 6px;
}
```

### 4.5 按钮

```css
/* 主要按钮 */
.btn-primary {
  background: #2563EB;
  color: white;
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 150ms ease-out;
}

.btn-primary:hover {
  background: #1D4ED8;
}

.btn-primary:active {
  transform: scale(0.98);
}

/* 次要按钮 */
.btn-secondary {
  background: white;
  color: #334155;
  border: 1px solid #E2E8F0;
}

/* 危险按钮 */
.btn-danger {
  background: #EF4444;
  color: white;
}
```

---

## 5. 动效与交互

### 5.1 过渡时长规范

| 类型 | 时长 | 缓动函数 | 使用场景 |
|-----|------|---------|---------|
| 快速反馈 | 150ms | ease-out | 按钮悬停、焦点 |
| 标准过渡 | 200ms | ease-in-out | 卡片悬停、展开 |
| 页面过渡 | 300ms | ease-in-out | 模态框、抽屉 |

### 5.2 微交互

```css
/* 悬停效果 */
.hover-lift:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

/* 点击效果 */
.click-scale:active {
  transform: scale(0.98);
}

/* 骨架屏动画 */
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.skeleton {
  background: linear-gradient(90deg, #F1F5F9 25%, #E2E8F0 50%, #F1F5F9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}
```

---

## 6. 数据可视化

### 6.1 图表颜色

```
数据可视化调色板:
┌─────────────────────────────────────────────────────────────────────┐
│                                                                      │
│  主数据色: #2563EB (蓝)                                            │
│  次数据色: #10B981 (绿)                                            │
│  第三数据色: #F59E0B (琥珀)                                         │
│  第四数据色: #8B5CF6 (紫)                                           │
│  第五数据色: #EC4899 (粉)                                           │
│  第六数据色: #06B6D4 (青)                                           │
│                                                                      │
│  趋势色:                                                            │
│  ├── 上升: #10B981 (绿)                                            │
│  ├── 下降: #EF4444 (红)                                            │
│  └── 持平: #94A3B8 (灰)                                            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 6.2 推荐图表类型

| 数据类型 | 推荐图表 | 库推荐 |
|---------|---------|--------|
| 设备状态分布 | 环形图/Doughnut | Chart.js, ECharts |
| 设备在线趋势 | 折线图/Line | ECharts, ApexCharts |
| 设备分类对比 | 柱状图/Bar | Chart.js |
| 升级进度 | 进度条/Progress | 自定义 |
| 设备分布 | 地图/Map | ECharts |

---

## 7. 可访问性 (Accessibility)

### 7.1 核心要求

| 要求 | 实现标准 |
|-----|---------|
| 颜色对比度 | 文字与背景 ≥ 4.5:1 |
| 焦点状态 | 可见的 2px 轮廓 |
| 键盘导航 | Tab 顺序 = 视觉顺序 |
| ARIA | 图标按钮、弹窗、表单标签 |

### 7.2 实现代码

```css
/* 焦点样式 */
:focus-visible {
  outline: 2px solid #2563EB;
  outline-offset: 2px;
}

/* 跳过链接 */
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  background: #2563EB;
  color: white;
  padding: 8px 16px;
  z-index: 100;
}

.skip-link:focus {
  top: 0;
}
```

---

## 8. 响应式设计

### 8.1 断点

```
xs: < 640px     (手机)
sm: 640-767px   (大手机)
md: 768-1023px  (平板)
lg: 1024-1279px (小桌面)
xl: ≥ 1280px    (桌面)
```

### 8.2 适配规则

```
移动端适配:
├── 侧边栏 → 抽屉导航
├── 表格 → 横向滚动
├── 按钮 → 全宽
└── 间距 → 紧凑
```

---

## 9. 质量检查清单

### 9.1 视觉质量

```
□ 使用SVG图标 (Heroicons/Lucide)，不使用emoji
□ 所有图标风格一致
□ 悬停状态有视觉反馈
□ 过渡动画流畅 (150-300ms)
□ 颜色对比度足够
```

### 9.2 交互体验

```
□ 可点击元素有 cursor-pointer
□ 加载显示骨架屏
□ 错误提示明确
□ 成功反馈清晰
□ 禁用状态不可操作
```

### 9.3 代码质量

```
□ 使用 CSS 变量管理主题色
□ 组件样式 scoped
□ 响应式断点清晰
□ 无硬编码颜色值
```

---

## 10. 实现建议

### 10.1 技术栈

| 层级 | 技术 | 说明 |
|-----|------|------|
| UI框架 | Element Plus | Vue 3 组件库 |
| 样式 | Tailwind CSS | 工具类CSS |
| 图标 | Heroicons / Lucide | SVG图标 |
| 表单验证 | VeeValidate | 表单验证 |
| 图表 | ECharts | 数据可视化 |
| 状态管理 | Pinia | Vue状态管理 |

### 10.2 项目结构

```
src/
├── components/
│   ├── common/          # 通用组件
│   ├── layout/          # 布局组件
│   │   ├── AppSidebar.vue
│   │   ├── AppHeader.vue
│   │   └── AppLayout.vue
│   └── business/        # 业务组件
├── composables/         # 组合式函数
├── styles/
│   ├── variables.scss   # 设计变量
│   └── transitions.scss # 动画
└── views/               # 页面
```

---

## 11. 参考资源

### 11.1 设计系统参考

| 参考 | 链接 |
|-----|------|
| Material Design | material.io |
| Ant Design | ant.design |
| Element Plus | element-plus.org |
| Tailwind CSS | tailwindcss.com |

### 11.2 图标资源

| 图标库 | 链接 |
|-------|------|
| Heroicons | heroicons.com |
| Lucide | lucide.dev |
| Simple Icons | simpleicons.org |

### 11.3 字体资源

| 字体 | 链接 |
|-----|------|
| Fira Sans | fonts.google.com/specimen/Fira+Sans |
| Fira Code | fonts.google.com/specimen/Fira+Code |
| Inter | fonts.google.com/specimen/Inter |

---

> 本规范整合了UI/UX Pro Max设计系统的最佳实践，确保产出专业、高质量的企业级管理平台界面。
