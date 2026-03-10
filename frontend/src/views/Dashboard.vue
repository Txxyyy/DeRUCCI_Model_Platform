<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="30"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.publishedCount }}</div>
              <div class="stat-label">已发布产品</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="30"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.productCount }}</div>
              <div class="stat-label">产品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="30"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.deviceCount }}</div>
              <div class="stat-label">设备总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" v-loading="loading">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="30"><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.onlineCount }}</div>
              <div class="stat-label">在线设备</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>设备状态分布</span>
          </template>
          <div style="height: 300px; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 20px">
            <el-statistic title="在线率" :value="onlineRate" :precision="1">
              <template #suffix>
                <span style="font-size: 14px; margin-left: 4px">%</span>
              </template>
            </el-statistic>
            <div style="display: flex; gap: 32px; color: #606266; font-size: 14px">
              <span><span style="color:#52c41a">●</span> 在线: {{ stats.onlineCount }}</span>
              <span><span style="color:#999">●</span> 离线: {{ stats.deviceCount - stats.onlineCount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近创建</span>
          </template>
          <el-timeline v-if="recentItems.length > 0">
            <el-timeline-item
              v-for="item in recentItems"
              :key="item.key"
              :timestamp="formatTime(item.createTime)"
              placement="top"
            >
              {{ item.label }}
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Goods, Monitor, Connection } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { deviceApi } from '@/api/device'

const loading = ref(false)
const stats = reactive({ publishedCount: 0, productCount: 0, deviceCount: 0, onlineCount: 0 })
const recentItems = ref([])

const onlineRate = computed(() => {
  if (!stats.deviceCount) return 0
  return parseFloat(((stats.onlineCount / stats.deviceCount) * 100).toFixed(1))
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
      products.slice(0, 5).forEach(p => {
        recentItems.value.push({ key: 'p_' + p.id, label: `产品「${p.name}」已创建`, createTime: p.createTime })
      })
    }

    if (devicesRes.status === 'fulfilled' && devicesRes.value?.code === 200) {
      const devices = devicesRes.value.data || []
      stats.deviceCount = devices.length
      stats.onlineCount = devices.filter(d => d.status === 'ONLINE').length
      devices.slice(0, 5).forEach(d => {
        recentItems.value.push({ key: 'd_' + d.id, label: `设备「${d.name}」已注册`, createTime: d.createTime })
      })
    }

    recentItems.value.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    recentItems.value = recentItems.value.slice(0, 5)
  } catch (e) {
    console.error('加载统计数据失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard { padding: 20px; }

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>
