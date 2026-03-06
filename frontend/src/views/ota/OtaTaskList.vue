<template>
  <div class="ota-task-list">
    <!-- 操作栏 -->
    <el-card class="action-card">
      <el-button type="primary" @click="handleAdd">创建任务</el-button>
    </el-card>

    <!-- 任务列表 -->
    <el-card>
      <el-table :data="tasks" stripe>
        <el-table-column prop="name" label="任务名称" min-width="180" />
        <el-table-column prop="firmwareVersion" label="目标版本" width="100" />
        <el-table-column label="目标设备" width="100">
          <template #default="{ row }">
            {{ row.totalCount }}台
          </template>
        </el-table-column>
        <el-table-column label="进度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="row.progress === 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="成功/失败" width="100">
          <template #default="{ row }">
            <span class="success">{{ row.successCount }}</span> /
            <span class="fail">{{ row.failCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button
              type="warning"
              link
              @click="handlePause(row)"
              v-if="row.status === 'RUNNING'"
            >暂停</el-button>
            <el-button
              type="danger"
              link
              @click="handleCancel(row)"
              v-if="row.status === 'RUNNING' || row.status === 'PAUSED'"
            >取消</el-button>
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
          <el-select v-model="form.firmwareId" placeholder="请选择固件" style="width: 100%">
            <el-option
              v-for="f in firmwares"
              :key="f.id"
              :label="`${f.productName} - ${f.version}`"
              :value="f.id"
            />
          </el-select>
        </el-form-item>

        <el-divider>目标设备</el-divider>
        <el-form-item label="设备范围">
          <el-radio-group v-model="form.deviceScope">
            <el-radio label="all">指定产品所有设备</el-radio>
            <el-radio label="specific">指定设备</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.deviceScope === 'all'" label="选择产品">
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
          <div class="device-count" v-if="form.productId">目标数量: {{ getDeviceCount(form.productId) }}台</div>
        </el-form-item>

        <el-divider>升级策略</el-divider>
        <el-form-item label="升级策略">
          <el-radio-group v-model="form.strategy">
            <el-radio label="immediately">立即升级</el-radio>
            <el-radio label="timed">定时升级</el-radio>
            <el-radio label="batch">分批升级</el-radio>
            <el-radio label="careful">谨慎升级</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.strategy === 'timed'" label="升级时间">
          <el-date-picker
            v-model="form.scheduledTime"
            type="datetime"
            placeholder="选择升级时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="form.strategy === 'batch'" label="分批设置">
          <el-input v-model="form.batchConfig" placeholder="如: 每次5台，间隔10分钟" />
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
    <el-dialog v-model="detailVisible" title="任务详情" width="800px">
      <div class="task-info">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">任务名称:</span>
              <span class="value">{{ currentTask.name }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">状态:</span>
              <el-tag :type="getStatusType(currentTask.status)" size="small">
                {{ getStatusText(currentTask.status) }}
              </el-tag>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ currentTask.createTime }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">发起人:</span>
              <span class="value">{{ currentTask.creator }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-divider>升级进度</el-divider>
      <el-progress :percentage="currentTask.progress" :stroke-width="20" />
      <el-row :gutter="20" class="progress-stats">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value success">{{ currentTask.successCount }}</div>
            <div class="stat-label">升级成功</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value running">{{ currentTask.runningCount }}</div>
            <div class="stat-label">升级中</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value pending">{{ currentTask.pendingCount }}</div>
            <div class="stat-label">待升级</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-value fail">{{ currentTask.failCount }}</div>
            <div class="stat-label">升级失败</div>
          </div>
        </el-col>
      </el-row>

      <el-divider>失败设备清单</el-divider>
      <el-table :data="failedDevices" size="small" v-if="failedDevices.length > 0">
        <el-table-column prop="name" label="设备名称" />
        <el-table-column prop="sn" label="SN码" />
        <el-table-column prop="failReason" label="失败原因" />
        <el-table-column prop="failTime" label="失败时间" width="160" />
      </el-table>
      <el-empty v-else description="暂无失败设备" />

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="warning" @click="handlePause(currentTask)" v-if="currentTask.status === 'RUNNING'">暂停</el-button>
        <el-button type="danger" @click="handleCancel(currentTask)" v-if="currentTask.status === 'RUNNING' || currentTask.status === 'PAUSED'">取消任务</el-button>
        <el-button type="success" @click="handleRollback(currentTask)">回滚</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tasks = ref([
  {
    id: 1,
    name: '智能床垫v1.2.3版本升级',
    firmwareVersion: 'v1.2.3',
    totalCount: 50,
    successCount: 15,
    failCount: 5,
    runningCount: 5,
    pendingCount: 25,
    progress: 30,
    status: 'RUNNING',
    createTime: '2024-01-15 10:00:00',
    creator: 'admin'
  },
  {
    id: 2,
    name: '智能枕头v1.1.0版本升级',
    firmwareVersion: 'v1.1.0',
    totalCount: 30,
    successCount: 30,
    failCount: 0,
    runningCount: 0,
    pendingCount: 0,
    progress: 100,
    status: 'COMPLETED',
    createTime: '2024-01-10 10:00:00',
    creator: 'admin'
  }
])

const firmwares = ref([
  { id: 1, productName: '智能床垫', version: 'v1.2.3', fileSize: '2.5MB' },
  { id: 2, productName: '智能床垫', version: 'v1.2.2', fileSize: '2.4MB' },
  { id: 3, productName: '智能枕头', version: 'v1.1.0', fileSize: '1.8MB' }
])

const products = ref([
  { id: 1, name: '智能床垫', deviceCount: 50 },
  { id: 2, name: '智能枕头', deviceCount: 30 },
  { id: 3, name: '智能沙发', deviceCount: 20 }
])

const dialogVisible = ref(false)
const detailVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const currentTask = ref({})

const form = reactive({
  name: '',
  firmwareId: null,
  deviceScope: 'all',
  productId: null,
  strategy: 'immediately',
  scheduledTime: '',
  batchConfig: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  firmwareId: [{ required: true, message: '请选择固件', trigger: 'change' }]
}

const failedDevices = ref([
  { name: '卧室床垫-05', sn: 'SN123456789005', failReason: '固件下载失败', failTime: '2024-01-15 14:30' },
  { name: '卧室床垫-12', sn: 'SN123456789012', failReason: '固件校验失败', failTime: '2024-01-15 14:35' },
  { name: '客厅床垫-03', sn: 'SN123456789023', failReason: '设备无响应', failTime: '2024-01-15 14:40' }
])

const getStatusType = (status) => {
  const map = { PENDING: 'info', RUNNING: 'warning', PAUSED: 'warning', COMPLETED: 'success', CANCELLED: 'info', FAILED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待开始', RUNNING: '进行中', PAUSED: '已暂停', COMPLETED: '已完成', CANCELLED: '已取消', FAILED: '失败' }
  return map[status] || status
}

const getDeviceCount = (productId) => {
  return products.value.find(p => p.id === productId)?.deviceCount || 0
}

const handleAdd = () => {
  Object.assign(form, {
    name: '', firmwareId: null, deviceScope: 'all', productId: null,
    strategy: 'immediately', scheduledTime: '', batchConfig: '', description: ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  setTimeout(() => {
    const fw = firmwares.value.find(f => f.id === form.firmwareId)
    tasks.value.unshift({
      id: Date.now(),
      name: form.name,
      firmwareVersion: fw?.version || '',
      totalCount: form.productId ? getDeviceCount(form.productId) : 0,
      successCount: 0,
      failCount: 0,
      runningCount: 0,
      pendingCount: form.productId ? getDeviceCount(form.productId) : 0,
      progress: 0,
      status: 'PENDING',
      createTime: new Date().toLocaleString(),
      creator: 'admin'
    })
    ElMessage.success('任务创建成功')
    dialogVisible.value = false
    submitLoading.value = false
  }, 1000)
}

const handleView = (row) => {
  currentTask.value = row
  detailVisible.value = true
}

const handlePause = (row) => {
  row.status = row.status === 'RUNNING' ? 'PAUSED' : 'RUNNING'
  ElMessage.success(row.status === 'PAUSED' ? '任务已暂停' : '任务已恢复')
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定取消此任务?', '提示', { type: 'warning' })
  row.status = 'CANCELLED'
  ElMessage.success('任务已取消')
}

const handleRollback = (row) => {
  ElMessage.info('回滚功能开发中')
}
</script>

<style scoped>
.action-card { margin-bottom: 20px; }

.success { color: #52c41a; }
.fail { color: #ff4d4f; }

.device-count {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}

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

.info-item .label {
  width: 100px;
  color: #909399;
}

.progress-stats {
  margin-top: 20px;
  text-align: center;
}

.stat-item {
  padding: 16px;
}

.stat-value {
  font-size: 24px;
  font-weight: 500;
}

.stat-value.success { color: #52c41a; }
.stat-value.running { color: #fa8c16; }
.stat-value.pending { color: #909399; }
.stat-value.fail { color: #ff4d4f; }

.stat-label {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
