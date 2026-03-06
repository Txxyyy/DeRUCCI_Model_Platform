<template>
  <div class="ota-list">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="固件管理" name="firmware">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>固件列表</span>
              <el-button type="primary" @click="handleAddFirmware">上传固件</el-button>
            </div>
          </template>
          <el-table :data="firmwareData" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="固件名称" />
            <el-table-column prop="version" label="版本" />
            <el-table-column prop="productId" label="产品ID" />
            <el-table-column prop="fileSize" label="文件大小" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
                  {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="升级任务" name="task">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>升级任务</span>
              <el-button type="primary" @click="handleAddTask">创建任务</el-button>
            </div>
          </template>
          <el-table :data="taskData" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="任务名称" />
            <el-table-column prop="targetVersion" label="目标版本" />
            <el-table-column prop="totalCount" label="设备数" />
            <el-table-column prop="successCount" label="成功" />
            <el-table-column prop="failCount" label="失败" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag>{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('firmware')

const firmwareData = ref([
  { id: 1, name: '智能床垫固件', version: 'v1.2.0', productId: 1, fileSize: '2.5MB', status: 'PUBLISHED' },
  { id: 2, name: '智能床垫固件', version: 'v1.1.5', productId: 1, fileSize: '2.3MB', status: 'DEPRECATED' }
])

const taskData = ref([
  { id: 1, name: '批量升级v1.2.0', targetVersion: 'v1.2.0', totalCount: 100, successCount: 95, failCount: 5, status: 'COMPLETED' }
])

const handleAddFirmware = () => ElMessage.info('上传固件')
const handleAddTask = () => ElMessage.info('创建任务')
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
