<template>
  <div class="device-monitor">
    <!-- 设备基本信息 -->
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8">
        <el-card class="info-card">
          <template #header><span>设备信息</span></template>
          <div class="info-item">
            <span class="label">设备名称:</span>
            <span class="value">{{ device.name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">产品:</span>
            <span class="value">{{ device.productName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">SN码:</span>
            <span class="value">{{ device.serialNumber || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">设备Key:</span>
            <span class="value">{{ showSecret ? device.deviceKey : '********' }}</span>
            <el-button type="primary" link @click="showSecret = !showSecret">
              {{ showSecret ? '隐藏' : '查看' }}
            </el-button>
          </div>
          <div class="info-item">
            <span class="label">创建时间:</span>
            <span class="value">{{ formatTime(device.createTime) }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="status-card">
          <template #header><span>设备状态</span></template>
          <div class="info-item">
            <span class="label">在线状态:</span>
            <span class="value">
              <span :class="['status-dot', device.status === 'ONLINE' ? 'online' : 'offline']"></span>
              {{ device.status === 'ONLINE' ? '在线' : '离线' }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">固件版本:</span>
            <span class="value">{{ device.firmwareVersion || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后上线:</span>
            <span class="value">{{ formatTime(device.lastOnlineTime) }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="action-card">
          <template #header><span>操作</span></template>
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
          <el-button size="small" @click="loadDevice">刷新</el-button>
        </div>
      </template>
      <el-row :gutter="20" v-if="properties.length > 0">
        <el-col v-for="prop in properties" :key="prop.identifier" :xs="12" :sm="8" :md="6">
          <div class="property-item">
            <div class="property-name">{{ prop.name || prop.identifier }}</div>
            <div class="property-value">
              {{ prop.value }}
              <span class="property-unit">{{ prop.unit || '' }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无属性数据" :image-size="60" />
    </el-card>

    <!-- 发送命令弹窗 -->
    <el-dialog v-model="commandDialogVisible" title="发送命令" width="500px">
      <el-form :model="commandForm" label-width="100px">
        <el-form-item label="命令标识符">
          <el-input v-model="commandForm.type" placeholder="如: setTemperature" />
        </el-form-item>
        <el-form-item label="命令参数">
          <el-input v-model="commandForm.params" type="textarea" rows="3" placeholder='请输入参数JSON，如: {"value": 26}' />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { deviceApi } from '@/api/device'

const route = useRoute()
const router = useRouter()
const deviceId = route.query.id

const loading = ref(false)
const showSecret = ref(false)
const commandDialogVisible = ref(false)
const sending = ref(false)
const device = reactive({})
const properties = ref([])

const commandForm = reactive({ type: '', params: '' })

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadDevice = async () => {
  if (!deviceId) return
  loading.value = true
  try {
    const res = await deviceApi.getById(deviceId)
    if (res?.code === 200 && res.data) {
      Object.assign(device, res.data)
      // 解析 properties JSON 字段
      if (res.data.properties) {
        try {
          const parsed = JSON.parse(res.data.properties)
          if (Array.isArray(parsed)) {
            properties.value = parsed
          } else if (typeof parsed === 'object') {
            properties.value = Object.entries(parsed).map(([k, v]) => ({
              identifier: k, name: k, value: String(v)
            }))
          }
        } catch (e) {
          properties.value = []
        }
      }
    }
  } catch (e) {
    console.error('加载设备详情失败:', e)
  } finally {
    loading.value = false
  }
}

const handleSendCommand = () => {
  commandForm.type = ''
  commandForm.params = ''
  commandDialogVisible.value = true
}

const confirmSendCommand = () => {
  if (!commandForm.type) {
    ElMessage.warning('请输入命令标识符')
    return
  }
  sending.value = true
  setTimeout(() => {
    ElMessage.success(`命令 ${commandForm.type} 已发送`)
    commandDialogVisible.value = false
    sending.value = false
  }, 800)
}

const handleOta = () => {
  router.push('/ota/tasks')
}

const handleUnbind = async () => {
  await ElMessageBox.confirm('确定解绑该设备？此操作不可恢复。', '确认解绑', { type: 'warning' })
  try {
    await deviceApi.delete(deviceId)
    ElMessage.success('设备已解绑')
    router.push('/devices')
  } catch (e) {
    ElMessage.error('解绑失败: ' + (e.message || '未知错误'))
  }
}

onMounted(() => {
  loadDevice()
})
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
.info-item .label { width: 100px; color: #909399; flex-shrink: 0; }
.info-item .value { flex: 1; display: flex; align-items: center; gap: 8px; }

.status-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.status-dot.online { background: #52c41a; }
.status-dot.offline { background: #999; }

.action-buttons { display: flex; flex-direction: column; gap: 12px; }

.property-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  text-align: center;
}
.property-name { color: #909399; font-size: 14px; margin-bottom: 8px; }
.property-value { font-size: 24px; font-weight: 500; color: #303133; }
.property-unit { font-size: 14px; color: #909399; }
</style>
