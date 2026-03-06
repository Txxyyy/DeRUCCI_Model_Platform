<template>
  <div class="device-list">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-select v-model="filters.productId" placeholder="产品" clearable>
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.status" placeholder="状态" clearable>
            <el-option label="正常" value="NORMAL" />
            <el-option label="异常" value="ABNORMAL" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filters.online" placeholder="在线状态" clearable>
            <el-option label="在线" :value="true" />
            <el-option label="离线" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索设备名称/SN码..."
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="action-card">
      <div class="action-bar">
        <div class="action-left">
          <el-button type="primary" @click="handleAdd">注册设备</el-button>
          <el-button @click="handleBatchImport">批量导入</el-button>
        </div>
        <div class="action-right" v-if="selectedDevices.length > 0">
          <span>已选择 {{ selectedDevices.length }} 台设备</span>
          <el-dropdown @command="handleBatchCommand">
            <el-button>批量操作<el-icon class="el-icon--right"><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="online">批量上线</el-dropdown-item>
                <el-dropdown-item command="offline">批量下线</el-dropdown-item>
                <el-dropdown-item command="ota">批量升级</el-dropdown-item>
                <el-dropdown-item command="delete" divided>批量删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-card>

    <!-- 设备列表 -->
    <el-card>
      <el-table
        :data="filteredDevices"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="40" />
        <el-table-column label="设备名称" min-width="150">
          <template #default="{ row }">
            <div class="device-name" @click="handleViewDetail(row)">{{ row.name }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="产品" width="120" />
        <el-table-column prop="sn" label="SN码" min-width="150" />
        <el-table-column label="在线" width="80">
          <template #default="{ row }">
            <div class="online-status">
              <span :class="['status-dot', row.online ? 'online' : 'offline']"></span>
              {{ row.online ? '在线' : '离线' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'" size="small">
              {{ row.status === 'NORMAL' ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="100" />
        <el-table-column prop="lastOnlineTime" label="最后上线" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              type="success"
              link
              @click="handleOnline(row)"
              v-if="!row.online"
            >上线</el-button>
            <el-button
              type="warning"
              link
              @click="handleOffline(row)"
              v-if="row.online"
            >下线</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          layout="total, prev, pager, next"
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
import { Search, ArrowDown } from '@element-plus/icons-vue'
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
.filter-card, .action-card { margin-bottom: 16px; }
.action-card .el-card__body { padding: 12px 16px; }

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-left, .action-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.device-name { color: #409eff; cursor: pointer; }

.online-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.online { background: #52c41a; }
.status-dot.offline { background: #999; }

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
