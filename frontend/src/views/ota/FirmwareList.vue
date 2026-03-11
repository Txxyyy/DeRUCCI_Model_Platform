<template>
  <div class="firmware-list">
    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filters.productId" placeholder="产品" clearable @change="loadFirmwares">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.status" placeholder="版本状态" clearable @change="loadFirmwares">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="废弃" value="DEPRECATED" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-input v-model="filters.keyword" placeholder="搜索版本号..." clearable @clear="loadFirmwares">
            <template #prefix><AppIcon name="search" :size="15" /></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleAdd">上传固件</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 固件列表 -->
    <el-card>
      <el-table :data="filteredFirmwares" stripe v-loading="loading">
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column label="产品" width="120">
          <template #default="{ row }">
            {{ getProductName(row.productId) }}
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="100">
          <template #default="{ row }">
            {{ row.fileSize ? formatFileSize(row.fileSize) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="变更说明" min-width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 'DRAFT'">发布</el-button>
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
          <el-input v-model="form.version" placeholder="格式: 1.2.3" />
        </el-form-item>
        <el-form-item label="固件文件">
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
        <el-form-item label="变更说明">
          <el-input v-model="form.description" type="textarea" rows="4" placeholder="请输入变更说明" />
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
import AppIcon from '@/components/AppIcon.vue'
import { otaApi } from '@/api/ota'
import { productApi } from '@/api/product'

const filters = reactive({ productId: '', status: '', keyword: '' })
const firmwares = ref([])
const products = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({ productId: null, version: '', file: null, description: '' })

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  version: [{ required: true, message: '请输入固件版本', trigger: 'blur' }]
}

const filteredFirmwares = computed(() => {
  let result = firmwares.value
  if (filters.productId) result = result.filter(f => f.productId === filters.productId)
  if (filters.status) result = result.filter(f => f.status === filters.status)
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(f => f.version.toLowerCase().includes(kw))
  }
  return result
})

const getStatusType = (status) => {
  const map = { DRAFT: 'info', PUBLISHED: 'success', DEPRECATED: 'warning' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { DRAFT: '草稿', PUBLISHED: '已发布', DEPRECATED: '废弃' }
  return map[status] || status
}

const getProductName = (productId) => {
  return products.value.find(p => p.id === productId)?.name || '-'
}

const formatFileSize = (bytes) => {
  if (!bytes) return '-'
  return (bytes / 1024 / 1024).toFixed(1) + 'MB'
}

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const loadFirmwares = async () => {
  loading.value = true
  try {
    const res = await otaApi.getFirmwares()
    if (res?.code === 200) firmwares.value = res.data || []
  } catch (e) {
    console.error('加载固件列表失败:', e)
  } finally {
    loading.value = false
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
  Object.assign(form, { productId: null, version: '', file: null, description: '' })
  dialogVisible.value = true
}

const handleFileChange = (file) => {
  form.file = file
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const data = {
      name: form.version,
      version: form.version,
      productId: form.productId,
      description: form.description,
      status: 'DRAFT'
    }
    const res = await otaApi.createFirmware(data)
    if (res?.code === 200) {
      ElMessage.success('上传成功')
      dialogVisible.value = false
      await loadFirmwares()
    } else {
      ElMessage.error(res?.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('上传失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const handlePublish = async (row) => {
  try {
    const res = await otaApi.publishFirmware(row.id)
    if (res?.code === 200) {
      ElMessage.success('发布成功')
      await loadFirmwares()
    } else {
      ElMessage.error(res?.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败: ' + (e.message || '未知错误'))
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除固件 ${row.version}?`, '提示', { type: 'warning' })
  firmwares.value = firmwares.value.filter(f => f.id !== row.id)
  ElMessage.success('删除成功')
}

onMounted(() => {
  loadFirmwares()
  loadProducts()
})
</script>

<style scoped>
.filter-card { margin-bottom: 20px; }

.firmware-upload { width: 100%; }
.firmware-upload :deep(.el-upload-dragger) { width: 100%; }
</style>
