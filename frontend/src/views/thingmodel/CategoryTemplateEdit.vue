<template>
  <div class="template-edit-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <el-button text @click="handleBack">
          <AppIcon name="arrow-left" :size="16" style="margin-right:4px" />
          返回
        </el-button>
        <h2>{{ isEdit ? '编辑品类模板' : '新建品类模板' }}</h2>
        <el-tag v-if="form.category">{{ form.category }}</el-tag>
      </div>
      <div class="header-actions">
        <el-button @click="handleBack">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSave">保存</el-button>
      </div>
    </div>

    <!-- 基本信息卡片 -->
    <el-card class="info-card">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="form.name" placeholder="如：智能床垫基础模板" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板编码" prop="code">
              <el-input v-model="form.code" placeholder="如：TM_BASIC_MATTRESS" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属品类">
              <el-tag>{{ form.category || '-' }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板描述" prop="description">
              <el-input v-model="form.description" placeholder="请输入模板描述" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 功能点管理卡片 -->
    <el-card class="points-card">
      <template #header>
        <div class="card-header">
          <span>功能点管理</span>
          <el-button type="primary" size="small" @click="handleAddPoint">
            <AppIcon name="plus" :size="14" style="margin-right:4px" />
            添加功能点
          </el-button>
        </div>
      </template>

      <!-- 功能点摘要 -->
      <div class="tm-summary">
        <el-tag type="info">属性 {{ properties.length }}</el-tag>
        <el-tag type="info">事件 {{ events.length }}</el-tag>
        <el-tag type="info">命令 {{ commands.length }}</el-tag>
      </div>

      <!-- 功能点Tab -->
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="`属性 (${properties.length})`" name="properties">
          <el-table :data="properties" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="数据类型" width="100" />
            <el-table-column prop="access" label="读写类型" width="100" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column label="其他" min-width="150">
              <template #default="{ row }">
                <span v-if="row.dataType === 'int' || row.dataType === 'float'">
                  范围: {{ getRange(row) }}
                </span>
                <span v-else-if="row.dataType === 'enum'">
                  枚举: {{ getEnumCount(row.enumValues) }}个
                </span>
                <span v-else-if="row.dataType === 'string'">
                  长度: {{ row.maxLength || '无限制' }}
                </span>
                <span v-else-if="row.dataType === 'array'">
                  元素:{{ row.elementType || '-' }} 长度:{{ row.maxLength || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template #default="{ row, $index }">
                <div style="display:flex;gap:8px;justify-content:center;flex-wrap:nowrap">
                  <el-button type="primary" link size="small" @click="handleEditPoint(row, $index)">编辑</el-button>
                  <el-button type="success" link size="small" @click="handleClonePoint(row)">克隆</el-button>
                  <el-button type="danger" link size="small" @click="handleRemovePoint('properties', $index)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="properties.length === 0" description="暂无属性，请添加" />
        </el-tab-pane>

        <el-tab-pane :label="`事件 (${events.length})`" name="events">
          <el-table :data="events" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="事件类型" width="100" />
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template #default="{ row, $index }">
                <div style="display:flex;gap:8px;justify-content:center;flex-wrap:nowrap">
                  <el-button type="primary" link size="small" @click="handleEditPoint(row, $index)">编辑</el-button>
                  <el-button type="success" link size="small" @click="handleClonePoint(row)">克隆</el-button>
                  <el-button type="danger" link size="small" @click="handleRemovePoint('events', $index)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="events.length === 0" description="暂无事件，请添加" />
        </el-tab-pane>

        <el-tab-pane :label="`命令 (${commands.length})`" name="commands">
          <el-table :data="commands" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="调用类型" width="100" />
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template #default="{ row, $index }">
                <div style="display:flex;gap:8px;justify-content:center;flex-wrap:nowrap">
                  <el-button type="primary" link size="small" @click="handleEditPoint(row, $index)">编辑</el-button>
                  <el-button type="success" link size="small" @click="handleClonePoint(row)">克隆</el-button>
                  <el-button type="danger" link size="small" @click="handleRemovePoint('commands', $index)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="commands.length === 0" description="暂无命令，请添加" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 功能点编辑弹窗 -->
    <PointEditDialog
      v-model="showPointDialog"
      :point="editingPoint"
      :is-edit="isEditPoint"
      :point-type="activeTab"
      @save="handlePointSaved"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import PointEditDialog from '@/components/PointEditDialog.vue'
import { categoryTemplateApi } from '@/api/categoryTemplate'

const router = useRouter()
const route = useRoute()

const isEdit = computed(() => !!route.params.id)
const activeTab = ref('properties')
const showPointDialog = ref(false)
const isEditPoint = ref(false)
const editingPoint = ref(null)
const editingIndex = ref(-1)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  code: '',
  category: '',
  description: '',
  properties: [],
  events: [],
  commands: []
})

const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入模板编码', trigger: 'blur' }]
}

const properties = computed(() => form.properties || [])
const events = computed(() => form.events || [])
const commands = computed(() => form.commands || [])

const getEnumCount = (enumValues) => {
  if (!enumValues) return 0
  if (Array.isArray(enumValues)) return enumValues.length
  try { return JSON.parse(enumValues).length } catch { return 0 }
}

const getRange = (row) => {
  if (row.rangeMin !== undefined && row.rangeMin !== null && row.rangeMin !== '') {
    return `${row.rangeMin}~${row.rangeMax ?? '-'}`
  }
  if (row.rangeJson) {
    try {
      const range = JSON.parse(row.rangeJson)
      return `${range.min ?? '-'}~${range.max ?? '-'}`
    } catch { /* ignore */ }
  }
  return '-'
}

const handleBack = () => {
  router.push('/products/category-templates')
}

const handleAddPoint = () => {
  isEditPoint.value = false
  editingPoint.value = null
  editingIndex.value = -1
  showPointDialog.value = true
}

const handleEditPoint = (row, index) => {
  isEditPoint.value = true
  editingPoint.value = { ...row }
  editingIndex.value = index
  showPointDialog.value = true
}

const handleClonePoint = (row) => {
  isEditPoint.value = false
  editingPoint.value = {
    ...row,
    id: null,
    pointId: (row.pointId || '') + '_copy',
    name: (row.name || '') + '（克隆）'
  }
  editingIndex.value = -1
  showPointDialog.value = true
}

const handleRemovePoint = (type, index) => {
  if (type === 'properties') {
    form.properties.splice(index, 1)
  } else if (type === 'events') {
    form.events.splice(index, 1)
  } else if (type === 'commands') {
    form.commands.splice(index, 1)
  }
}

const handlePointSaved = (saveData) => {
  if (isEditPoint.value) {
    // 编辑模式：替换原数据
    const arr = activeTab.value === 'properties' ? form.properties
      : activeTab.value === 'events' ? form.events : form.commands
    if (editingIndex.value >= 0 && editingIndex.value < arr.length) {
      arr[editingIndex.value] = { ...saveData }
    }
  } else {
    // 新增模式：push 到对应数组
    if (activeTab.value === 'properties') {
      form.properties.push({ ...saveData })
    } else if (activeTab.value === 'events') {
      form.events.push({ ...saveData })
    } else if (activeTab.value === 'commands') {
      form.commands.push({ ...saveData })
    }
  }
  ElMessage.success(isEditPoint.value ? '更新成功' : '添加成功')
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      name: form.name,
      code: form.code,
      category: form.category,
      description: form.description,
      propertiesJson: JSON.stringify(form.properties),
      eventsJson: JSON.stringify(form.events),
      commandsJson: JSON.stringify(form.commands),
      propertyCount: form.properties.length,
      eventCount: form.events.length,
      commandCount: form.commands.length
    }

    if (isEdit.value) {
      const res = await categoryTemplateApi.updateTemplate(form.id, data)
      if (res?.code === 200) {
        ElMessage.success('更新成功')
        router.push('/products/category-templates')
      } else {
        ElMessage.error(res?.message || '更新失败')
      }
    } else {
      const res = await categoryTemplateApi.createTemplate(data)
      if (res?.code === 200) {
        ElMessage.success('创建成功')
        router.push('/products/category-templates')
      } else {
        ElMessage.error(res?.message || '创建失败')
      }
    }
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  // 从 query 参数获取品类
  const category = route.query.category
  if (category) {
    form.category = category
  }

  // 编辑模式：加载数据
  if (isEdit.value) {
    const id = route.params.id
    categoryTemplateApi.getTemplateById(id).then(res => {
      if (res?.code === 200 && res.data) {
        form.id = res.data.id
        form.name = res.data.name
        form.code = res.data.code
        form.category = res.data.category
        form.description = res.data.description || ''
        try {
          form.properties = res.data.propertiesJson ? JSON.parse(res.data.propertiesJson) : []
          form.events = res.data.eventsJson ? JSON.parse(res.data.eventsJson) : []
          form.commands = res.data.commandsJson ? JSON.parse(res.data.commandsJson) : []
        } catch {
          form.properties = []
          form.events = []
          form.commands = []
        }
      }
    }).catch(e => {
      ElMessage.error('加载模板失败: ' + e.message)
    })
  }
})
</script>

<style scoped>
.template-edit-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.info-card {
  margin-bottom: 20px;
}

.points-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tm-summary {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
</style>
