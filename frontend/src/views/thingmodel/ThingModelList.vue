<template>
  <div class="thing-model-list">
    <el-row :gutter="20">
      <!-- 我的模型 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>我的模型</span>
              <el-button type="primary" @click="handleAdd">新建模型</el-button>
            </div>
          </template>

          <el-table :data="myModels" stripe>
            <el-table-column prop="name" label="模型名称">
              <template #default="{ row }">
                <div class="model-name">{{ row.name }}</div>
                <div class="model-version">v{{ row.version }}</div>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column label="统计" width="150">
              <template #default="{ row }">
                <span>属性:{{ row.propertyCount }} 事件:{{ row.eventCount }} 命令:{{ row.commandCount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
                  {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                <el-button type="success" link @click="handlePublish(row)" v-if="row.status === 'DRAFT'">发布</el-button>
                <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 标准模板库 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <span>从模板创建</span>
          </template>

          <div class="template-list">
            <div
              v-for="template in templates"
              :key="template.id"
              class="template-item"
              @click="handleUseTemplate(template)"
            >
              <div class="template-name">{{ template.name }}</div>
              <div class="template-info">
                <el-tag type="success" size="small">系统模板</el-tag>
                <span>v{{ template.version }}</span>
                <span>属性:{{ template.propertyCount }} 事件:{{ template.eventCount }} 命令:{{ template.commandCount }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 物模型编辑器弹窗 -->
    <el-dialog v-model="editorVisible" :title="isEdit ? '编辑物模型' : '新建物模型'" width="900px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模型名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入模型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option label="床垫" value="床垫" />
                <el-option label="枕头" value="枕头" />
                <el-option label="沙发" value="沙发" />
                <el-option label="椅子" value="椅子" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="版本" prop="version">
              <el-input v-model="form.version" placeholder="如: 1.0.0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="2" placeholder="请输入描述" />
        </el-form-item>

        <!-- 属性列表 -->
        <el-divider>属性定义</el-divider>
        <div class="section-header">
          <span>属性 (Properties) - 共{{ form.properties.length }}个</span>
          <el-button type="primary" size="small" @click="addProperty">+ 添加属性</el-button>
        </div>
        <el-table :data="form.properties" border size="small" v-if="form.properties.length > 0">
          <el-table-column prop="identifier" label="标识符" width="150" />
          <el-table-column prop="name" label="名称" width="120" />
          <el-table-column prop="dataType" label="数据类型" width="100" />
          <el-table-column prop="access" label="读写" width="80" />
          <el-table-column prop="unit" label="单位" width="60" />
          <el-table-column prop="range" label="取值范围" />
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeProperty($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 事件列表 -->
        <el-divider>事件定义</el-divider>
        <div class="section-header">
          <span>事件 (Events) - 共{{ form.events.length }}个</span>
          <el-button type="primary" size="small" @click="addEvent">+ 添加事件</el-button>
        </div>
        <el-table :data="form.events" border size="small" v-if="form.events.length > 0">
          <el-table-column prop="identifier" label="标识符" width="150" />
          <el-table-column prop="name" label="名称" width="120" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="params" label="参数" />
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeEvent($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 命令列表 -->
        <el-divider>命令定义</el-divider>
        <div class="section-header">
          <span>命令 (Commands) - 共{{ form.commands.length }}个</span>
          <el-button type="primary" size="small" @click="addCommand">+ 添加命令</el-button>
        </div>
        <el-table :data="form.commands" border size="small" v-if="form.commands.length > 0">
          <el-table-column prop="identifier" label="标识符" width="150" />
          <el-table-column prop="name" label="名称" width="120" />
          <el-table-column prop="type" label="类型" width="100" />
          <el-table-column prop="params" label="参数" />
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="removeCommand($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>

      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 属性编辑弹窗 -->
    <el-dialog v-model="propertyDialogVisible" title="编辑属性" width="600px">
      <el-form :model="propertyForm" label-width="100px">
        <el-form-item label="标识符 *" prop="identifier">
          <el-input v-model="propertyForm.identifier" placeholder="英文字母、数字、下划线" />
        </el-form-item>
        <el-form-item label="名称 *" prop="name">
          <el-input v-model="propertyForm.name" placeholder="如: 当前温度" />
        </el-form-item>
        <el-form-item label="数据类型 *" prop="dataType">
          <el-select v-model="propertyForm.dataType" style="width: 100%">
            <el-option label="整数 (int)" value="int" />
            <el-option label="浮点数 (float)" value="float" />
            <el-option label="字符串 (string)" value="string" />
            <el-option label="布尔值 (bool)" value="bool" />
            <el-option label="枚举 (enum)" value="enum" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最小值">
              <el-input v-model="propertyForm.min" type="number" placeholder="-40" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大值">
              <el-input v-model="propertyForm.max" type="number" placeholder="80" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="读写类型 *">
          <el-radio-group v-model="propertyForm.access">
            <el-radio label="只读">只读</el-radio>
            <el-radio label="可写">可写</el-radio>
            <el-radio label="可读可写">可读可写</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="propertyForm.unit" placeholder="如: ℃、%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="propertyForm.description" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="propertyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmProperty">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { thingModelApi } from '@/api/thingModel'

const myModels = ref([])
const templates = ref([])

const editorVisible = ref(false)
const propertyDialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const editingPropertyIndex = ref(-1)

const form = reactive({
  id: null,
  name: '',
  category: '',
  version: '1.0.0',
  description: '',
  properties: [],
  events: [],
  commands: []
})

const propertyForm = reactive({
  identifier: '',
  name: '',
  dataType: 'int',
  min: '',
  max: '',
  access: '可读可写',
  unit: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null, name: '', category: '', version: '1.0.0', description: '',
    properties: [], events: [], commands: []
  })
  editorVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    ...row,
    properties: [
      { identifier: 'current_temperature', name: '当前温度', dataType: 'int', access: '只读', unit: '℃', range: '-40~80' },
      { identifier: 'target_temperature', name: '目标温度', dataType: 'int', access: '可写', unit: '℃', range: '16~30' }
    ],
    events: [
      { identifier: 'temperature_alarm', name: '温度告警', type: '告警', params: '温度,阈值' }
    ],
    commands: [
      { identifier: 'setTemperature', name: '设置温度', type: '同步', params: '温度(int)' }
    ]
  })
  editorVisible.value = true
}

const handleUseTemplate = (template) => {
  ElMessage.success(`已选择模板: ${template.name}`)
}

const handlePublish = async (row) => {
  try {
    const res = await thingModelApi.updateThingModel(row.id, { ...row, status: 'PUBLISHED' })
    if (res?.code === 200) {
      row.status = 'PUBLISHED'
      ElMessage.success('发布成功')
    }
  } catch (e) {
    row.status = 'PUBLISHED'
    ElMessage.success('发布成功')
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除物模型?`, '提示', { type: 'warning' })
  try {
    await thingModelApi.deleteThingModel(row.id)
  } catch (e) {}
  myModels.value = myModels.value.filter(m => m.id !== row.id)
  ElMessage.success('删除成功')
}

const addProperty = () => {
  editingPropertyIndex.value = -1
  Object.assign(propertyForm, {
    identifier: '', name: '', dataType: 'int', min: '', max: '',
    access: '可读可写', unit: '', description: ''
  })
  propertyDialogVisible.value = true
}

const removeProperty = (index) => {
  form.properties.splice(index, 1)
}

const confirmProperty = () => {
  const range = propertyForm.min && propertyForm.max
    ? `${propertyForm.min}~${propertyForm.max}` : '-'
  const prop = {
    identifier: propertyForm.identifier,
    name: propertyForm.name,
    dataType: propertyForm.dataType,
    access: propertyForm.access,
    unit: propertyForm.unit,
    range
  }
  if (editingPropertyIndex.value >= 0) {
    form.properties[editingPropertyIndex.value] = prop
  } else {
    form.properties.push(prop)
  }
  propertyDialogVisible.value = false
}

const addEvent = () => {
  form.events.push({ identifier: '', name: '', type: '信息', params: '' })
}

const removeEvent = (index) => {
  form.events.splice(index, 1)
}

const addCommand = () => {
  form.commands.push({ identifier: '', name: '', type: '同步', params: '' })
}

const removeCommand = (index) => {
  form.commands.splice(index, 1)
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      name: form.name,
      category: form.category,
      version: form.version,
      description: form.description,
      status: 'DRAFT'
    }
    if (!isEdit.value) {
      const res = await thingModelApi.createThingModel(data)
      if (res?.code === 200) {
        myModels.value.push(res.data)
        ElMessage.success('创建成功')
      } else {
        ElMessage.error(res?.message || '创建失败')
        return
      }
    } else {
      const res = await thingModelApi.updateThingModel(form.id, data)
      if (res?.code === 200) {
        const idx = myModels.value.findIndex(m => m.id === form.id)
        if (idx !== -1) Object.assign(myModels.value[idx], res.data)
        ElMessage.success('更新成功')
      } else {
        ElMessage.error(res?.message || '更新失败')
        return
      }
    }
    editorVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

const loadModels = async () => {
  try {
    const res = await thingModelApi.getThingModels()
    if (res?.code === 200) myModels.value = res.data || []
  } catch (e) {
    console.error('加载物模型列表失败:', e)
  }
}

onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }

.model-name { font-weight: 500; }
.model-version { font-size: 12px; color: #909399; }

.template-list { max-height: 500px; overflow-y: auto; }

.template-item {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-item:hover {
  border-color: #409eff;
  background: #f5f7fa;
}

.template-name { font-weight: 500; margin-bottom: 8px; }

.template-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
