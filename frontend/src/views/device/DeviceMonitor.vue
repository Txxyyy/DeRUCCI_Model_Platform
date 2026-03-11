<template>
  <div class="device-monitor">
    <!-- 设备头部 -->
    <div class="device-header" v-loading="loading">
      <div class="device-header-left">
        <div class="status-indicator" :class="device.status === 'ONLINE' ? 'indicator-online' : 'indicator-offline'">
          <span class="status-pulse-dot"></span>
        </div>
        <div class="device-title-block">
          <h2 class="device-name">{{ device.name || '设备监控' }}</h2>
          <div class="device-meta-tags">
            <el-tag size="small" type="info">{{ device.productName || '未关联产品' }}</el-tag>
            <el-tag size="small" :type="device.status === 'ONLINE' ? 'success' : 'danger'">
              {{ device.status === 'ONLINE' ? '在线' : '离线' }}
            </el-tag>
            <el-tag size="small" v-if="device.firmwareVersion">固件 {{ device.firmwareVersion }}</el-tag>
            <span class="last-report-text" v-if="device.lastOnlineTime">
              · {{ device.status === 'ONLINE' ? '最近上报' : '断开' }} {{ formatTimeAgo(device.lastOnlineTime) }}
            </span>
          </div>
        </div>
      </div>
      <div class="device-header-actions">
        <el-button size="small" @click="loadDevice">
          <AppIcon name="refresh" :size="16" /> 刷新
        </el-button>
        <el-button size="small" type="primary" @click="handleSendCommand">发送命令</el-button>
        <el-button size="small" type="success" @click="handleOta">OTA升级</el-button>
        <el-button size="small" type="danger" plain @click="handleUnbind">解绑</el-button>
      </div>
    </div>

    <!-- KPI 卡片行 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :span="6">
        <div class="kpi-card">
          <div class="kpi-icon kpi-blue"><AppIcon name="clock" :size="22" /></div>
          <div class="kpi-content">
            <div class="kpi-value">{{ connectDuration }}</div>
            <div class="kpi-label">当前连接时长</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="kpi-card">
          <div class="kpi-icon kpi-green"><AppIcon name="clock" :size="22" /></div>
          <div class="kpi-content">
            <div class="kpi-value">{{ formatTime(device.lastOnlineTime) }}</div>
            <div class="kpi-label">最后上报时间</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="kpi-card">
          <div class="kpi-icon kpi-purple"><AppIcon name="cpu" :size="22" /></div>
          <div class="kpi-content">
            <div class="kpi-value">{{ device.firmwareVersion || '-' }}</div>
            <div class="kpi-label">固件版本</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="kpi-card">
          <div class="kpi-icon kpi-orange"><AppIcon name="key" :size="22" /></div>
          <div class="kpi-content">
            <div class="kpi-value kpi-sn">{{ device.serialNumber || '-' }}</div>
            <div class="kpi-label">序列号 SN</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 实时属性面板 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">实时属性</span>
          <el-button size="small" text @click="loadDevice">
            <AppIcon name="refresh" :size="16" /> 刷新数据
          </el-button>
        </div>
      </template>

      <div v-if="properties.length > 0">
        <!-- 传感器数据组 -->
        <div class="prop-group" v-if="sensorProps.length > 0">
          <div class="prop-group-title prop-group-sensor">传感器数据</div>
          <el-row :gutter="12">
            <el-col v-for="prop in sensorProps" :key="prop.identifier" :xs="12" :sm="8" :md="6">
              <div class="prop-card">
                <div class="prop-name">{{ prop.name || prop.identifier }}</div>
                <div class="prop-value-row">
                  <span class="prop-value">{{ prop.value }}</span>
                  <span class="prop-unit">{{ prop.unit || '' }}</span>
                </div>
                <div class="prop-id">{{ prop.identifier }}</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 控制状态组 -->
        <div class="prop-group" v-if="controlProps.length > 0">
          <div class="prop-group-title prop-group-control">控制状态</div>
          <el-row :gutter="12">
            <el-col v-for="prop in controlProps" :key="prop.identifier" :xs="12" :sm="8" :md="6">
              <div class="prop-card prop-card-control">
                <div class="prop-name">{{ prop.name || prop.identifier }}</div>
                <div class="prop-value-row">
                  <span class="prop-value">{{ prop.value }}</span>
                  <span class="prop-unit">{{ prop.unit || '' }}</span>
                </div>
                <div class="prop-id">{{ prop.identifier }}</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 其他属性 -->
        <div class="prop-group" v-if="otherProps.length > 0">
          <div class="prop-group-title prop-group-other">其他属性</div>
          <el-row :gutter="12">
            <el-col v-for="prop in otherProps" :key="prop.identifier" :xs="12" :sm="8" :md="6">
              <div class="prop-card">
                <div class="prop-name">{{ prop.name || prop.identifier }}</div>
                <div class="prop-value-row">
                  <span class="prop-value">{{ prop.value }}</span>
                  <span class="prop-unit">{{ prop.unit || '' }}</span>
                </div>
                <div class="prop-id">{{ prop.identifier }}</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <el-empty v-else description="暂无属性数据，设备上报后自动显示" :image-size="60" />
    </el-card>

    <!-- 设备信息 + 告警历史 并排 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 设备详情 -->
      <el-col :span="12">
        <el-card class="section-card">
          <template #header><span class="card-title">设备详情</span></template>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">设备名称</span>
              <span class="info-val">{{ device.name || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">产品</span>
              <span class="info-val">{{ device.productName || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">SN码</span>
              <span class="info-val font-mono">{{ device.serialNumber || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">设备 Key</span>
              <span class="info-val font-mono">
                {{ showSecret ? device.deviceKey : '●●●●●●●●' }}
                <el-button type="primary" link size="small" @click="showSecret = !showSecret" style="margin-left: 4px">
                  {{ showSecret ? '隐藏' : '查看' }}
                </el-button>
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">创建时间</span>
              <span class="info-val">{{ formatTime(device.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 告警历史 -->
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">告警历史</span>
              <el-tag size="small" :type="alerts.length > 0 ? 'warning' : 'success'">
                {{ alerts.length > 0 ? alerts.length + ' 条' : '无告警' }}
              </el-tag>
            </div>
          </template>
          <el-timeline v-if="alerts.length > 0" style="padding-left: 4px">
            <el-timeline-item
              v-for="alert in alerts"
              :key="alert.id"
              :timestamp="alert.time"
              :type="alert.level === 'ERROR' ? 'danger' : alert.level === 'WARN' ? 'warning' : 'info'"
              placement="top"
            >
              <div class="alert-item">
                <span class="alert-title">{{ alert.title }}</span>
                <el-tag :type="alert.resolved ? 'success' : 'danger'" size="small">
                  {{ alert.resolved ? '已处理' : '未处理' }}
                </el-tag>
              </div>
              <div class="alert-desc">{{ alert.desc }}</div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无告警记录" :image-size="50" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 发送命令弹窗 -->
    <el-dialog v-model="commandDialogVisible" title="发送命令" width="500px">
      <el-form :model="commandForm" label-width="100px">
        <el-form-item label="命令标识符">
          <el-input v-model="commandForm.type" placeholder="如: setTemperature" />
        </el-form-item>
        <el-form-item label="命令参数">
          <el-input v-model="commandForm.params" type="textarea" rows="4" placeholder='{"value": 26}' />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
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

// 模拟告警数据（实际应从后端获取）
const alerts = ref([])

const commandForm = reactive({ type: '', params: '' })

// 属性分组
const sensorKeywords = ['temp', 'humidity', 'hum', 'pressure', 'sensor', 'heart', 'breath', 'light', 'lux']
const controlKeywords = ['mode', 'status', 'power', 'switch', 'control', 'set', 'enable', 'adjust']

const sensorProps = computed(() =>
  properties.value.filter(p => sensorKeywords.some(k => p.identifier?.toLowerCase().includes(k)))
)
const controlProps = computed(() =>
  properties.value.filter(p =>
    !sensorKeywords.some(k => p.identifier?.toLowerCase().includes(k)) &&
    controlKeywords.some(k => p.identifier?.toLowerCase().includes(k))
  )
)
const otherProps = computed(() =>
  properties.value.filter(p =>
    !sensorKeywords.some(k => p.identifier?.toLowerCase().includes(k)) &&
    !controlKeywords.some(k => p.identifier?.toLowerCase().includes(k))
  )
)

const connectDuration = computed(() => {
  if (device.status !== 'ONLINE' || !device.lastOnlineTime) return '-'
  const diff = Date.now() - new Date(device.lastOnlineTime).getTime()
  const h = Math.floor(diff / 3600000)
  const m = Math.floor((diff % 3600000) / 60000)
  if (h > 0) return `${h}h ${m}m`
  return `${m}分钟`
})

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const formatTimeAgo = (time) => {
  if (!time) return ''
  const diff = Date.now() - new Date(time).getTime()
  const s = Math.floor(diff / 1000)
  if (s < 60) return `${s}秒前`
  const m = Math.floor(s / 60)
  if (m < 60) return `${m}分钟前`
  const h = Math.floor(m / 60)
  if (h < 24) return `${h}小时前`
  return `${Math.floor(h / 24)}天前`
}

const loadDevice = async () => {
  if (!deviceId) return
  loading.value = true
  try {
    const res = await deviceApi.getById(deviceId)
    if (res?.code === 200 && res.data) {
      Object.assign(device, res.data)
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
        } catch {
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
  if (!commandForm.type) { ElMessage.warning('请输入命令标识符'); return }
  sending.value = true
  setTimeout(() => {
    ElMessage.success(`命令「${commandForm.type}」已发送`)
    commandDialogVisible.value = false
    sending.value = false
  }, 800)
}

const handleOta = () => { router.push('/ota/tasks') }

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

onMounted(() => { loadDevice() })
</script>

<style scoped>
.device-monitor {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

/* ====== 设备头部 ====== */
.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  border: 1px solid #ebeef5;
}

.device-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.device-header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 在线状态指示器 */
.status-indicator {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.indicator-online {
  background: rgba(16, 185, 129, 0.1);
}

.indicator-offline {
  background: rgba(239, 68, 68, 0.1);
}

.status-pulse-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
}

.indicator-online .status-pulse-dot {
  background: #10B981;
  box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  animation: pulse-green 2s infinite;
}

.indicator-offline .status-pulse-dot {
  background: #EF4444;
}

@keyframes pulse-green {
  0%   { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70%  { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
  100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

.device-title-block {}

.device-name {
  font-size: 20px;
  font-weight: 700;
  color: #0F172A;
  margin: 0 0 6px 0;
}

.device-meta-tags {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.last-report-text {
  font-size: 13px;
  color: #909399;
}

/* ====== KPI 卡片行 ====== */
.kpi-row {
  margin-bottom: 16px;
}

.kpi-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  border: 1px solid #ebeef5;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.kpi-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.kpi-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-blue   { background: rgba(30, 77, 163, 0.08); color: #1E4DA3; }
.kpi-green  { background: rgba(16, 185, 129, 0.08); color: #10B981; }
.kpi-purple { background: rgba(139, 92, 246, 0.08); color: #8B5CF6; }
.kpi-orange { background: rgba(245, 158, 11, 0.08); color: #F59E0B; }

.kpi-content {}

.kpi-value {
  font-size: 16px;
  font-weight: 700;
  color: #0F172A;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.kpi-sn {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
}

.kpi-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

/* ====== 属性面板 ====== */
.section-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.prop-group {
  margin-bottom: 20px;
}

.prop-group-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
  padding-left: 8px;
  border-left: 3px solid #8FA3BF;
}

.prop-group-sensor { border-left-color: #60AEFC; }
.prop-group-control { border-left-color: #10B981; }
.prop-group-other   { border-left-color: #8FA3BF; }

.prop-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 14px 16px;
  margin-bottom: 12px;
  text-align: center;
  transition: all 0.15s;
}

.prop-card:hover {
  border-color: #c0c4cc;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.prop-card-control {
  background: #f0f9ff;
  border-color: #bae6fd;
}

.prop-name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.prop-value-row {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
}

.prop-value {
  font-size: 24px;
  font-weight: 700;
  color: #0F172A;
}

.prop-unit {
  font-size: 13px;
  color: #909399;
}

.prop-id {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
  font-family: monospace;
}

/* ====== 设备详情信息列表 ====== */
.info-list { display: flex; flex-direction: column; }

.info-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-row:last-child { border-bottom: none; }

.info-label {
  width: 90px;
  color: #909399;
  font-size: 13px;
  flex-shrink: 0;
}

.info-val {
  flex: 1;
  font-size: 13px;
  color: #303133;
  display: flex;
  align-items: center;
}

.font-mono {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 12px;
}

/* ====== 告警 ====== */
.alert-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.alert-title {
  font-weight: 500;
  font-size: 13px;
  color: #303133;
}

.alert-desc {
  font-size: 12px;
  color: #909399;
}
</style>
