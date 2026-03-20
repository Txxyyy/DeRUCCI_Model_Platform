<template>
  <div class="ota-task-list">
    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-button v-permission="'OTA:RW'" type="primary" @click="handleAdd">创建任务</el-button>
    </el-card>

    <!-- 任务列表 -->
    <el-card>
      <el-table v-loading="loading" :data="tasks" stripe>
        <el-table-column prop="name" label="任务名称" min-width="180" />
        <el-table-column prop="targetVersion" label="目标版本" width="120" />
        <el-table-column label="目标设备" width="100">
          <template #default="{ row }">{{ row.totalCount || 0 }}台</template>
        </el-table-column>
        <el-table-column label="进度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="calcProgress(row)" :status="row.status === 'COMPLETED' ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="成功/失败" width="100">
          <template #default="{ row }">
            <span class="success">{{ row.successCount || 0 }}</span> /
            <span class="fail">{{ row.failCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 'RUNNING' || row.status === 'PENDING'" type="danger" link @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建任务弹窗 -->
    <el-dialog v-model="dialogVisible" title="创建升级任务" width="750px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>

        <el-divider>选择产品与固件</el-divider>
        <el-form-item label="选择产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%" @change="handleProductChange">
            <el-option v-for="p in publishedProducts" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="选择固件" prop="firmwareId">
          <el-select v-model="form.firmwareId" placeholder="请先选择产品" style="width: 100%" :disabled="!form.productId" @change="handleFirmwareChange">
            <el-option v-for="f in filteredFirmwares" :key="f.id" :label="f.version" :value="f.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="升级说明">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入升级说明" />
        </el-form-item>

        <el-divider>选择目标设备</el-divider>
        <el-form-item label="目标设备" prop="deviceIds">
          <div v-if="!form.productId" class="device-tip">请先选择产品</div>
          <div v-else-if="productDevices.length === 0" class="device-tip">该产品暂无关联设备</div>
          <div v-else class="device-select-area">
            <div class="device-select-header">
              <el-checkbox v-model="selectAll" @change="handleSelectAll">全选 ({{ productDevices.length }}台)</el-checkbox>
              <span class="selected-info">已选 {{ form.deviceIds.length }} 台</span>
            </div>
            <div class="device-checkbox-list">
              <el-checkbox-group v-model="form.deviceIds">
                <div v-for="d in productDevices" :key="d.id" class="device-checkbox-item">
                  <el-checkbox :value="d.id">
                    <span class="dev-name">{{ d.name }}</span>
                    <span class="dev-sn">{{ d.serialNumber || '-' }}</span>
                    <span class="dev-ver">{{ d.firmwareVersion || '-' }}</span>
                    <el-tag :type="d.online ? 'success' : 'info'" size="small" effect="light">{{ d.online ? '在线' : '离线' }}</el-tag>
                  </el-checkbox>
                </div>
              </el-checkbox-group>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">发布任务</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情弹窗 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="700px">
      <div class="task-info">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item"><span class="label">任务名称:</span><span class="value">{{ currentTask.name }}</span></div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentTask.status)" size="small">{{ getStatusText(currentTask.status) }}</el-tag>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item"><span class="label">目标版本:</span><span class="value">{{ currentTask.targetVersion }}</span></div>
          </el-col>
          <el-col :span="12">
            <div class="info-item"><span class="label">创建时间:</span><span class="value">{{ formatTime(currentTask.createTime) }}</span></div>
          </el-col>
        </el-row>
      </div>

      <el-divider>升级进度</el-divider>
      <el-progress :percentage="calcProgress(currentTask)" :stroke-width="20" />
      <el-row :gutter="20" class="progress-stats">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value success">{{ currentTask.successCount || 0 }}</div>
            <div class="stat-label">升级成功</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value pending">{{ currentTask.totalCount || 0 }}</div>
            <div class="stat-label">目标总数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value fail">{{ currentTask.failCount || 0 }}</div>
            <div class="stat-label">升级失败</div>
          </div>
        </el-col>
      </el-row>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentTask.status === 'RUNNING' || currentTask.status === 'PENDING'" type="danger" @click="handleCancel(currentTask)">取消任务</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { otaApi } from '@/api/ota'
import { productApi } from '@/api/product'
import { deviceApi } from '@/api/device'

const tasks = ref([])
const firmwares = ref([])
const products = ref([])
const productDevices = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const currentTask = ref({})
const selectAll = ref(false)

const publishedProducts = computed(() => products.value.filter(p => p.status === 'PUBLISHED'))
const filteredFirmwares = computed(() => {
  if (!form.productId) return []
  return firmwares.value.filter(f => f.productId === form.productId)
})

const form = reactive({
  name: '',
  productId: null,
  firmwareId: null,
  targetVersion: '',
  description: '',
  deviceIds: []
})

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  firmwareId: [{ required: true, message: '请选择固件', trigger: 'change' }],
  deviceIds: [{ type: 'array', required: true, min: 1, message: '请选择至少一台设备', trigger: 'change' }]
}

const calcProgress = (row) => {
  if (!row.totalCount || row.totalCount === 0) return 0
  const done = (row.successCount || 0) + (row.failCount || 0)
  return Math.round((done / row.totalCount) * 100)
}

const getStatusType = (status) => {
  const map = { PENDING: 'info', RUNNING: 'warning', COMPLETED: 'success', CANCELLED: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待开始', RUNNING: '进行中', COMPLETED: '已完成', CANCELLED: '已取消' }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const handleFirmwareChange = (firmwareId) => {
  const fw = firmwares.value.find(f => f.id === firmwareId)
  if (fw) form.targetVersion = fw.version
}

const handleProductChange = async (productId) => {
  form.firmwareId = null
  form.targetVersion = ''
  form.deviceIds = []
  selectAll.value = false
  productDevices.value = []
  if (!productId) return
  try {
    const res = await deviceApi.getList({ productId })
    productDevices.value = (res?.data || res) || []
  } catch (e) {
    productDevices.value = []
  }
}

const handleSelectAll = (val) => {
  form.deviceIds = val ? productDevices.value.map(d => d.id) : []
}

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await otaApi.getTasks()
    if (res?.code === 200) tasks.value = res.data || []
  } catch (e) {
    console.error('加载任务列表失败:', e)
  } finally {
    loading.value = false
  }
}

const loadFirmwares = async () => {
  try {
    const res = await otaApi.getFirmwares()
    if (res?.code === 200) firmwares.value = (res.data || []).filter(f => f.status === 'PUBLISHED')
  } catch (e) {
    console.error('加载固件列表失败:', e)
  }
}

const loadProducts = async () => {
  try {
    const res = await productApi.getList()
    if (res?.code === 200) products.value = res.data || []
  } catch (e) {
    console.error('加载产品列表失败:', e)
  }
}

const handleAdd = () => {
  Object.assign(form, { name: '', productId: null, firmwareId: null, targetVersion: '', description: '', deviceIds: [] })
  selectAll.value = false
  productDevices.value = []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const res = await otaApi.createTask({
      name: form.name,
      firmwareId: form.firmwareId,
      targetVersion: form.targetVersion,
      description: form.description,
      deviceIds: form.deviceIds,
      totalCount: form.deviceIds.length
    })
    if (res?.code === 200) {
      ElMessage.success('任务创建成功')
      dialogVisible.value = false
      await loadTasks()
    } else {
      ElMessage.error(res?.message || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const handleView = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定取消此任务?', '提示', { type: 'warning' })
  try {
    const res = await otaApi.cancelTask(row.id)
    if (res?.code === 200) {
      ElMessage.success('任务已取消')
      await loadTasks()
      detailVisible.value = false
    } else {
      ElMessage.error(res?.message || '取消失败')
    }
  } catch (e) {
    ElMessage.error('取消失败: ' + (e.message || '未知错误'))
  }
}

onMounted(() => {
  loadTasks()
  loadFirmwares()
  loadProducts()
})
</script>

<style scoped>
.action-card { margin-bottom: 20px; }
.success { color: #52c41a; }
.fail { color: #ff4d4f; }

.task-info {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.info-item .label { width: 100px; color: #909399; }

.progress-stats { margin-top: 20px; text-align: center; }

.stat-item { padding: 16px; }
.stat-value { font-size: 24px; font-weight: 500; }
.stat-value.success { color: #52c41a; }
.stat-value.pending { color: #909399; }
.stat-value.fail { color: #ff4d4f; }
.stat-label { margin-top: 8px; color: #909399; font-size: 14px; }

.device-tip { color: #909399; font-size: 13px; padding: 8px 0; }

.device-select-area { width: 100%; }

.device-select-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.selected-info { font-size: 13px; color: var(--el-color-primary); }

.device-checkbox-list {
  max-height: 240px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 4px 0;
}

.device-checkbox-item {
  padding: 6px 12px;
  transition: background 0.2s;
}

.device-checkbox-item:hover { background: #f5f7fa; }

.device-checkbox-item .dev-name { font-weight: 500; margin-right: 12px; }
.device-checkbox-item .dev-sn { color: #909399; font-size: 12px; margin-right: 12px; font-family: monospace; }
.device-checkbox-item .dev-ver { color: #909399; font-size: 12px; margin-right: 8px; }
</style>
