<template>
  <div class="device-monitor">
    <!-- 设备基本信息 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="info-card">
          <template #header>
            <span>设备信息</span>
          </template>
          <div class="info-item">
            <span class="label">设备名称:</span>
            <span class="value">{{ device.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">产品:</span>
            <span class="value">{{ device.productName }}</span>
          </div>
          <div class="info-item">
            <span class="label">SN码:</span>
            <span class="value">{{ device.sn }}</span>
          </div>
          <div class="info-item">
            <span class="label">密钥:</span>
            <span class="value">{{ showSecret ? device.deviceSecret : '********' }}</span>
            <el-button type="primary" link @click="showSecret = !showSecret">
              {{ showSecret ? '隐藏' : '查看' }}
            </el-button>
          </div>
          <div class="info-item">
            <span class="label">创建时间:</span>
            <span class="value">{{ device.createTime }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="status-card">
          <template #header>
            <span>设备状态</span>
          </template>
          <div class="info-item">
            <span class="label">在线状态:</span>
            <span class="value">
              <span :class="['status-dot', device.online ? 'online' : 'offline']"></span>
              {{ device.online ? '在线' : '离线' }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">固件版本:</span>
            <span class="value">{{ device.firmwareVersion }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后上线:</span>
            <span class="value">{{ device.lastOnlineTime }}</span>
          </div>
          <div class="info-item">
            <span class="label">设备影子:</span>
            <span class="value">
              <el-tag :type="device.shadowSynced ? 'success' : 'warning'" size="small">
                {{ device.shadowSynced ? '已同步' : '未同步' }}
              </el-tag>
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="action-card">
          <template #header>
            <span>操作</span>
          </template>
          <div class="action-buttons">
            <el-button type="primary" @click="handleSendCommand">发送命令</el-button>
            <el-button type="success" @click="handleOta">OTA升级</el-button>
            <el-button type="danger" @click="handleUnbind">解绑设备</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 实时属性 -->
    <el-card class="property-card">
      <template #header>
        <div class="card-header">
          <span>实时属性</span>
          <div>
            <el-button size="small" @click="fetchProperties">刷新</el-button>
          </div>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col
          v-for="prop in properties"
          :key="prop.identifier"
          :xs="12"
          :sm="8"
          :md="6"
        >
          <div class="property-item">
            <div class="property-name">{{ prop.name }}</div>
            <div class="property-value">
              {{ prop.value }}
              <span class="property-unit">{{ prop.unit }}</span>
            </div>
            <div class="property-update">
              <span :class="['update-dot', prop.updated ? 'updated' : '']"></span>
              {{ prop.updated ? '已更新' : '未更新' }}
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 设备日志 -->
    <el-card class="log-card">
      <template #header>
        <div class="card-header">
          <span>设备日志</span>
          <div>
            <el-button size="small" @click="handleExportLog">导出</el-button>
          </div>
        </div>
      </template>
      <el-table :data="logs" stripe size="small">
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getLogType(row.type)" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" />
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="logPagination.page"
          :total="logPagination.total"
          :page-size="logPagination.pageSize"
          layout="total, prev, pager, next"
        />
      </div>
    </el-card>

    <!-- 发送命令弹窗 -->
    <el-dialog v-model="commandDialogVisible" title="发送命令" width="500px">
      <el-form :model="commandForm" label-width="100px">
        <el-form-item label="命令类型">
          <el-select v-model="commandForm.type" style="width: 100%">
            <el-option label="设置温度" value="setTemperature" />
            <el-option label="设置硬度" value="setHardness" />
            <el-option label="启动按摩" value="startMassage" />
            <el-option label="关闭设备" value="powerOff" />
          </el-select>
        </el-form-item>
        <el-form-item label="命令参数">
          <el-input v-model="commandForm.params" type="textarea" rows="3" placeholder="请输入命令参数JSON" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="commandDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSendCommand" :loading="sending">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const deviceId = route.query.id

const showSecret = ref(false)
const commandDialogVisible = ref(false)
const sending = ref(false)

const device = reactive({
  id: deviceId,
  name: '卧室床垫-01',
  productName: '智能床垫',
  sn: 'SN123456789001',
  deviceSecret: 'SEC_ABCD1234567890',
  createTime: '2024-01-01 10:00:00',
  online: true,
  firmwareVersion: 'v1.2.3',
  lastOnlineTime: '2024-01-15 14:30:00',
  shadowSynced: true
})

const properties = ref([
  { identifier: 'current_temperature', name: '温度', value: '25.5', unit: '℃', updated: true },
  { identifier: 'target_temperature', name: '目标温度', value: '26', unit: '℃', updated: false },
  { identifier: 'hardness_level', name: '硬度', value: '5', unit: '-', updated: true },
  { identifier: 'vibration_level', name: '振动', value: '2', unit: '-', updated: false },
  { identifier: 'sleep_mode', name: '睡眠模式', value: '深度睡眠', unit: '-', updated: true },
  { identifier: 'bed_position', name: '床位', value: '左侧', unit: '-', updated: false }
])

const logs = ref([
  { time: '14:30:25', type: '属性', content: 'current_temperature = 25.5' },
  { time: '14:30:20', type: '事件', content: 'sleep_completed, duration=8h' },
  { time: '14:25:00', type: '命令', content: 'setTemperature(26) 成功' },
  { time: '14:20:15', type: '告警', content: 'temperature_alarm, temp=85' },
  { time: '14:15:00', type: '属性', content: 'target_temperature = 26' },
  { time: '14:10:00', type: '事件', content: 'hardness_alarm, hardness=10' },
  { time: '14:05:00', type: '命令', content: 'startMassage(1) 成功' }
])

const logPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 50
})

const commandForm = reactive({
  type: '',
  params: ''
})

const getLogType = (type) => {
  const map = { '属性': 'primary', '事件': 'info', '命令': 'success', '告警': 'danger' }
  return map[type] || 'info'
}

const fetchProperties = () => {
  ElMessage.success('属性已刷新')
}

const handleSendCommand = () => {
  commandForm.type = ''
  commandForm.params = ''
  commandDialogVisible.value = true
}

const confirmSendCommand = () => {
  if (!commandForm.type) {
    ElMessage.warning('请选择命令类型')
    return
  }
  sending.value = true
  setTimeout(() => {
    logs.value.unshift({
      time: new Date().toLocaleTimeString(),
      type: '命令',
      content: `${commandForm.type}(${commandForm.params || '-'}) 已发送`
    })
    ElMessage.success('命令已发送')
    commandDialogVisible.value = false
    sending.value = false
  }, 1000)
}

const handleOta = () => {
  router.push('/ota/tasks')
}

const handleUnbind = () => {
  ElMessage.info('解绑设备功能开发中')
}

const handleExportLog = () => {
  ElMessage.success('日志导出成功')
}
</script>

<style scoped>
.info-card, .status-card, .property-card, .log-card { margin-bottom: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }

.info-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child { border-bottom: none; }

.info-item .label {
  width: 100px;
  color: #909399;
}

.info-item .value {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.online { background: #52c41a; }
.status-dot.offline { background: #999; }

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.property-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: center;
}

.property-name {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.property-value {
  font-size: 24px;
  font-weight: 500;
  color: #303133;
}

.property-unit {
  font-size: 14px;
  color: #909399;
}

.property-update {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.update-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #999;
}

.update-dot.updated { background: #52c41a; }

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
