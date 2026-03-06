<template>
  <div class="firmware-list">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filters.productId" placeholder="产品" clearable>
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.status" placeholder="版本状态" clearable>
            <el-option label="测试版" value="TESTING" />
            <el-option label="稳定版" value="STABLE" />
            <el-option label="废弃" value="DEPRECATED" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索版本号..."
            clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleAdd">上传固件</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 固件列表 -->
    <el-card>
      <el-table :data="filteredFirmwares" stripe>
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column prop="productName" label="产品" width="120" />
        <el-table-column prop="fileSize" label="文件大小" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="160" />
        <el-table-column prop="changeLog" label="变更说明" min-width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 'TESTING'">发布</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传固件弹窗 -->
    <el-dialog v-model="dialogVisible" title="上传固件" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="适用产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="固件版本" prop="version">
          <el-input v-model="form.version" placeholder="格式: v主版本.次版本.修订号">
            <template #prepend>v</template>
          </el-input>
        </el-form-item>
        <el-form-item label="固件文件" prop="file">
          <el-upload
            class="firmware-upload"
            drag
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处上传<br />
              <em>或点击选择文件</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">支持 .bin .hex .tar.gz 格式</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="版本状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="TESTING">测试版</el-radio>
            <el-radio label="STABLE">稳定版</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="变更说明">
          <el-input v-model="form.changeLog" type="textarea" rows="4" placeholder="请输入变更说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, UploadFilled } from '@element-plus/icons-vue'

const filters = reactive({
  productId: '',
  status: '',
  keyword: ''
})

const firmwares = ref([
  { id: 1, version: '1.2.3', productId: 1, productName: '智能床垫', fileSize: '2.5MB', status: 'STABLE', uploadTime: '2024-01-15 10:00:00', changeLog: '优化温度传感器精度' },
  { id: 2, version: '1.2.2', productId: 1, productName: '智能床垫', fileSize: '2.4MB', status: 'STABLE', uploadTime: '2024-01-10 10:00:00', changeLog: '修复睡眠监测bug' },
  { id: 3, version: '1.2.1', productId: 1, productName: '智能床垫', fileSize: '2.3MB', status: 'DEPRECATED', uploadTime: '2023-12-20 10:00:00', changeLog: '优化性能' },
  { id: 4, version: '1.2.0', productId: 1, productName: '智能床垫', fileSize: '2.2MB', status: 'DEPRECATED', uploadTime: '2023-12-15 10:00:00', changeLog: '新增功能' },
  { id: 5, version: '1.1.0', productId: 2, productName: '智能枕头', fileSize: '1.8MB', status: 'STABLE', uploadTime: '2023-12-01 10:00:00', changeLog: '初始版本' }
])

const products = ref([
  { id: 1, name: '智能床垫' },
  { id: 2, name: '智能枕头' },
  { id: 3, name: '智能沙发' }
])

const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  productId: null,
  version: '',
  file: null,
  status: 'TESTING',
  changeLog: ''
})

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  version: [{ required: true, message: '请输入固件版本', trigger: 'blur' }],
  status: [{ required: true, message: '请选择版本状态', trigger: 'change' }]
}

const filteredFirmwares = computed(() => {
  let result = firmwares.value

  if (filters.productId) {
    result = result.filter(f => f.productId === filters.productId)
  }
  if (filters.status) {
    result = result.filter(f => f.status === filters.status)
  }
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(f => f.version.toLowerCase().includes(kw))
  }

  return result
})

const getStatusType = (status) => {
  const map = { TESTING: 'warning', STABLE: 'success', DEPRECATED: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { TESTING: '测试版', STABLE: '稳定版', DEPRECATED: '废弃' }
  return map[status] || status
}

const handleAdd = () => {
  Object.assign(form, { id: null, productId: null, version: '', file: null, status: 'TESTING', changeLog: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleFileChange = (file) => {
  form.file = file
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (!form.file && !form.id) {
    ElMessage.warning('请上传固件文件')
    return
  }

  submitLoading.value = true
  setTimeout(() => {
    if (!form.id) {
      firmwares.value.unshift({
        id: Date.now(),
        ...form,
        productName: products.value.find(p => p.id === form.productId)?.name,
        fileSize: '2.5MB',
        uploadTime: new Date().toLocaleString()
      })
      ElMessage.success('上传成功')
    } else {
      const idx = firmwares.value.findIndex(f => f.id === form.id)
      if (idx !== -1) Object.assign(firmwares.value[idx], form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    submitLoading.value = false
  }, 1500)
}

const handlePublish = (row) => {
  row.status = 'STABLE'
  ElMessage.success('发布成功')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除固件 ${row.version}?`, '提示', { type: 'warning' })
  firmwares.value = firmwares.value.filter(f => f.id !== row.id)
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.filter-card { margin-bottom: 20px; }

.firmware-upload {
  width: 100%;
}

.firmware-upload :deep(.el-upload-dragger) {
  width: 100%;
}
</style>
