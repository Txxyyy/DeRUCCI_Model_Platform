<template>
  <div class="ota-task-list">
    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-button type="primary" @click="handleAdd">创建任务</el-button>
    </el-card>

    <!-- 任务列表 -->
    <el-card>
      <el-table :data="tasks" stripe v-loading="loading">
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
            <el-button type="danger" link @click="handleCancel(row)" v-if="row.status === 'RUNNING' || row.status === 'PENDING'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建任务弹窗 -->
    <el-dialog v-model="dialogVisible" title="创建升级任务" width="700px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入任务名称" />
        </el-form-item>

        <el-divider>选择固件</el-divider>
        <el-form-item label="选择固件" prop="firmwareId">
          <el-select v-model="form.firmwareId" placeholder="请选择固件" style="width: 100%" @change="handleFirmwareChange">
            <el-option
              v-for="f in firmwares"
              :key="f.id"
              :label="`${getProductName(f.productId)} - ${f.version}`"
              :value="f.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="升级说明">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入升级说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">发布任务</el-button>
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
        <el-button type="danger" @click="handleCancel(currentTask)" v-if="currentTask.status === 'RUNNING' || currentTask.status === 'PENDING'">取消任务</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { otaApi } from '@/api/ota'
import { productApi } from '@/api/product'

const tasks = ref([])
const firmwares = ref([])
const products = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const currentTask = ref({})

const form = reactive({
  name: '',
  firmwareId: null,
  targetVersion: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  firmwareId: [{ required: true, message: '请选择固件', trigger: 'change' }]
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

const getProductName = (productId) => {
  return products.value.find(p => p.id === productId)?.name || '-'
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
  Object.assign(form, { name: '', firmwareId: null, targetVersion: '', description: '' })
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
      description: form.description
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
</style>
