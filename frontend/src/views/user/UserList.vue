<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">新增用户</el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'danger'">
              {{ row.status === 'ENABLED' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleToggleStatus(row)">
              {{ row.status === 'ENABLED' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="开发者" value="DEVELOPER" />
            <el-option label="FAE工程师" value="FAE" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role === 'DEVELOPER'" label="产品分配">
          <el-select v-model="form.assignedProductIds" multiple placeholder="选择分配的产品" style="width: 100%">
            <el-option v-for="p in productList" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.role !== 'ADMIN'" label="操作权限">
          <div style="display: flex; gap: 12px; flex-wrap: wrap;">
            <el-checkbox v-model="form.actionPublish">发布产品</el-checkbox>
            <el-checkbox v-model="form.actionDelete">删除设备</el-checkbox>
            <el-checkbox v-model="form.actionOffline">设备下线</el-checkbox>
            <el-checkbox v-model="form.actionPush">OTA推送</el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { productApi } from '@/api/product'
import authApi from '@/api/auth'

const tableData = ref([])
const productList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const editingId = ref(null)

const form = reactive({
  username: '', password: '', nickname: '', email: '', role: 'DEVELOPER',
  assignedProductIds: [],
  actionPublish: false, actionDelete: false, actionOffline: false, actionPush: false
})

const roleLabel = (role) => ({ ADMIN: '管理员', DEVELOPER: '开发者', FAE: 'FAE工程师' }[role] || role)
const roleTagType = (role) => ({ ADMIN: 'danger', DEVELOPER: '', FAE: 'warning' }[role] || 'info')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const fetchData = async () => {
  try {
    const res = await userApi.getList()
    tableData.value = res.data || []
  } catch { tableData.value = [] }
}

const handleAdd = () => {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { username: '', password: '', nickname: '', email: '', role: 'DEVELOPER',
    assignedProductIds: [], actionPublish: false, actionDelete: false, actionOffline: false, actionPush: false })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { username: row.username, password: '', nickname: row.nickname || '', email: row.email || '', role: row.role || 'DEVELOPER',
    assignedProductIds: [], actionPublish: false, actionDelete: false, actionOffline: false, actionPush: false })
  // 加载用户权限配置
  try {
    const res = await authApi.getUserPermissions(row.id)
    const data = res.data
    if (data.role) form.role = data.role
    form.assignedProductIds = data.assignedProductIds || []
    const perms = data.permissions || []
    form.actionPublish = perms.some(p => p.action === 'PUBLISH')
    form.actionDelete = perms.some(p => p.action === 'DELETE')
    form.actionOffline = perms.some(p => p.action === 'OFFLINE')
    form.actionPush = perms.some(p => p.action === 'PUSH')
  } catch { /* ignore */ }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await userApi.update(editingId.value, { nickname: form.nickname, email: form.email, role: form.role })
      // 保存权限配置
      const permissions = []
      if (form.actionPublish) permissions.push({ module: 'PRODUCT', access: 'RW', action: 'PUBLISH' })
      if (form.actionDelete) permissions.push({ module: 'DEVICE', access: 'RW', action: 'DELETE' })
      if (form.actionOffline) permissions.push({ module: 'DEVICE', access: 'R', action: 'OFFLINE' })
      if (form.actionPush) permissions.push({ module: 'OTA', access: 'RW', action: 'PUSH' })
      await authApi.updateUserPermissions(editingId.value, {
        role: form.role,
        permissions,
        assignedProductIds: form.role === 'DEVELOPER' ? form.assignedProductIds : []
      })
    } else {
      const userData = { username: form.username, password: form.password, nickname: form.nickname || null, email: form.email || null, role: form.role }
      const res = await userApi.create(userData)
      // 创建后保存权限配置
      const newUserId = res.data?.id
      if (newUserId) {
        const permissions = []
        if (form.actionPublish) permissions.push({ module: 'PRODUCT', access: 'RW', action: 'PUBLISH' })
        if (form.actionDelete) permissions.push({ module: 'DEVICE', access: 'RW', action: 'DELETE' })
        if (form.actionOffline) permissions.push({ module: 'DEVICE', access: 'R', action: 'OFFLINE' })
        if (form.actionPush) permissions.push({ module: 'OTA', access: 'RW', action: 'PUSH' })
        try {
          await authApi.updateUserPermissions(newUserId, {
            role: form.role,
            permissions,
            assignedProductIds: form.role === 'DEVELOPER' ? form.assignedProductIds : []
          })
        } catch { /* 权限保存失败不阻塞用户创建 */ }
      }
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const handleToggleStatus = async (row) => {
  try {
    if (row.status === 'ENABLED') {
      await userApi.disable(row.id)
    } else {
      await userApi.enable(row.id)
    }
    ElMessage.success('操作成功')
    fetchData()
  } catch (e) { ElMessage.error('操作失败') }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除用户 ${row.username}?`, '提示', { type: 'warning' })
  try {
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch { ElMessage.error('删除失败') }
}

const fetchProducts = async () => {
  try {
    const res = await productApi.getList()
    productList.value = res.data || []
  } catch { productList.value = [] }
}

onMounted(() => { fetchData(); fetchProducts() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
