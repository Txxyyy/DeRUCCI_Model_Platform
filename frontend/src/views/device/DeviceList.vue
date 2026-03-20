<template>
  <div class="device-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>设备列表</h2>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <div class="filter-row">
        <el-select v-model="filters.productId" placeholder="按产品筛选" clearable class="filter-select">
          <el-option-group
            v-for="(group, cat) in groupedProducts"
            :key="cat"
            :label="cat"
          >
            <el-option v-for="p in group" :key="p.id" :label="p.name" :value="p.id" />
          </el-option-group>
        </el-select>
        <el-select v-model="filters.deviceType" placeholder="设备类型" clearable class="filter-select">
          <el-option label="生产设备" value="PRODUCTION" />
          <el-option label="测试设备" value="TEST" />
        </el-select>
        <el-select v-model="filters.online" placeholder="在线状态" clearable class="filter-select">
          <el-option label="在线" :value="true" />
          <el-option label="离线" :value="false" />
        </el-select>
        <el-input
          v-model="filters.keyword"
          placeholder="搜索设备名称/SN码..."
          clearable
          class="filter-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix><AppIcon name="search" :size="15" /></template>
        </el-input>
        <div class="filter-buttons">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
      </div>
    </el-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <el-button v-permission="'DEVICE:RW'" type="primary" @click="handleAdd">
          <AppIcon name="plus" :size="15" style="margin-right:4px" />
          注册设备
        </el-button>
        <el-button @click="handleBatchImport">
          <AppIcon name="upload" :size="15" style="margin-right:4px" />
          批量导入
        </el-button>
      </div>
      <div v-if="selectedDevices.length > 0" class="action-right">
        <span class="selected-count">已选择 {{ selectedDevices.length }} 台设备</span>
        <el-dropdown trigger="click" @command="handleBatchCommand">
          <el-button type="primary" plain>
            批量操作<AppIcon name="chevron-down" :size="14" style="margin-left:4px" />
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="online">
                <AppIcon name="connection" :size="14" style="margin-right:6px" />批量上线
              </el-dropdown-item>
              <el-dropdown-item command="offline">
                <AppIcon name="remove" :size="14" style="margin-right:6px" />批量下线
              </el-dropdown-item>
              <el-dropdown-item command="ota">
                <AppIcon name="refresh" :size="14" style="margin-right:6px" />批量升级
              </el-dropdown-item>
              <el-dropdown-item command="delete" divided>
                <AppIcon name="trash" :size="14" style="margin-right:6px" />批量删除
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 设备列表 -->
    <el-card class="table-card">
      <el-table
        :data="filteredDevices"
        stripe
        :header-cell-style="{ background: 'var(--el-fill-color-light)', color: 'var(--el-text-color-primary)', fontWeight: 600 }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column label="设备名称" min-width="200">
          <template #default="{ row }">
            <div class="device-name" @click="handleViewDetail(row)">
              <AppIcon name="monitor" :size="15" class="device-icon" />
              <span>{{ row.name }}</span>
              <el-tag v-if="row.deviceType === 'TEST'" type="warning" size="small" effect="light" style="margin-left:6px">测试</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="产品" width="130" />
        <el-table-column prop="serialNumber" label="SN码" min-width="150">
          <template #default="{ row }">
            <span class="sn-code">{{ row.serialNumber }}</span>
          </template>
        </el-table-column>
        <el-table-column label="在线状态" width="100">
          <template #default="{ row }">
            <div class="online-status">
              <span :class="['status-dot', row.online ? 'online' : 'offline']"></span>
              <span :class="row.online ? 'text-success' : 'text-muted'">
                {{ row.online ? '在线' : '离线' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ERROR' ? 'danger' : 'success'" size="small" effect="light">
              {{ row.status === 'ERROR' ? '异常' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="110">
          <template #default="{ row }">
            <span class="version-tag">{{ row.firmwareVersion }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后上线" width="180">
          <template #default="{ row }">{{ formatTime(row.lastOnlineTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="op-btns">
              <el-button type="primary" link size="small" @click="handleViewDetail(row)">详情</el-button>
              <el-divider direction="vertical" />
              <el-button v-permission-disabled="'DEVICE:RW'" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-divider direction="vertical" />
              <el-button v-if="row.online" v-permission-disabled="'DEVICE:OFFLINE'" type="warning" link size="small" @click="handleOffline(row)">下线</el-button>
              <el-button v-else v-permission-disabled="'DEVICE:RW'" type="success" link size="small" @click="handleOnline(row)">上线</el-button>
              <el-divider direction="vertical" />
              <el-button v-permission-disabled="'DEVICE:DELETE'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 注册设备弹窗 -->
    <el-dialog v-model="dialogVisible" title="注册设备" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="SN码" prop="serialNumber">
          <el-input v-model="form.serialNumber" placeholder="请输入SN码" />
        </el-form-item>

        <!-- 品类选择 -->
        <el-form-item label="产品品类" prop="productId">
          <div class="category-selector">
            <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
              <el-radio-button value="智能床垫">智能床垫</el-radio-button>
              <el-radio-button value="电动床">电动床</el-radio-button>
              <el-radio-button value="智能枕头">智能枕头</el-radio-button>
            </el-radio-group>
          </div>
        </el-form-item>

        <!-- 产品卡片列表 -->
        <el-form-item v-if="selectedCategory" label="选择产品">
          <div v-if="publishedProducts.length === 0" class="no-products-tip">
            <el-empty description="该品类暂无产品" :image-size="60" />
          </div>
          <div v-else class="product-card-list">
            <el-tooltip
              v-for="p in publishedProducts"
              :key="p.id"
              :content="'请先发布产品'"
              :disabled="p.status !== 'DEVELOPING'"
              placement="top"
            >
              <div
                :class="['product-card', form.productId === p.id ? 'selected' : '', p.status === 'DEVELOPING' ? 'developing disabled' : '']"
                @click="p.status !== 'DEVELOPING' && handleProductSelect(p)"
              >
                <el-tag v-if="p.status === 'DEVELOPING'" type="warning" size="small" class="dev-badge">开发中</el-tag>
                <div class="product-card-name">{{ p.name }}</div>
                <div class="product-card-meta">PID: {{ p.pid || p.code || '-' }}</div>
                <div class="product-card-meta">协议: {{ p.protocol || p.communicationType || '-' }}</div>
              </div>
            </el-tooltip>
          </div>
        </el-form-item>

        <!-- deviceKey -->
        <el-form-item label="设备密钥">
          <el-input v-model="form.deviceKey" placeholder="选择产品后自动生成，可手动修改">
            <template #append>
              <el-button @click="generateDeviceKey(form.productId ? (publishedProducts.find(p=>p.id===form.productId)?.pid || '') : '')">重新生成</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import { deviceApi } from '../../api/device'
import { productApi } from '../../api/product'

const router = useRouter()

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// 筛选条件
const filters = reactive({
  productId: '',
  deviceType: '',
  online: '',
  keyword: ''
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 数据
const devices = ref([])
const allProductsForFilter = ref([])
const selectedDevices = ref([])

// 弹窗内状态
const selectedCategory = ref('')
const publishedProducts = ref([])

// 对话框
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  serialNumber: '',
  productId: null,
  productName: '',
  thingModelId: null,
  deviceKey: '',
  deviceType: 'PRODUCTION'
})

const rules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  serialNumber: [{ required: true, message: '请输入SN码', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }]
}

// 按品类分组的产品（用于筛选区下拉）
const groupedProducts = computed(() => {
  const groups = {}
  for (const p of allProductsForFilter.value) {
    const cat = p.category || '其他'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(p)
  }
  return groups
})

// 筛选后的设备
const filteredDevices = computed(() => {
  let result = devices.value

  if (filters.productId) {
    result = result.filter(d => d.productId === filters.productId)
  }
  if (filters.deviceType) {
    result = result.filter(d => (d.deviceType || 'PRODUCTION') === filters.deviceType)
  }
  if (filters.online !== '') {
    result = result.filter(d => d.online === filters.online)
  }
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(d =>
      (d.name || '').toLowerCase().includes(kw) ||
      (d.serialNumber || '').toLowerCase().includes(kw)
    )
  }

  pagination.total = result.length
  return result
})

const fetchData = async () => {
  try {
    const res = await deviceApi.getList()
    devices.value = res.data || []
  } catch (e) {
    devices.value = []
  }
}

const fetchProducts = async () => {
  try {
    const res = await productApi.getList()
    allProductsForFilter.value = res.data || []
  } catch (e) {
    allProductsForFilter.value = []
  }
}

const handleCategoryChange = async (category) => {
  form.productId = null
  form.productName = ''
  form.thingModelId = null
  form.deviceType = 'PRODUCTION'
  publishedProducts.value = []
  if (!category) return
  try {
    const res = await productApi.getList({ category })
    const all = res.data || []
    // PUBLISHED 排前面，DEVELOPING 排后面
    publishedProducts.value = [...all].sort((a, b) => {
      if (a.status === 'PUBLISHED' && b.status !== 'PUBLISHED') return -1
      if (a.status !== 'PUBLISHED' && b.status === 'PUBLISHED') return 1
      return 0
    })
  } catch (e) {
    publishedProducts.value = []
  }
}

const generateDeviceKey = (pid) => {
  const prefix = (pid || 'XXXXXX').substring(0, 6).toUpperCase()
  const hex = Math.random().toString(16).substring(2, 10).toUpperCase()
  form.deviceKey = `DK_${prefix}_${hex}`
}

const handleProductSelect = (product) => {
  if (product.status === 'DEVELOPING') return
  form.productId = product.id
  form.productName = product.name
  form.thingModelId = product.thingModelId || null
  form.deviceType = 'PRODUCTION'
  generateDeviceKey(product.pid || product.code || '')
}

const handleSearch = () => { pagination.page = 1 }
const handleReset = () => {
  filters.productId = ''
  filters.deviceType = ''
  filters.online = ''
  filters.keyword = ''
  pagination.page = 1
}
const handlePageChange = (page) => { pagination.page = page }
const handleSizeChange = (size) => { pagination.pageSize = size; pagination.page = 1 }

const handleSelectionChange = (selection) => {
  selectedDevices.value = selection
}

const handleAdd = () => {
  Object.assign(form, { id: null, name: '', serialNumber: '', productId: null, productName: '', thingModelId: null, deviceKey: '', deviceType: 'PRODUCTION' })
  selectedCategory.value = ''
  publishedProducts.value = []
  generateDeviceKey('')
  if (formRef.value) formRef.value.resetFields()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    serialNumber: row.serialNumber,
    productId: row.productId,
    productName: row.productName,
    thingModelId: row.thingModelId,
    deviceKey: row.deviceKey || ''
  })
  selectedCategory.value = ''
  publishedProducts.value = []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (!form.id) {
      await deviceApi.create({
        name: form.name,
        serialNumber: form.serialNumber,
        productId: form.productId,
        productName: form.productName,
        thingModelId: form.thingModelId,
        deviceKey: form.deviceKey,
        deviceType: form.deviceType
      })
      ElMessage.success('注册成功')
    } else {
      await deviceApi.update(form.id, {
        name: form.name,
        serialNumber: form.serialNumber,
        productId: form.productId,
        productName: form.productName,
        thingModelId: form.thingModelId,
        deviceKey: form.deviceKey
      })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleViewDetail = (row) => {
  router.push({ path: '/devices/monitor', query: { id: row.id } })
}

const handleOnline = async (row) => {
  try {
    await deviceApi.online(row.id)
    row.online = true
    row.lastOnlineTime = formatTime(new Date())
    ElMessage.success('上线成功')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleOffline = async (row) => {
  await ElMessageBox.confirm(`确定将设备「${row.name}」下线？`, '提示', {
    type: 'warning',
    confirmButtonText: '确定下线',
    cancelButtonText: '取消'
  })
  try {
    await deviceApi.offline(row.id)
    row.online = false
    ElMessage.success('下线成功')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除设备 ${row.name}?`, '提示', { type: 'warning' })
  try {
    await deviceApi.delete(row.id)
    devices.value = devices.value.filter(d => d.id !== row.id)
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const handleBatchImport = () => {
  ElMessage.info('批量导入功能开发中')
}

const handleBatchCommand = (command) => {
  ElMessage.info(`批量操作: ${command}`)
}

onMounted(() => {
  fetchData()
  fetchProducts()
})
</script>

<style scoped>
/* 页面容器 */
.device-list {
  padding: 0 24px 24px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 12px;
}

.filter-card :deep(.el-card__body) {
  padding: 12px 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-select {
  width: 160px;
  flex-shrink: 0;
}

.filter-input {
  flex: 1;
  min-width: 180px;
}

.filter-buttons {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 10px 16px;
  background: var(--el-bg-color);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
}

.action-left, .action-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-count {
  font-size: 14px;
  color: var(--el-color-primary);
  font-weight: 500;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 20px;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.table-card :deep(.el-table) {
  --el-table-border-color: var(--el-border-color-lighter);
  --el-table-header-bg-color: var(--el-fill-color-light);
}

.table-card :deep(.el-table th.el-table__cell) {
  padding: 10px 0;
  font-size: 13px;
}

.table-card :deep(.el-table td.el-table__cell) {
  padding: 10px 0;
  font-size: 13px;
}

.table-card :deep(.el-table__row) {
  transition: background-color 0.2s ease;
}

.table-card :deep(.el-table__row:hover > td) {
  background-color: var(--el-fill-color-light) !important;
}

/* 设备名称 */
.device-name {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-color-primary);
  cursor: pointer;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.device-name:hover {
  opacity: 0.8;
}

.device-icon {
  font-size: 16px;
  color: var(--el-color-primary);
}

/* SN码 */
.sn-code {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
  color: var(--el-text-color-regular);
  letter-spacing: 0.5px;
}

/* 在线状态 */
.online-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-dot.online {
  background: var(--el-color-success);
  box-shadow: 0 0 8px var(--el-color-success);
}

.status-dot.offline {
  background: var(--el-text-color-placeholder);
}

.text-success {
  color: var(--el-color-success);
  font-weight: 500;
}

.text-muted {
  color: var(--el-text-color-placeholder);
}

/* 固件版本标签 */
.version-tag {
  display: inline-block;
  padding: 2px 8px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
  font-size: 12px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  color: var(--el-text-color-secondary);
}

/* 操作按钮 */
.op-btns {
  display: flex;
  align-items: center;
}

.op-btns .el-divider--vertical {
  margin: 0 4px;
  height: 12px;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 16px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.pagination-wrapper :deep(.el-pagination) {
  --el-pagination-button-bg-color: var(--el-fill-color-light);
  --el-pagination-hover-color: var(--el-color-primary);
}

/* 弹窗内品类选择器 */
.category-selector {
  width: 100%;
}

/* 产品卡片列表 */
.product-card-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  width: 100%;
}

.product-card {
  position: relative;
  width: calc(33% - 8px);
  padding: 12px 14px;
  border: 2px solid var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: var(--el-bg-color);
}

.product-card:hover {
  border-color: var(--el-color-primary-light-5);
  background: var(--el-color-primary-light-9);
}

.product-card.selected {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}

.product-card.developing {
  border-color: var(--el-border-color-lighter);
  opacity: 0.5;
  cursor: not-allowed;
}

.product-card.developing:hover {
  border-color: var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

.product-card.developing.selected {
  border-color: var(--el-border-color-lighter);
  background: var(--el-bg-color);
}

.dev-badge {
  position: absolute;
  top: 8px;
  right: 8px;
}

.product-card-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.product-card-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
}

.no-products-tip {
  width: 100%;
  padding: 10px 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .device-list {
    padding: 0 16px 16px;
  }

  .page-header h2 {
    font-size: 18px;
  }

  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .action-left, .action-right {
    justify-content: center;
  }

  .product-card {
    width: 100%;
  }
}
</style>
