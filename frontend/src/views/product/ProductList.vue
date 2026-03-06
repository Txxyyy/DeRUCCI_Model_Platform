<template>
  <div class="product-list">
    <!-- Tab分类筛选 -->
    <div class="category-tabs">
      <el-tabs v-model="activeCategory" @tab-change="handleCategoryChange">
        <el-tab-pane label="全部" name="" />
        <el-tab-pane label="智能床垫" name="智能床垫" />
        <el-tab-pane label="电动床" name="电动床" />
        <el-tab-pane label="智能枕头" name="智能枕头" />
      </el-tabs>
    </div>

    <!-- 搜索栏 - 灰色背景 -->
    <div class="search-bar">
      <div class="search-left">
        <el-input
          v-model="filters.keyword"
          placeholder="搜索产品名称/型号..."
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filters.protocol" placeholder="通信方式" clearable class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="MQTT" value="MQTT" />
          <el-option label="BLE" value="BLE" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable class="filter-select">
          <el-option label="全部" value="" />
          <el-option label="开发中" value="DEVELOPING" />
          <el-option label="已发布" value="PUBLISHED" />
        </el-select>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <div class="search-right">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加产品
        </el-button>
      </div>
    </div>

    <!-- 产品列表 - 卡片布局 -->
    <div class="product-cards">
      <el-row :gutter="16">
        <!-- 产品卡片 -->
        <el-col
          v-for="item in filteredProducts"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="product-card" shadow="hover" @click="handleViewDetail(item)">
            <div class="card-body">
              <!-- 顶部：产品名称 + 状态标签 -->
              <div class="card-header">
                <h3 class="product-name">{{ item.name }}</h3>
                <div class="product-tags">
                  <el-tag :type="getStatusType(item.status)" size="small" effect="light">
                    {{ getStatusText(item.status) }}
                  </el-tag>
                </div>
              </div>

              <!-- 详细信息列表 -->
              <div class="product-detail">
                <div class="detail-row">
                  <span class="detail-label">PID</span>
                  <span class="detail-value">{{ item.pid || '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">产品类型</span>
                  <span class="detail-value">{{ item.category || '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">产品型号</span>
                  <span class="detail-value">{{ item.model || '-' }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">创建时间</span>
                  <span class="detail-value">{{ formatDate(item.createTime) }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">通信协议</span>
                  <span class="detail-value">
                    <el-tag :type="item.protocol === 'MQTT' ? 'primary' : 'warning'" size="small" effect="plain">
                      {{ item.protocol || 'MQTT' }}
                    </el-tag>
                  </span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="filteredProducts.length === 0 && !activeCategory && !filters.protocol && !filters.status && !filters.keyword" description="暂无产品" />

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

// 获取品类对应的CSS类
const getCategoryClass = (category) => {
  const map = {
    '智能床垫': 'category-bed',
    '电动床': 'category-sofa',
    '智能枕头': 'category-pillow'
  }
  return map[category] || 'category-default'
}
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
  console.log('开始获取产品列表...')
  try {
    const res = await productApi.getList({
      category: activeCategory.value || undefined,
      protocol: filters.protocol || undefined,
      status: filters.status || undefined,
      keyword: filters.keyword || undefined
    })
    console.log('API返回数据:', res)
    if (res && res.code === 200) {
      products.value = res.data || []
      console.log('设置产品列表:', products.value.length)
    } else {
      console.error('API返回错误:', res)
      products.value = []
    }
  } catch (e) {
    console.error('获取产品列表失败:', e)
    // 清空产品列表，不使用mock数据
    products.value = []
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
  console.log('提交表单数据:', form)
  try {
    const res = await productApi.create(form)
    console.log('创建产品返回:', res)
    if (res && res.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error('创建失败: ' + (res?.message || '未知错误'))
    }
  } catch (e) {
    console.error('创建产品失败:', e)
    ElMessage.error('创建失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 页面容器 */
.product-list {
  padding: 24px;
  background: var(--el-bg-color);
  min-height: calc(100vh - 60px);
}

/* Tab分类 */
.category-tabs {
  margin-bottom: 16px;
}

.category-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.category-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.category-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  padding: 0 20px;
}

.category-tabs :deep(.el-tabs__item.is-active) {
  color: var(--el-color-primary);
  font-weight: 600;
}

/* 搜索栏 - 灰色背景 */
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.filter-select {
  width: 140px;
}

.search-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 产品卡片列表 */
.product-cards {
  min-height: 400px;
}

/* 卡片样式 */
.product-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 12px;
  border: 1px solid var(--el-border-color-lighter);
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-color: var(--el-color-primary-light-5);
}

.product-card :deep(.el-card__body) {
  padding: 20px;
}

/* 卡片主体 */
.card-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 顶部：产品名称 + 状态标签 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.product-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.5;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-tags {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

/* 详细信息列表 */
.product-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  line-height: 1.6;
}

.detail-label {
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.detail-value {
  color: var(--el-text-color-regular);
  font-family: 'JetBrains Mono', 'SF Mono', monospace;
  font-size: 12px;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 60%;
}

/* 空状态 */
.form-tip {
  font-size: 12px;
  color: var(--color-disabled);
  margin-top: 4px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 20px 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .product-list {
    padding: 16px;
  }

  .search-bar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .search-left {
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
  }

  .filter-select {
    flex: 1;
    min-width: 100px;
  }

  .card-content {
    padding: 16px;
  }

  .card-icon {
    width: 56px;
    height: 56px;
  }

  .card-icon svg {
    width: 28px;
    height: 28px;
  }
}
</style>
