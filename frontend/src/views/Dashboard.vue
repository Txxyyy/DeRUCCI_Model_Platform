<template>
  <div class="dashboard">
    <!-- KPI 卡片行 -->
    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card" v-loading="loading" @click="$router.push('/products')">
          <div class="stat-icon-wrap" style="color: #1E4DA3">
            <AppIcon name="package" :size="22" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.publishedCount }}</div>
            <div class="stat-label">已发布产品</div>
            <div class="stat-trend" v-if="stats.productCount > 0">
              <span class="trend-text">共 {{ stats.productCount }} 个产品</span>
            </div>
          </div>
          <div class="stat-sparkline">
            <div class="sparkline-bars">
              <div v-for="(h, i) in sparklineData.products" :key="i" class="sparkline-bar" :style="{ height: h + '%' }"></div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" v-loading="loading" @click="$router.push('/devices')">
          <div class="stat-icon-wrap" style="color: #0EA5E9">
            <AppIcon name="monitor" :size="22" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.deviceCount }}</div>
            <div class="stat-label">设备总数</div>
            <div class="stat-trend">
              <span class="trend-text">{{ stats.onlineCount }} 台在线</span>
            </div>
          </div>
          <div class="stat-sparkline">
            <div class="sparkline-bars">
              <div v-for="(h, i) in sparklineData.devices" :key="i" class="sparkline-bar bar-blue" :style="{ height: h + '%' }"></div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" v-loading="loading">
          <div class="stat-icon-wrap" style="color: #10B981">
            <AppIcon name="connection" :size="22" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.onlineCount }}</div>
            <div class="stat-label">在线设备</div>
            <div class="stat-trend">
              <span class="trend-online">{{ onlineRate }}% 在线率</span>
            </div>
          </div>
          <div class="stat-sparkline">
            <div class="sparkline-bars">
              <div v-for="(h, i) in sparklineData.online" :key="i" class="sparkline-bar bar-green" :style="{ height: h + '%' }"></div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" v-loading="loading">
          <div class="stat-icon-wrap" style="color: #F59E0B">
            <AppIcon name="alert-triangle" :size="22" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.deviceCount - stats.onlineCount }}</div>
            <div class="stat-label">离线设备</div>
            <div class="stat-trend">
              <span class="trend-offline" v-if="stats.deviceCount > 0">
                {{ (100 - parseFloat(onlineRate)).toFixed(1) }}% 离线率
              </span>
            </div>
          </div>
          <div class="stat-sparkline">
            <div class="sparkline-bars">
              <div v-for="(h, i) in sparklineData.offline" :key="i" class="sparkline-bar bar-orange" :style="{ height: h + '%' }"></div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表行 -->
    <el-row :gutter="16" style="margin-top: 16px" class="chart-row">
      <!-- 设备状态分布 -->
      <el-col :span="12" class="chart-col">
        <el-card class="equal-height-card">
          <template #header>
            <div class="card-header-row">
              <span class="card-title">设备状态分布</span>
              <el-tag size="small" :type="onlineRate >= 70 ? 'success' : onlineRate >= 40 ? 'warning' : 'danger'">
                {{ onlineRate >= 70 ? '健康' : onlineRate >= 40 ? '注意' : '异常' }}
              </el-tag>
            </div>
          </template>
          <div class="donut-chart-wrap">
            <!-- SVG 环形图 -->
            <svg class="donut-svg" viewBox="0 0 200 200">
              <!-- 背景圆环 -->
              <circle cx="100" cy="100" r="70" fill="none" stroke="#EAECF0" stroke-width="24" />
              <!-- 在线弧 -->
              <circle
                cx="100" cy="100" r="70"
                fill="none"
                stroke="#10B981"
                stroke-width="24"
                stroke-linecap="round"
                :stroke-dasharray="`${onlineArc} ${offlineArc}`"
                stroke-dashoffset="110"
                transform="rotate(-90 100 100)"
                style="transition: stroke-dasharray 0.8s ease"
              />
              <!-- 离线弧 -->
              <circle
                cx="100" cy="100" r="70"
                fill="none"
                stroke="#EF4444"
                stroke-width="24"
                stroke-linecap="round"
                :stroke-dasharray="`${offlineArc > 4 ? offlineArc - 4 : 0} 999`"
                :stroke-dashoffset="-(onlineArc - 110)"
                transform="rotate(-90 100 100)"
                style="transition: all 0.8s ease"
              />
              <!-- 中心文字 -->
              <text x="100" y="94" text-anchor="middle" font-size="28" font-weight="700" fill="#0F172A" font-family="Inter, sans-serif">{{ onlineRate }}</text>
              <text x="100" y="115" text-anchor="middle" font-size="12" fill="#9CA3AF">在线率%</text>
            </svg>
            <div class="donut-legend">
              <div class="legend-item">
                <span class="legend-dot" style="background:#10B981"></span>
                <span class="legend-label">在线</span>
                <span class="legend-val">{{ stats.onlineCount }}</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot" style="background:#EF4444"></span>
                <span class="legend-label">离线</span>
                <span class="legend-val">{{ stats.deviceCount - stats.onlineCount }}</span>
              </div>
              <div class="legend-item">
                <span class="legend-dot" style="background:#EAECF0"></span>
                <span class="legend-label">总计</span>
                <span class="legend-val">{{ stats.deviceCount }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最近活动 -->
      <el-col :span="12" class="chart-col">
        <el-card class="equal-height-card activity-card">
          <template #header>
            <div class="card-header-row">
              <span class="card-title">最近活动</span>
              <el-button size="small" text :loading="activityLoading" @click="refreshActivity" title="刷新">
                <AppIcon name="refresh-cw" :size="14" />
              </el-button>
            </div>
          </template>
          <div class="activity-scroll">
            <el-timeline v-if="recentItems.length > 0" style="padding-left: 4px">
              <el-timeline-item
                v-for="item in recentItems"
                :key="item.key"
                :timestamp="formatTime(item.createTime)"
                :type="item.type === 'product' ? 'primary' : item.type === 'device' ? 'success' : 'info'"
                placement="top"
              >
                <div class="timeline-item">
                  <AppIcon :name="item.type === 'product' ? 'package' : 'monitor'" :size="15" class="timeline-icon" />
                  <span class="timeline-text">{{ item.label }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无活动记录" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速入口 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card>
          <template #header><span class="card-title">快速入口</span></template>
          <div class="quick-entries">
            <div class="quick-entry" @click="$router.push('/products')">
              <div class="qe-icon" style="color: #1E4DA3; background: rgba(30,77,163,0.08)">
                <AppIcon name="package" :size="22" />
              </div>
              <div class="qe-label">产品管理</div>
              <div class="qe-desc">创建和管理产品</div>
            </div>
            <div class="quick-entry" @click="$router.push('/devices')">
              <div class="qe-icon" style="color: #0EA5E9; background: rgba(14,165,233,0.08)">
                <AppIcon name="monitor" :size="22" />
              </div>
              <div class="qe-label">设备管理</div>
              <div class="qe-desc">注册和监控设备</div>
            </div>
            <div class="quick-entry" @click="$router.push('/products/category-templates')">
              <div class="qe-icon" style="color: #8B5CF6; background: rgba(139,92,246,0.08)">
                <AppIcon name="grid" :size="22" />
              </div>
              <div class="qe-label">品类模板</div>
              <div class="qe-desc">标准物模型模板库</div>
            </div>
            <div class="quick-entry" @click="$router.push('/ota/firmware')">
              <div class="qe-icon" style="color: #F59E0B; background: rgba(245,158,11,0.08)">
                <AppIcon name="upload" :size="22" />
              </div>
              <div class="qe-label">OTA固件</div>
              <div class="qe-desc">上传和管理固件</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { productApi } from '@/api/product'
import { deviceApi } from '@/api/device'

const loading = ref(false)
const activityLoading = ref(false)
const stats = reactive({ publishedCount: 0, productCount: 0, deviceCount: 0, onlineCount: 0 })
const recentItems = ref([])

const onlineRate = computed(() => {
  if (!stats.deviceCount) return 0
  return parseFloat(((stats.onlineCount / stats.deviceCount) * 100).toFixed(1))
})

// SVG环形图弧长计算（周长 = 2πr ≈ 440）
const CIRCUMFERENCE = 440
const onlineArc = computed(() => stats.deviceCount ? (stats.onlineCount / stats.deviceCount) * CIRCUMFERENCE : 0)
const offlineArc = computed(() => CIRCUMFERENCE - onlineArc.value)

// 迷你趋势图数据
const sparklineData = reactive({
  products: [30, 45, 40, 60, 55, 75, 80],
  devices:  [20, 35, 50, 45, 70, 65, 90],
  online:   [50, 60, 55, 70, 65, 80, 75],
  offline:  [50, 40, 45, 30, 35, 20, 25]
})

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadStats = async () => {
  loading.value = true
  try {
    const [productsRes, devicesRes] = await Promise.allSettled([
      productApi.getList(),
      deviceApi.getList()
    ])

    if (productsRes.status === 'fulfilled' && productsRes.value?.code === 200) {
      const products = productsRes.value.data || []
      stats.productCount = products.length
      stats.publishedCount = products.filter(p => p.status === 'PUBLISHED').length
      products.slice(0, 3).forEach(p => {
        recentItems.value.push({ key: 'p_' + p.id, label: `产品「${p.name}」已创建`, createTime: p.createTime, type: 'product' })
      })
    }

    if (devicesRes.status === 'fulfilled' && devicesRes.value?.code === 200) {
      const devices = devicesRes.value.data || []
      stats.deviceCount = devices.length
      stats.onlineCount = devices.filter(d => d.status === 'ONLINE').length
      devices.slice(0, 3).forEach(d => {
        recentItems.value.push({ key: 'd_' + d.id, label: `设备「${d.name}」已注册`, createTime: d.createTime, type: 'device' })
      })
    }

    recentItems.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    recentItems.value = recentItems.value.slice(0, 6)
  } catch (e) {
    console.error('加载统计数据失败:', e)
  } finally {
    loading.value = false
  }
}

const refreshActivity = async () => {
  activityLoading.value = true
  recentItems.value = []
  try {
    const [productsRes, devicesRes] = await Promise.allSettled([
      productApi.getList(),
      deviceApi.getList()
    ])
    if (productsRes.status === 'fulfilled' && productsRes.value?.code === 200) {
      productsRes.value.data?.slice(0, 3).forEach(p => {
        recentItems.value.push({ key: 'p_' + p.id, label: `产品「${p.name}」已创建`, createTime: p.createTime, type: 'product' })
      })
    }
    if (devicesRes.status === 'fulfilled' && devicesRes.value?.code === 200) {
      devicesRes.value.data?.slice(0, 3).forEach(d => {
        recentItems.value.push({ key: 'd_' + d.id, label: `设备「${d.name}」已注册`, createTime: d.createTime, type: 'device' })
      })
    }
    recentItems.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    recentItems.value = recentItems.value.slice(0, 6)
  } catch (e) {
    console.error('刷新活动记录失败:', e)
  } finally {
    activityLoading.value = false
  }
}

onMounted(() => { loadStats() })
</script>

<style scoped>
.dashboard { padding: 20px; }

/* ====== KPI 卡片 ====== */
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 12px;
  padding: 18px 20px;
  border: 1px solid #EAECF0;
  box-shadow: 0 1px 2px rgba(16,24,40,0.06);
  cursor: pointer;
  transition: border-color 0.2s;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  border-color: #CBD5E1;
}

.stat-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #F5F6F8;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content { flex: 1; }

.stat-value {
  font-size: 30px;
  font-weight: 800;
  color: #0F172A;
  line-height: 1;
  margin-bottom: 4px;
  font-family: 'Inter', sans-serif;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 4px;
}

.stat-trend { font-size: 12px; }
.trend-text    { color: #9CA3AF; }
.trend-online  { color: #10B981; font-weight: 500; }
.trend-offline { color: #EF4444; font-weight: 500; }

/* 迷你趋势图 */
.stat-sparkline {
  position: absolute;
  right: 16px;
  bottom: 12px;
  opacity: 0.18;
}

.sparkline-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 36px;
}

.sparkline-bar {
  width: 5px;
  background: #1E4DA3;
  border-radius: 2px 2px 0 0;
  min-height: 4px;
  transition: height 0.6s ease;
}

.bar-blue   { background: #0EA5E9; }
.bar-green  { background: #10B981; }
.bar-orange { background: #F59E0B; }

/* ====== 图表行等高 ====== */
.chart-row {
  align-items: stretch;
}

.chart-col {
  display: flex;
  flex-direction: column;
}

.equal-height-card {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 活动卡片 body 可滚动 */
.activity-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  padding-bottom: 0;
}

.activity-scroll {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
  scrollbar-width: thin;
  scrollbar-color: #E5E7EB transparent;
}

.activity-scroll::-webkit-scrollbar {
  width: 4px;
}

.activity-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.activity-scroll::-webkit-scrollbar-thumb {
  background: #E5E7EB;
  border-radius: 2px;
}

/* ====== 卡片公共 ====== */
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #0F172A;
  font-family: 'Inter', sans-serif;
}

/* ====== 环形图 ====== */
.donut-chart-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  padding: 16px 0;
}

.donut-svg {
  width: 180px;
  height: 180px;
  flex-shrink: 0;
}

.donut-legend {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  flex-shrink: 0;
}

.legend-label {
  font-size: 13px;
  color: #6B7280;
  width: 30px;
}

.legend-val {
  font-size: 15px;
  font-weight: 700;
  color: #0F172A;
  font-family: 'Inter', sans-serif;
}

/* ====== Timeline ====== */
.timeline-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.timeline-icon {
  flex-shrink: 0;
  color: #6B7280;
}

.timeline-text {
  font-size: 13px;
  color: #374151;
}

/* ====== 快速入口 ====== */
.quick-entries {
  display: flex;
  gap: 16px;
}

.quick-entry {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px;
  border: 1px solid #EAECF0;
  border-radius: 12px;
  cursor: pointer;
  transition: border-color 0.2s;
  background: #FAFAFA;
  text-align: center;
}

.quick-entry:hover {
  border-color: #CBD5E1;
  background: #fff;
}

.qe-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.qe-label {
  font-size: 14px;
  font-weight: 600;
  color: #0F172A;
  margin-bottom: 4px;
  font-family: 'Inter', sans-serif;
}

.qe-desc {
  font-size: 12px;
  color: #6B7280;
}
</style>
