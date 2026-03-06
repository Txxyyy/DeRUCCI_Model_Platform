<template>
  <div class="product-categories">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>产品分类</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="categories" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="code" label="分类编码" />
        <el-table-column prop="productCount" label="产品数量" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入分类编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([
  { id: 1, name: '床垫', code: 'BED', productCount: 5, description: '智能床垫产品' },
  { id: 2, name: '枕头', code: 'PILLOW', productCount: 3, description: '智能枕头产品' },
  { id: 3, name: '沙发', code: 'SOFA', productCount: 2, description: '智能沙发产品' },
  { id: 4, name: '椅子', code: 'CHAIR', productCount: 1, description: '智能椅子产品' }
])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', code: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  setTimeout(() => {
    if (!isEdit.value) {
      form.id = Date.now()
      categories.value.push({ ...form, productCount: 0 })
      ElMessage.success('创建成功')
    } else {
      const idx = categories.value.findIndex(c => c.id === form.id)
      if (idx !== -1) Object.assign(categories.value[idx], form)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    submitLoading.value = false
  }, 500)
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除分类 ${row.name}?`, '提示', { type: 'warning' })
  categories.value = categories.value.filter(c => c.id !== row.id)
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
