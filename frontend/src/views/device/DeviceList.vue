<template>
  <div class="device-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>设备列表</h2>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="5">
          <el-select v-model="filters.productId" placeholder="产品" clearable style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 100%">
            <el-option label="全部" value="" />
            <el-option label="正常" value="NORMAL" />
            <el-option label="异常" value="ABNORMAL" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.online" placeholder="在线状态" clearable style="width: 100%">
            <el-option label="在线" :value="true" />
            <el-option label="离线" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索设备名称/SN码..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="3">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          注册设备
        </el-button>
        <el-button @click="handleBatchImport">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
      <div class="action-right" v-if="selectedDevices.length > 0">
        <span class="selected-count">已选择 {{ selectedDevices.length }} 台设备</span>
        <el-dropdown @command="handleBatchCommand" trigger="click">
          <el-button type="primary" plain>
            批量操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="online">
                <el-icon><Connection /></el-icon>批量上线
              </el-dropdown-item>
              <el-dropdown-item command="offline">
                <el-icon><Remove /></el-icon>批量下线
              </el-dropdown-item>
              <el-dropdown-item command="ota">
                <el-icon><Refresh /></el-icon>批量升级
              </el-dropdown-item>
              <el-dropdown-item command="delete" divided>
                <el-icon><Delete /></el-icon>批量删除
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
        @selection-change="handleSelectionChange"
        stripe
        :header-cell-style="{ background: 'var(--el-fill-color-light)', color: 'var(--el-text-color-primary)', fontWeight: 600 }"
      >
        <el-table-column type="selection" width="45" />
        <el-table-column label="设备名称" min-width="180">
          <template #default="{ row }">
            <div class="device-name" @click="handleViewDetail(row)">
              <el-icon class="device-icon"><Monitor /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="产品" width="140" />
        <el-table-column prop="sn" label="SN码" min-width="180">
          <template #default="{ row }">
            <span class="sn-code">{{ row.sn }}</span>
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
            <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'" size="small" effect="light">
              {{ row.status === 'NORMAL' ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="110">
          <template #default="{ row }">
            <span class="version-tag">{{ row.firmwareVersion }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="lastOnlineTime" label="最后上线" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">
              <el-icon><View /></el-icon>详情
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button
              :type="row.online ? 'warning' : 'success'"
              link
              @click="row.online ? handleOffline(row) : handleOnline(row)"
            >
              <el-icon v-if="row.online"><TurnOff /></el-icon>
              <el-icon v-else><Connection /></el-icon>
              {{ row.online ? '下线' : '上线' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
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
    <el-dialog v-model="dialogVisible" title="注册设备" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="SN码" prop="sn">
          <el-input v-model="form.sn" placeholder="请输入SN码" />
        </el-form-item>
        <el-form-item label="所属产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备密钥">
          <el-input v-model="form.deviceSecret" placeholder="系统自动生成" readonly>
            <template #append>
              <el-button @click="generateSecret">生成</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowDown, Plus, Upload, Connection, Remove, Refresh, Delete, Monitor, View, Edit, TurnOff } from '@element-plus/icons-vue'
import { deviceApi } from '../../api/device'

const router = useRouter()

// 筛选条件
const filters = reactive({
  productId: '',
  status: '',
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
const products = ref([])
const selectedDevices = ref([])

// 对话框
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  sn: '',
  productId: null,
  deviceSecret: ''
})

const rules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  sn: [{ required: true, message: '请输入SN码', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }]
}

// 筛选后的设备
const filteredDevices = computed(() => {
  let result = devices.value

  if (filters.productId) {
    result = result.filter(d => d.productId === filters.productId)
  }
  if (filters.status) {
    result = result.filter(d => d.status === filters.status)
  }
  if (filters.online !== '') {
    result = result.filter(d => d.online === filters.online)
  }
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(d =>
      d.name.toLowerCase().includes(kw) ||
      d.sn.toLowerCase().includes(kw)
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
    devices.value = [
      { id: 1, name: '卧室床垫-01', productId: 1, productName: '智能床垫', sn: 'SN123456789001', online: true, status: 'NORMAL', firmwareVersion: 'v1.2.3', lastOnlineTime: '2024-01-15 14:30' },
      { id: 2, name: '卧室床垫-02', productId: 1, productName: '智能床垫', sn: 'SN123456789002', online: false, status: 'NORMAL', firmwareVersion: 'v1.2.3', lastOnlineTime: '2024-01-15 10:20' },
      { id: 3, name: '客厅沙发-01', productId: 2, productName: '智能沙发', sn: 'SN123456789003', online: true, status: 'ABNORMAL', firmwareVersion: 'v1.1.0', lastOnlineTime: '2024-01-15 14:25' },
      { id: 4, name: '客房枕头-01', productId: 3, productName: '智能枕头', sn: 'SN123456789004', online: false, status: 'NORMAL', firmwareVersion: 'v1.0.5', lastOnlineTime: '2024-01-14 22:00' }
    ]
  }
}

const fetchProducts = async () => {
  products.value = [
    { id: 1, name: '智能床垫' },
    { id: 2, name: '智能沙发' },
    { id: 3, name: '智能枕头' }
  ]
}

const handleSearch = () => { pagination.page = 1 }
const handleReset = () => {
  filters.productId = ''
  filters.status = ''
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
  Object.assign(form, { id: null, name: '', sn: '', productId: null, deviceSecret: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const generateSecret = () => {
  form.deviceSecret = 'SEC_' + Math.random().toString(36).substring(2, 18).toUpperCase()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  setTimeout(() => {
    if (!form.id) {
      form.id = Date.now()
      devices.value.unshift({
        ...form,
        productName: products.value.find(p => p.id === form.productId)?.name,
        online: false,
        status: 'NORMAL',
        firmwareVersion: 'v1.0.0',
        lastOnlineTime: '-'
      })
      ElMessage.success('注册成功')
    } else {
      const idx = devices.value.findIndex(d => d.id === form.id)
      if (idx !== -1) Object.assign(devices.value[idx], form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    submitLoading.value = false
  }, 500)
}

const handleViewDetail = (row) => {
  router.push({ path: '/devices/monitor', query: { id: row.id } })
}

const handleOnline = (row) => {
  row.online = true
  row.lastOnlineTime = new Date().toLocaleString()
  ElMessage.success('上线成功')
}

const handleOffline = (row) => {
  row.online = false
  ElMessage.success('下线成功')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除设备 ${row.name}?`, '提示', { type: 'warning' })
  devices.value = devices.value.filter(d => d.id !== row.id)
  ElMessage.success('删除成功')
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
/* 页面容器 - 增加上下边距 */
.device-list {
  padding: 0 24px 24px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 20px;
}

.filter-card :deep(.el-card__body) {
  padding: 20px 24px;
}

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
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

/* 表格样式优化 */
.table-card :deep(.el-table) {
  --el-table-border-color: var(--el-border-color-lighter);
  --el-table-header-bg-color: var(--el-fill-color-light);
}

.table-card :deep(.el-table th.el-table__cell) {
  padding: 14px 0;
  font-size: 13px;
}

.table-card :deep(.el-table td.el-table__cell) {
  padding: 16px 0;
  font-size: 14px;
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

/* SN码 - 等宽字体 */
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
.el-button + .el-button {
  margin-left: 4px;
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
}
</style>
