<template>
  <div class="thing-model-templates">
    <el-card>
      <template #header>
        <span>标准模板库</span>
      </template>

      <el-row :gutter="20">
        <el-col
          v-for="template in templates"
          :key="template.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="template-card" shadow="hover">
            <div class="template-header">
              <el-tag type="success" size="small">系统模板</el-tag>
            </div>
            <h3 class="template-name">{{ template.name }}</h3>
            <p class="template-version">版本: {{ template.version }}</p>
            <div class="template-stats">
              <span>属性: {{ template.propertyCount }}</span>
              <span>事件: {{ template.eventCount }}</span>
              <span>命令: {{ template.commandCount }}</span>
            </div>
            <div class="template-actions">
              <el-button type="primary" size="small" @click="handleUseTemplate(template)">使用模板</el-button>
              <el-button size="small" @click="handlePreview(template)">预览</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="模板预览" width="700px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="属性" name="properties">
          <el-table :data="previewData.properties" size="small">
            <el-table-column prop="identifier" label="标识符" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="dataType" label="数据类型" width="100" />
            <el-table-column prop="access" label="读写类型" width="80" />
            <el-table-column prop="unit" label="单位" width="60" />
            <el-table-column prop="range" label="取值范围" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="事件" name="events">
          <el-table :data="previewData.events" size="small">
            <el-table-column prop="identifier" label="标识符" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="params" label="参数" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="命令" name="commands">
          <el-table :data="previewData.commands" size="small">
            <el-table-column prop="identifier" label="标识符" />
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="类型" width="80" />
            <el-table-column prop="params" label="参数" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const templates = ref([
  {
    id: 1,
    name: '温度传感器模板',
    version: 'v1.0.0',
    propertyCount: 2,
    eventCount: 2,
    commandCount: 1
  },
  {
    id: 2,
    name: '开关设备模板',
    version: 'v1.0.0',
    propertyCount: 2,
    eventCount: 1,
    commandCount: 3
  },
  {
    id: 3,
    name: '调光灯模板',
    version: 'v1.0.0',
    propertyCount: 3,
    eventCount: 1,
    commandCount: 4
  },
  {
    id: 4,
    name: '智能床垫模板',
    version: 'v1.0.0',
    propertyCount: 12,
    eventCount: 5,
    commandCount: 8
  },
  {
    id: 5,
    name: '智能枕头模板',
    version: 'v1.0.0',
    propertyCount: 8,
    eventCount: 3,
    commandCount: 5
  },
  {
    id: 6,
    name: '环境监测模板',
    version: 'v1.0.0',
    propertyCount: 5,
    eventCount: 3,
    commandCount: 2
  }
])

const previewVisible = ref(false)
const activeTab = ref('properties')
const previewData = reactive({
  properties: [],
  events: [],
  commands: []
})

const handleUseTemplate = (template) => {
  ElMessage.success(`已选择模板: ${template.name}`)
  router.push('/thing-models/my')
}

const handlePreview = (template) => {
  // 模拟预览数据
  if (template.name.includes('温度')) {
    previewData.properties = [
      { identifier: 'current_temp', name: '当前温度', dataType: 'int', access: '只读', unit: '℃', range: '-40~80' },
      { identifier: 'target_temp', name: '目标温度', dataType: 'int', access: '可写', unit: '℃', range: '16~30' }
    ]
    previewData.events = [
      { identifier: 'temp_alarm', name: '温度告警', type: '告警', params: '温度,阈值' },
      { identifier: 'temp_normal', name: '温度恢复', type: '信息', params: '温度' }
    ]
    previewData.commands = [
      { identifier: 'setTemp', name: '设置温度', type: '同步', params: '温度(int)' }
    ]
  } else if (template.name.includes('床垫')) {
    previewData.properties = [
      { identifier: 'current_temperature', name: '当前温度', dataType: 'int', access: '只读', unit: '℃', range: '-40~80' },
      { identifier: 'target_temperature', name: '目标温度', dataType: 'int', access: '可写', unit: '℃', range: '16~30' },
      { identifier: 'hardness_level', name: '硬度等级', dataType: 'int', access: '可写', unit: '-', range: '1~10' },
      { identifier: 'vibration_level', name: '振动等级', dataType: 'int', access: '只读', unit: '-', range: '0~10' },
      { identifier: 'sleep_mode', name: '睡眠模式', dataType: 'enum', access: '可写', unit: '-', range: '预设值' }
    ]
    previewData.events = [
      { identifier: 'temperature_alarm', name: '温度告警', type: '告警', params: '温度,阈值' },
      { identifier: 'hardness_alarm', name: '硬度告警', type: '告警', params: '硬度' },
      { identifier: 'sleep_completed', name: '睡眠完成', type: '信息', params: '睡眠时长' }
    ]
    previewData.commands = [
      { identifier: 'setTemperature', name: '设置温度', type: '同步', params: '温度(int)' },
      { identifier: 'setHardness', name: '设置硬度', type: '同步', params: '硬度(int)' },
      { identifier: 'startMassage', name: '启动按摩', type: '异步', params: '模式(int)' }
    ]
  } else {
    previewData.properties = [
      { identifier: 'switch_status', name: '开关状态', dataType: 'bool', access: '可写', unit: '-', range: '0/1' },
      { identifier: 'value', name: '值', dataType: 'int', access: '可写', unit: '-', range: '0~100' }
    ]
    previewData.events = [
      { identifier: 'status_change', name: '状态变更', type: '信息', params: '状态' }
    ]
    previewData.commands = [
      { identifier: 'turnOn', name: '开启', type: '同步', params: '-' },
      { identifier: 'turnOff', name: '关闭', type: '同步', params: '-' },
      { identifier: 'setValue', name: '设置值', type: '同步', params: '值(int)' }
    ]
  }
  previewVisible.value = true
}
</script>

<style scoped>
.template-card {
  margin-bottom: 20px;
}

.template-header {
  margin-bottom: 12px;
}

.template-name {
  margin: 0 0 8px;
  font-size: 16px;
}

.template-version {
  margin: 0 0 12px;
  color: #909399;
  font-size: 14px;
}

.template-stats {
  display: flex;
  gap: 16px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 16px;
}

.template-actions {
  display: flex;
  gap: 8px;
}
</style>
