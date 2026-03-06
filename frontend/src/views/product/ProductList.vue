<template>
  <div class="product-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>产品列表</h2>
    </div>

    <!-- Tab分类筛选 -->
    <div class="category-tabs">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="智能床垫" name="智能床垫" />
        <el-tab-pane label="电动床" name="电动床" />
        <el-tab-pane label="智能枕头" name="智能枕头" />
      </el-tabs>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-select v-model="filters.protocol" placeholder="通信方式" clearable style="width: 100%">
            <el-option label="全部" value="" />
            <el-option label="MQTT" value="MQTT" />
            <el-option label="BLE" value="BLE" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 100%">
            <el-option label="全部" value="" />
            <el-option label="开发中" value="DEVELOPING" />
            <el-option label="已发布" value="PUBLISHED" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索名称/型号..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 产品列表 - 4列布局 -->
    <div class="product-cards">
      <el-row :gutter="20">
        <!-- 添加产品卡片 - 固定在第一个 -->
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <el-card class="product-card add-card" shadow="hover" @click="handleAdd">
            <div class="add-card-content">
              <div class="device-icon-wrapper">
                <svg class="device-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="32" cy="32" r="28" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
                  <path d="M32 20v24M20 32h24" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              <span class="add-text">添加产品</span>
            </div>
          </el-card>
        </el-col>

        <!-- 产品卡片 -->
        <el-col
          v-for="item in filteredProducts"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="6"
          :lg="6"
        >
          <el-card class="product-card" shadow="hover" @click="handleViewDetail(item)">
            <!-- 顶部图标区域 -->
            <div class="card-header">
              <div class="device-icon-wrapper">
                <!-- 智能床垫图标 -->
                <svg v-if="item.category === '智能床垫'" class="device-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="8" y="24" width="48" height="24" rx="4" stroke="currentColor" stroke-width="2"/>
                  <path d="M16 24V16a2 2 0 012-2h0a2 2 0 012 2v8M24 24V16a2 2 0 012-2h0a2 2 0 012 2v8M32 24V16a2 2 0 012-2h0a2 2 0 012 2v8M40 24V16a2 2 0 012-2h0a2 2 0 012 2v8M20 40h24M20 44h24" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                <!-- 电动床图标 -->
                <svg v-else-if="item.category === '电动床'" class="device-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="6" y="28" width="52" height="20" rx="2" stroke="currentColor" stroke-width="2"/>
                  <path d="M14 28V20a2 2 0 012-2h0a2 2 0 012 2v8M26 28V20a2 2 0 012-2h0a2 2 0 012 2v8M38 28V20a2 2 0 012-2h0a2 2 0 012 2v8M50 28V20a2 2 0 012-2h0a2 2 0 012 2v8" stroke="currentColor" stroke-width="2"/>
                  <path d="M14 48l4-6h28l4 6M18 48h28" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                <!-- 智能枕头图标 -->
                <svg v-else-if="item.category === '智能枕头'" class="device-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <ellipse cx="32" cy="36" rx="24" ry="14" stroke="currentColor" stroke-width="2"/>
                  <path d="M20 32c-2 0-4-2-4-4s2-4 4-4M44 32c2 0 4-2 4-4s-2-4-4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                  <circle cx="32" cy="28" r="4" stroke="currentColor" stroke-width="2"/>
                </svg>
                <!-- 默认图标 -->
                <svg v-else class="device-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="12" y="8" width="40" height="48" rx="4" stroke="currentColor" stroke-width="2"/>
                  <path d="M24 20h16M24 28h16M24 36h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="status-tags">
                <el-tag :type="item.protocol === 'MQTT' ? 'primary' : 'warning'" size="small" effect="light">
                  {{ item.protocol || 'MQTT' }}
                </el-tag>
                <el-tag :type="getStatusType(item.status)" size="small" effect="light">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
            </div>

            <!-- 产品信息 -->
            <div class="card-body">
              <h3 class="product-name">{{ item.name }}</h3>
              <div class="product-meta">
                <div class="meta-item">
                  <span class="meta-label">PID</span>
                  <span class="meta-value">{{ item.pid || '-' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">型号</span>
                  <span class="meta-value">{{ item.model || '-' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">创建</span>
                  <span class="meta-value">{{ formatDate(item.createTime) }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="filteredProducts.length === 0 && !activeCategory && !filters.protocol && !filters.status && !filters.keyword" description="暂无产品，点击添加产品卡片创建" />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="filteredProducts.length > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新增产品弹窗 -->
    <el-dialog v-model="dialogVisible" title="新建产品" width="600px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-divider>基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品型号" prop="model">
              <el-input v-model="form.model" placeholder="如：DR-M001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="form.name" placeholder="如：慕思智能床垫X1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="如：慕思" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通信方式" prop="protocol">
              <el-radio-group v-model="form.protocol">
                <el-radio value="MQTT">MQTT</el-radio>
                <el-radio value="BLE">BLE</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品品类" prop="category">
              <el-select v-model="form.category" placeholder="请选择品类" style="width: 100%">
                <el-option label="智能床垫" value="智能床垫" />
                <el-option label="电动床" value="电动床" />
                <el-option label="智能枕头" value="智能枕头" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="PID" v-if="form.pid">
          <el-input v-model="form.pid" disabled>
            <template #append>
              <el-button @click="generatePid" title="重新生成PID">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </template>
          </el-input>
          <div class="form-tip">系统自动生成的唯一标识符</div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Plus, Picture, Refresh } from '@element-plus/icons-vue'
import { productApi } from '../../api/product'

const router = useRouter()

// Tab分类筛选
const activeCategory = ref('')

// 筛选条件
const filters = reactive({
  protocol: '',
  status: '',
  keyword: ''
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 12,
  total: 0
})

// 产品数据
const products = ref([])

// 对话框
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  pid: '',
  name: '',
  model: '',
  brand: '',
  category: '',
  protocol: 'MQTT'
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  model: [{ required: true, message: '请输入产品型号', trigger: 'blur' }],
  brand: [{ required: true, message: '请输入产品品牌', trigger: 'blur' }],
  category: [{ required: true, message: '请选择品类', trigger: 'change' }],
  protocol: [{ required: true, message: '请选择通信方式', trigger: 'change' }]
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}-${day}`
}

// Tab分类切换
const handleCategoryChange = (category) => {
  activeCategory.value = category
  pagination.page = 1
  fetchData()
}

// 筛选后的产品（不包含添加产品卡片）
const filteredProducts = computed(() => {
  let result = products.value

  // 使用Tab分类筛选
  if (activeCategory.value) {
    result = result.filter(p => p.category === activeCategory.value)
  }
  if (filters.protocol) {
    result = result.filter(p => p.protocol === filters.protocol)
  }
  if (filters.status) {
    result = result.filter(p => p.status === filters.status)
  }
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(p =>
      p.name.toLowerCase().includes(kw) ||
      (p.model && p.model.toLowerCase().includes(kw))
    )
  }

  pagination.total = result.length
  return result
})

const getStatusType = (status) => {
  const map = { DEVELOPING: 'primary', PUBLISHED: 'success', OFFLINE: 'warning', DEPRECATED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { DEVELOPING: '开发中', PUBLISHED: '已发布', OFFLINE: '已下线', DEPRECATED: '已废弃' }
  return map[status] || status
}

const fetchData = async () => {
  try {
    const res = await productApi.getList({
      category: activeCategory.value || undefined,
      protocol: filters.protocol || undefined,
      status: filters.status || undefined,
      keyword: filters.keyword || undefined
    })
    products.value = res.data || []
  } catch (e) {
    // 模拟数据
    products.value = [
      { id: 1, name: '慕思智能床垫X1', model: 'DR-M001', brand: '慕思', pid: 'A1B2C3', category: '智能床垫', protocol: 'MQTT', status: 'PUBLISHED', imageUrl: '', createTime: '2026-03-01' },
      { id: 2, name: '慕思智能枕头P1', model: 'DR-P001', brand: '慕思', pid: 'D4E5F6', category: '智能枕头', protocol: 'BLE', status: 'DEVELOPING', imageUrl: '', createTime: '2026-03-02' },
      { id: 3, name: '慕思电动床B1', model: 'DR-B001', brand: '慕思', pid: 'G7H8I9', category: '电动床', protocol: 'MQTT', status: 'PUBLISHED', imageUrl: '', createTime: '2026-03-03' },
      { id: 4, name: '慕思智能床垫Pro', model: 'DR-M002', brand: '慕思', pid: 'J0K1L2', category: '智能床垫', protocol: 'MQTT', status: 'DEVELOPING', imageUrl: '', createTime: '2026-03-04' },
      { id: 5, name: '慕思智能枕头Pro', model: 'DR-P002', brand: '慕思', pid: 'M3N4O5', category: '智能枕头', protocol: 'BLE', status: 'DEVELOPING', imageUrl: '', createTime: '2026-03-05' },
      { id: 6, name: '慕思电动床Pro', model: 'DR-B002', brand: '慕思', pid: 'P6Q7R8', category: '电动床', protocol: 'MQTT', status: 'PUBLISHED', imageUrl: '', createTime: '2026-03-05' },
      { id: 7, name: '慕思智能床垫Max', model: 'DR-M003', brand: '慕思', pid: 'S9T0U1', category: '智能床垫', protocol: 'MQTT', status: 'DEVELOPING', imageUrl: '', createTime: '2026-03-05' },
      { id: 8, name: '慕思智能枕头Max', model: 'DR-P003', brand: '慕思', pid: 'V2W3X4', category: '智能枕头', protocol: 'BLE', status: 'PUBLISHED', imageUrl: '', createTime: '2026-03-05' }
    ]
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  activeCategory.value = ''
  filters.protocol = ''
  filters.status = ''
  filters.keyword = ''
  pagination.page = 1
  fetchData()
}

const handlePageChange = (page) => {
  pagination.page = page
}

const handleViewDetail = (row) => {
  router.push(`/products/${row.id}`)
}

// 生成唯一PID
const generatePid = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let pid = ''
  for (let i = 0; i < 6; i++) {
    pid += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  form.pid = pid
  return pid
}

const handleAdd = () => {
  Object.assign(form, {
    id: null, pid: '', name: '', model: '', brand: '', category: '', protocol: 'MQTT'
  })
  generatePid()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    await productApi.create(form)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    // 模拟成功
    form.id = Date.now()
    form.status = 'DEVELOPING'
    form.createTime = new Date().toISOString().split('T')[0]
    products.value.unshift({ ...form })
    ElMessage.success('创建成功')
    dialogVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: var(--color-title);
}

.category-tabs {
  margin-bottom: 16px;
}

.category-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.product-cards {
  min-height: 400px;
}

/* 卡片通用样式 */
.product-card {
  height: 100%;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.product-card :deep(.el-card__body) {
  padding: 16px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 添加产品卡片 */
.add-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px 0;
}

.add-text {
  font-size: 14px;
  color: var(--color-disabled);
  transition: color 0.25s ease;
}

.add-card:hover .add-text {
  color: var(--el-color-primary);
}

/* 卡片头部 - 图标和标签 */
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 12px;
}

.device-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.device-icon {
  width: 40px;
  height: 40px;
  color: var(--el-color-primary);
  opacity: 0.85;
}

.status-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* 卡片主体 */
.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-title);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  letter-spacing: 0.3px;
}

/* 元信息区域 */
.product-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-label {
  font-size: 12px;
  color: var(--color-disabled);
  font-weight: 400;
}

.meta-value {
  font-size: 12px;
  color: var(--color-secondary);
  font-weight: 500;
  font-family: 'JetBrains Mono', monospace;
}

/* 移除了产品图片区域的背景样式 */
.product-image {
  display: none;
}

.form-tip {
  font-size: 12px;
  color: var(--color-disabled);
  margin-top: 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
