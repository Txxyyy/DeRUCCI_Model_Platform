<template>
  <div class="category-thing-model">
    <!-- 页面标题 -->
    <div class="page-header">
      <div>
        <h2>品类标准模板</h2>
        <p class="page-desc">维护各品类的标准功能点模板，产品开发时可直接导入使用</p>
      </div>
      <el-button type="primary" @click="handleCreate">
        <AppIcon name="plus" :size="15" style="margin-right:4px" />
        新建模板
      </el-button>
    </div>

    <!-- 品类选择 -->
    <div class="category-selector">
      <div
        v-for="category in categories"
        :key="category.value"
        class="category-item"
        :class="{ active: selectedCategory === category.value }"
        @click="handleSelectCategory(category.value)"
      >
        <CategoryIcon
          :category="category.value"
          :size="64"
          :style="{ color: selectedCategory === category.value ? '#1E4DA3' : '#94A3B8' }"
        />
        <div class="category-name" :class="{ 'active-text': selectedCategory === category.value }">
          {{ category.label }}
        </div>
        <div class="category-badge" :class="{ 'active-badge': selectedCategory === category.value }">
          <span>{{ getTemplateCount(category.value) }} 个模板</span>
        </div>
      </div>
    </div>

    <!-- 模板列表 -->
    <el-card class="template-list-card">
      <template #header>
        <div style="display:flex;align-items:center;gap:8px">
          <CategoryIcon :category="selectedCategory" :size="20" :style="{ color: '#1E4DA3' }" />
          <span>{{ currentCategoryName }} - 模板列表</span>
        </div>
      </template>

      <el-table :data="filteredTemplates" stripe>
        <el-table-column prop="name" label="模板名称" min-width="150" />
        <el-table-column prop="code" label="模板编码" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column label="功能点数" width="200">
          <template #default="{ row }">
            <el-tag type="primary" size="small" style="margin-right:4px">属性 {{ row.propertyCount || 0 }}</el-tag>
            <el-tag type="warning" size="small" style="margin-right:4px">事件 {{ row.eventCount || 0 }}</el-tag>
            <el-tag type="success" size="small">命令 {{ row.commandCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredTemplates.length === 0" description="暂无模板，点击右上角新建模板" />
    </el-card>

    <!-- 创建/编辑模板弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑模板' : '新建模板'" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
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

        <el-form-item label="所属品类">
          <el-tag>{{ getCategoryName(form.category) }}</el-tag>
        </el-form-item>

        <el-form-item label="模板描述">
          <el-input v-model="form.description" type="textarea" rows="2" placeholder="请输入模板描述" />
        </el-form-item>

        <el-divider>功能点配置</el-divider>

        <!-- 功能点Tab -->
        <el-tabs v-model="activeTab">
          <el-tab-pane label="属性" name="properties">
            <div class="point-header">
              <el-button type="primary" size="small" @click="addPoint('properties')">+ 添加属性</el-button>
            </div>
            <el-table :data="form.properties" border size="small">
              <el-table-column label="物模型ID" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.id" placeholder="如 bed_position" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="功能名称" min-width="100">
                <template #default="{ row }">
                  <el-input v-model="row.name" placeholder="如 睡床位置" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="数据类型" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.dataType" placeholder="选择" size="small">
                    <el-option label="int" value="int" />
                    <el-option label="float" value="float" />
                    <el-option label="bool" value="bool" />
                    <el-option label="string" value="string" />
                    <el-option label="enum" value="enum" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="读写类型" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.access" placeholder="选择" size="small">
                    <el-option label="只读" value="readOnly" />
                    <el-option label="可写" value="writeOnly" />
                    <el-option label="可读可写" value="readWrite" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="单位" width="80">
                <template #default="{ row }">
                  <el-input v-model="row.unit" placeholder="如 %" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button type="danger" link @click="removePoint('properties', $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="事件" name="events">
            <div class="point-header">
              <el-button type="primary" size="small" @click="addPoint('events')">+ 添加事件</el-button>
            </div>
            <el-table :data="form.events" border size="small">
              <el-table-column label="物模型ID" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.id" placeholder="如 alarmTempHigh" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="功能名称" min-width="100">
                <template #default="{ row }">
                  <el-input v-model="row.name" placeholder="如 温度过高告警" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="事件类型" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.type" placeholder="选择" size="small">
                    <el-option label="告警" value="alarm" />
                    <el-option label="信息" value="info" />
                    <el-option label="错误" value="error" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="参数" min-width="150">
                <template #default="{ row }">
                  <el-input v-model="row.params" placeholder="如 temperature:int" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button type="danger" link @click="removePoint('events', $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="命令" name="commands">
            <div class="point-header">
              <el-button type="primary" size="small" @click="addPoint('commands')">+ 添加命令</el-button>
            </div>
            <el-table :data="form.commands" border size="small">
              <el-table-column label="物模型ID" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.id" placeholder="如 setBedPosition" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="功能名称" min-width="100">
                <template #default="{ row }">
                  <el-input v-model="row.name" placeholder="如 设置睡床位置" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="调用类型" width="100">
                <template #default="{ row }">
                  <el-select v-model="row.callType" placeholder="选择" size="small">
                    <el-option label="同步" value="sync" />
                    <el-option label="异步" value="async" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="输入参数" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.inputParams" placeholder="如 position:int" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="输出参数" min-width="120">
                <template #default="{ row }">
                  <el-input v-model="row.outputParams" placeholder="如 result:bool" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button type="danger" link @click="removePoint('commands', $index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import CategoryIcon from '@/components/CategoryIcon.vue'
import { thingModelApi } from '@/api/thingModel'

// 品类列表
const categories = [
  { label: '智能床垫', value: '智能床垫' },
  { label: '电动床', value: '电动床' },
  { label: '智能枕头', value: '智能枕头' }
]

const selectedCategory = ref('智能床垫')
const templates = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const activeTab = ref('properties')

const form = reactive({
  id: null,
  name: '',
  code: '',
  category: '智能床垫',
  description: '',
  properties: [],
  events: [],
  commands: []
})

const rules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入模板编码', trigger: 'blur' }]
}

const currentCategoryName = computed(() => getCategoryName(selectedCategory.value))

const filteredTemplates = computed(() => {
  return templates.value.filter(t => t.category === selectedCategory.value)
})

const getCategoryName = (value) => {
  const c = categories.find(c => c.value === value)
  return c ? c.label : value
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return String(dateStr).replace('T', ' ').substring(0, 16)
}

const getTemplateCount = (category) => {
  return templates.value.filter(t => t.category === category).length
}

const handleSelectCategory = (category) => {
  selectedCategory.value = category
  form.category = category
}

const handleCreate = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null, name: '', code: '', category: selectedCategory.value,
    description: '', properties: [], events: [], commands: []
  })
  activeTab.value = 'properties'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  // 解析JSON字段
  try {
    form.properties = row.propertiesJson ? JSON.parse(row.propertiesJson) : []
    form.events = row.eventsJson ? JSON.parse(row.eventsJson) : []
    form.commands = row.commandsJson ? JSON.parse(row.commandsJson) : []
  } catch (e) {
    form.properties = []
    form.events = []
    form.commands = []
  }
  dialogVisible.value = true
}

const handleView = (row) => {
  handleEdit(row)
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除模板 ${row.name}?`, '提示', { type: 'warning' })
  try {
    const res = await thingModelApi.deleteThingModel(row.id)
    if (res?.code === 200) {
      templates.value = templates.value.filter(t => t.id !== row.id)
      ElMessage.success('删除成功')
    } else {
      templates.value = templates.value.filter(t => t.id !== row.id)
      ElMessage.success('删除成功')
    }
  } catch (e) {
    templates.value = templates.value.filter(t => t.id !== row.id)
    ElMessage.success('删除成功')
  }
}

const addPoint = (type) => {
  if (type === 'properties') {
    form.properties.push({ id: '', name: '', dataType: 'int', access: 'readWrite', unit: '' })
  } else if (type === 'events') {
    form.events.push({ id: '', name: '', type: 'alarm', params: '' })
  } else if (type === 'commands') {
    form.commands.push({ id: '', name: '', callType: 'sync', inputParams: '', outputParams: '' })
  }
}

const removePoint = (type, index) => {
  if (type === 'properties') {
    form.properties.splice(index, 1)
  } else if (type === 'events') {
    form.events.splice(index, 1)
  } else if (type === 'commands') {
    form.commands.splice(index, 1)
  }
}

const handleSubmit = async () => {
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
      const res = await thingModelApi.updateThingModel(form.id, data)
      if (res?.code === 200) {
        const idx = templates.value.findIndex(t => t.id === form.id)
        if (idx !== -1) templates.value[idx] = { ...templates.value[idx], ...res.data }
        ElMessage.success('更新成功')
      } else {
        ElMessage.error(res?.message || '更新失败')
        return
      }
    } else {
      const res = await thingModelApi.createThingModel(data)
      if (res?.code === 200) {
        templates.value.push(res.data)
        ElMessage.success('创建成功')
      } else {
        ElMessage.error(res?.message || '创建失败')
        return
      }
    }

    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const loadTemplates = async () => {
  try {
    const res = await thingModelApi.getThingModels()
    if (res?.code === 200) {
      templates.value = res.data || []
    }
  } catch (e) {
    console.error('加载模板失败:', e)
  }
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
  color: var(--color-title);
}

.page-desc {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.category-selector {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.category-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  background: #fff;
  border: 2px solid #E8EDF3;
  border-radius: 14px;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
}

.category-item:hover {
  border-color: #B8CBE4;
  box-shadow: 0 4px 12px rgba(30, 77, 163, 0.08);
}

.category-item.active {
  border-color: #1E4DA3;
  background: rgba(30, 77, 163, 0.04);
  box-shadow: 0 0 0 3px rgba(30, 77, 163, 0.08);
}

.category-name {
  font-size: 15px;
  font-weight: 600;
  color: #1A2B4B;
  margin: 0;
}

.category-name.active-text {
  color: #1E4DA3;
}

.category-badge {
  background: #F1F5F9;
  color: #64748B;
  border-radius: 20px;
  padding: 2px 10px;
  font-size: 12px;
}

.category-badge.active-badge {
  background: rgba(30, 77, 163, 0.08);
  color: #1E4DA3;
}

.template-list-card {
  min-height: 400px;
}

.point-header {
  margin-bottom: 12px;
}
</style>
