<template>
  <div class="product-detail">
    <!-- 返回和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button text @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>{{ product.name || '产品详情' }}</h2>
        <el-tag :type="getStatusType(product.status)">{{ getStatusText(product.status) }}</el-tag>
      </div>
      <div class="header-actions">
        <el-button @click="handleEdit" v-if="product.status === 'DEVELOPING'">编辑</el-button>
        <el-button type="primary" @click="handlePublish" v-if="product.status === 'DEVELOPING'">发布</el-button>
        <el-button type="danger" @click="handleDelete" v-if="product.status === 'DEVELOPING'">删除</el-button>
        <el-tag type="success" v-if="product.status === 'PUBLISHED'">
          <el-icon><Lock /></el-icon> 已发布锁定
        </el-tag>
      </div>
    </div>

    <!-- 基本信息 -->
    <el-card class="info-card">
      <template #header>
        <span>基本信息</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="product-image" :class="getCategoryClass(product.category)">
            <!-- 智能床垫 - 更像真实的床垫 -->
            <svg v-if="product.category === '智能床垫'" class="category-svg" viewBox="0 0 120 80" fill="none">
              <!-- 床垫主体 - 厚实的床垫形状 -->
              <rect x="8" y="28" width="104" height="40" rx="6" fill="currentColor" opacity="0.15" stroke="currentColor" stroke-width="2.5"/>
              <!-- 床垫表面纹理 -->
              <path d="M20 34h80M20 44h80M20 54h80" stroke="currentColor" stroke-width="1.2" opacity="0.6"/>
              <!-- 床脚 -->
              <path d="M15 68v6M105 68v6" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
              <!-- 智能传感器指示灯 -->
              <circle cx="95" cy="36" r="3" fill="currentColor" opacity="0.8"/>
              <circle cx="95" cy="36" r="6" stroke="currentColor" stroke-width="1.5" opacity="0.5"/>
            </svg>
            <!-- 电动床 - 可调节床头的电动床 -->
            <svg v-else-if="product.category === '电动床'" class="category-svg" viewBox="0 0 120 80" fill="none">
              <!-- 床架 -->
              <rect x="6" y="48" width="108" height="20" rx="3" fill="currentColor" opacity="0.1" stroke="currentColor" stroke-width="2"/>
              <!-- 可调节床头 - 倾斜角度 -->
              <path d="M8 48 L8 20 Q8 12 16 12 L30 12 Q38 12 38 20 L38 48" fill="currentColor" opacity="0.2" stroke="currentColor" stroke-width="2"/>
              <!-- 电机/调节按钮 -->
              <circle cx="95" cy="58" r="5" fill="currentColor" opacity="0.3"/>
              <circle cx="95" cy="58" r="2" fill="currentColor" opacity="0.8"/>
              <!-- 床脚 -->
              <path d="M12 68v6M108 68v6" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
            </svg>
            <!-- 智能枕头 - 清晰的枕头形状 -->
            <svg v-else-if="product.category === '智能枕头'" class="category-svg" viewBox="0 0 120 80" fill="none">
              <!-- 枕头主体 - 椭圆形 -->
              <ellipse cx="60" cy="48" rx="52" ry="24" fill="currentColor" opacity="0.15" stroke="currentColor" stroke-width="2.5"/>
              <!-- 枕头表面纹理 -->
              <ellipse cx="60" cy="48" rx="45" ry="18" stroke="currentColor" stroke-width="1" opacity="0.4"/>
              <!-- 智能传感器区域 -->
              <circle cx="60" cy="42" r="8" fill="currentColor" opacity="0.2" stroke="currentColor" stroke-width="1.5"/>
              <circle cx="60" cy="42" r="3" fill="currentColor" opacity="0.8"/>
              <!-- 舒适层线条 -->
              <path d="M20 55 Q60 65 100 55" stroke="currentColor" stroke-width="1.5" opacity="0.5" fill="none"/>
            </svg>
            <!-- 默认图标 -->
            <svg v-else class="category-svg" viewBox="0 0 120 80" fill="none">
              <rect x="20" y="12" width="80" height="56" rx="6" stroke="currentColor" stroke-width="2.5"/>
              <path d="M40 28h40M40 40h40M40 52h25" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <!-- 装饰点 -->
              <circle cx="80" cy="52" r="3" fill="currentColor" opacity="0.5"/>
            </svg>
            <div class="category-label">{{ product.category || '产品' }}</div>
          </div>
        </el-col>
        <el-col :span="16">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="产品型号">
              {{ product.model }}
              <el-icon v-if="product.status === 'PUBLISHED'" class="lock-icon"><Lock /></el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="产品名称">
              {{ product.name }}
              <el-icon v-if="product.status === 'PUBLISHED'" class="lock-icon"><Lock /></el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="产品品牌">
              {{ product.brand }}
              <el-icon v-if="product.status === 'PUBLISHED'" class="lock-icon"><Lock /></el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="产品品类">
              {{ product.category }}
              <el-icon v-if="product.status === 'PUBLISHED'" class="lock-icon"><Lock /></el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="PID">{{ product.pid }}</el-descriptions-item>
            <el-descriptions-item label="通信方式">
              <el-tag :type="product.protocol === 'MQTT' ? 'primary' : 'warning'" size="small">
                {{ product.protocol }}
              </el-tag>
              <el-icon v-if="product.status === 'PUBLISHED'" class="lock-icon"><Lock /></el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(product.status)">{{ getStatusText(product.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ product.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-card>

    <!-- 物模型管理 -->
    <el-card class="thing-model-card">
      <template #header>
        <div class="card-header">
          <span>物模型管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAddPoint">
              <el-icon><Plus /></el-icon>
              添加物模型
            </el-button>
            <el-button @click="handleExportJson">
              <el-icon><Download /></el-icon>
              导出JSON
            </el-button>
            <el-button type="success" @click="handlePublishThingModel" :disabled="thingModelPoints.length === 0">
              <el-icon><Upload /></el-icon>
              发布版本
            </el-button>
          </div>
        </div>
      </template>

      <!-- 功能点Tab -->
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="`属性 (${properties.length})`" name="properties">
          <div class="tab-header">
            <el-button type="primary" size="small" @click="handleAddPoint">
              + 添加属性
            </el-button>
          </div>
          <el-table :data="properties" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="数据类型" width="100" />
            <el-table-column prop="access" label="读写类型" width="100" />
            <el-table-column prop="unit" label="单位" width="80" />
            <el-table-column label="其他" min-width="150">
              <template #default="{ row }">
                <span v-if="row.dataType === 'int' || row.dataType === 'float'">
                  范围: {{ row.rangeJson || '-' }}
                </span>
                <span v-else-if="row.dataType === 'enum'">
                  枚举: {{ getEnumCount(row.enumValuesJson) }}个
                </span>
                <span v-else-if="row.dataType === 'string'">
                  长度: {{ row.maxLength || '无限制' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeletePoint(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="properties.length === 0" description="暂无属性，请添加" />
        </el-tab-pane>

        <el-tab-pane :label="`事件 (${events.length})`" name="events">
          <div class="tab-header">
            <el-button type="primary" size="small" @click="handleAddPoint">
              + 添加事件
            </el-button>
          </div>
          <el-table :data="events" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="事件类型" width="100" />
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeletePoint(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="events.length === 0" description="暂无事件，请添加" />
        </el-tab-pane>

        <el-tab-pane :label="`命令 (${commands.length})`" name="commands">
          <div class="tab-header">
            <el-button type="primary" size="small" @click="handleAddPoint">
              + 添加命令
            </el-button>
          </div>
          <el-table :data="commands" border stripe>
            <el-table-column prop="pointId" label="标识符" min-width="120" />
            <el-table-column prop="name" label="功能名称" min-width="100" />
            <el-table-column prop="dataType" label="调用类型" width="100" />
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeletePoint(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="commands.length === 0" description="暂无命令，请添加" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加/编辑功能点弹窗 - 根据数据类型动态显示字段 -->
    <el-dialog v-model="showPointDialog" :title="isEditPoint ? '编辑功能点' : '添加功能点'" width="650px">
      <el-form :model="pointForm" :rules="pointRules" ref="pointFormRef" label-width="100px">
        <el-form-item label="标识符" prop="pointId">
          <el-input v-model="pointForm.pointId" placeholder="如 bed_position" />
        </el-form-item>
        <el-form-item label="功能名称" prop="name">
          <el-input v-model="pointForm.name" placeholder="如 睡床位置" />
        </el-form-item>

        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="pointForm.dataType" placeholder="选择数据类型" style="width: 100%;" @change="handleDataTypeChange">
            <el-option label="int (整数)" value="int" />
            <el-option label="float (浮点数)" value="float" />
            <el-option label="bool (布尔)" value="bool" />
            <el-option label="string (字符串)" value="string" />
            <el-option label="enum (枚举)" value="enum" />
          </el-select>
        </el-form-item>

        <!-- int/float 类型额外字段 -->
        <template v-if="pointForm.dataType === 'int' || pointForm.dataType === 'float'">
          <el-form-item label="读写类型" prop="access">
            <el-radio-group v-model="pointForm.access">
              <el-radio label="readOnly">只读</el-radio>
              <el-radio label="readWrite">可读可写</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="pointForm.unit" placeholder="如 %、℃、bpm" />
          </el-form-item>
          <el-form-item label="取值范围">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-input v-model="pointForm.rangeMin" placeholder="最小值" />
              </el-col>
              <el-col :span="12">
                <el-input v-model="pointForm.rangeMax" placeholder="最大值" />
              </el-col>
            </el-row>
          </el-form-item>
        </template>

        <!-- bool 类型额外字段 -->
        <template v-if="pointForm.dataType === 'bool'">
          <el-form-item label="读写类型" prop="access">
            <el-radio-group v-model="pointForm.access">
              <el-radio label="readOnly">只读</el-radio>
              <el-radio label="readWrite">可读可写</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="默认值">
            <el-radio-group v-model="pointForm.defaultValue">
              <el-radio label="false">false</el-radio>
              <el-radio label="true">true</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- string 类型额外字段 -->
        <template v-if="pointForm.dataType === 'string'">
          <el-form-item label="读写类型" prop="access">
            <el-radio-group v-model="pointForm.access">
              <el-radio label="readOnly">只读</el-radio>
              <el-radio label="readWrite">可读可写</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="最大长度">
            <el-input-number v-model="pointForm.maxLength" :min="1" :max="10000" placeholder="字符数" />
          </el-form-item>
        </template>

        <!-- enum 类型额外字段 -->
        <template v-if="pointForm.dataType === 'enum'">
          <el-form-item label="读写类型" prop="access">
            <el-radio-group v-model="pointForm.access">
              <el-radio label="readOnly">只读</el-radio>
              <el-radio label="readWrite">可读可写</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="枚举值列表">
            <div class="enum-list">
              <div v-for="(item, index) in enumValues" :key="index" class="enum-item">
                <el-input v-model="item.value" placeholder="值" style="width: 120px;" />
                <el-input v-model="item.description" placeholder="描述" style="width: 150px;" />
                <el-button type="danger" link @click="removeEnumValue(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-button type="primary" link @click="addEnumValue">
                <el-icon><Plus /></el-icon> 添加枚举值
              </el-button>
            </div>
          </el-form-item>
        </template>

        <el-form-item label="描述">
          <el-input v-model="pointForm.description" type="textarea" rows="2" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPointDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSavePoint">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Picture, Plus, Delete, Lock, Download, Upload } from '@element-plus/icons-vue'
import { productApi } from '../../api/product'
import { thingModelApi } from '../../api/thingModel'

const router = useRouter()
const route = useRoute()

const product = ref({})
const thingModelId = ref(null)
const thingModelPoints = ref([])
const activeTab = ref('properties')
const showPointDialog = ref(false)
const isEditPoint = ref(false)
const pointFormRef = ref(null)

// 枚举值列表
const enumValues = ref([])

const pointForm = reactive({
  id: null,
  pointId: '',
  name: '',
  dataType: 'int',
  access: 'readWrite',
  unit: '',
  rangeMin: '',
  rangeMax: '',
  maxLength: null,
  defaultValue: '',
  description: ''
})

const pointRules = {
  pointId: [{ required: true, message: '请输入标识符', trigger: 'blur' }],
  name: [{ required: true, message: '请输入功能名称', trigger: 'blur' }],
  dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }]
}

// 计算属性
const properties = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'PROPERTY'))
const events = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'EVENT'))
const commands = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'COMMAND'))

// 方法
const getStatusType = (status) => {
  const map = { DEVELOPING: 'primary', PUBLISHED: 'success', OFFLINE: 'warning' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { DEVELOPING: '开发中', PUBLISHED: '已发布', OFFLINE: '已下线' }
  return map[status] || status
}

const getCategoryClass = (category) => {
  const map = {
    '智能床垫': 'category-bed',
    '电动床': 'category-sofa',
    '智能枕头': 'category-pillow'
  }
  return map[category] || 'category-default'
}

const getEnumCount = (json) => {
  if (!json) return 0
  try {
    return JSON.parse(json).length
  } catch {
    return 0
  }
}

const handleBack = () => router.back()

const handleEdit = () => {
  ElMessage.info('编辑功能开发中')
}

const handleDelete = async () => {
  await ElMessageBox.confirm('确定删除此产品?', '提示', { type: 'warning' })
  ElMessage.success('删除成功')
  router.push('/products')
}

const handlePublish = async () => {
  if (thingModelPoints.value.length === 0) {
    ElMessage.warning('请先配置物模型后再发布')
    return
  }
  await ElMessageBox.confirm('确定发布产品? 发布后产品信息将锁定不可修改。', '提示', { type: 'warning' })
  product.value.status = 'PUBLISHED'
  ElMessage.success('发布成功')
}

const handleAddPoint = () => {
  isEditPoint.value = false
  resetPointForm()
  showPointDialog.value = true
}

const handleEditPoint = (row) => {
  isEditPoint.value = true
  Object.assign(pointForm, row)

  // 解析范围
  if (row.rangeJson) {
    try {
      const range = JSON.parse(row.rangeJson)
      pointForm.rangeMin = range.min
      pointForm.rangeMax = range.max
    } catch {}
  }

  // 解析枚举值
  if (row.enumValuesJson) {
    try {
      enumValues.value = JSON.parse(row.enumValuesJson)
    } catch {
      enumValues.value = []
    }
  } else {
    enumValues.value = []
  }

  showPointDialog.value = true
}

const handleDeletePoint = async (row) => {
  await ElMessageBox.confirm('确定删除此功能点?', '提示', { type: 'warning' })

  try {
    // 调用API删除功能点
    if (row.id && typeof row.id === 'number' && row.id > 1000000000) {
      // 如果是临时ID（前端生成的），不需要调用API
      thingModelPoints.value = (thingModelPoints.value || []).filter(p => p.id !== row.id)
    } else if (row.id) {
      // 调用后端API删除
      await thingModelApi.deletePoint(row.id)
      thingModelPoints.value = (thingModelPoints.value || []).filter(p => p.id !== row.id)
    }
    ElMessage.success('删除成功')
  } catch (e) {
    console.error('删除功能点失败:', e)
    ElMessage.error('删除失败')
  }
}

const handleDataTypeChange = () => {
  // 切换数据类型时重置相关字段
  enumValues.value = []
  pointForm.rangeMin = ''
  pointForm.rangeMax = ''
  pointForm.maxLength = null
}

const addEnumValue = () => {
  enumValues.value.push({ value: '', description: '' })
}

const removeEnumValue = (index) => {
  enumValues.value.splice(index, 1)
}

const handleSavePoint = async () => {
  const valid = await pointFormRef.value.validate().catch(() => false)
  if (!valid) return

  // 构建保存数据
  const saveData = {
    ...pointForm,
    pointType: activeTab.value === 'properties' ? 'PROPERTY' : activeTab.value === 'events' ? 'EVENT' : 'COMMAND'
  }

  // 处理范围
  if ((pointForm.dataType === 'int' || pointForm.dataType === 'float') && pointForm.rangeMin && pointForm.rangeMax) {
    saveData.rangeJson = JSON.stringify({ min: Number(pointForm.rangeMin), max: Number(pointForm.rangeMax) })
  }

  // 处理枚举值
  if (pointForm.dataType === 'enum' && enumValues.value.length > 0) {
    saveData.enumValuesJson = JSON.stringify(enumValues.value)
  }

  try {
    // 如果没有thingModelId，先创建物模型
    if (!thingModelId.value) {
      const tmRes = await thingModelApi.createThingModel({
        name: product.value.name + '物模型',
        code: 'TM_' + product.value.pid,
        category: product.value.category,
        status: 'DRAFT'
      })
      // 检查物模型创建是否成功
      if (!tmRes || tmRes.code !== 200 || !tmRes.data) {
        ElMessage.error('创建物模型失败: ' + (tmRes?.message || '未知错误'))
        return
      }
      if (!tmRes.data.id) {
        ElMessage.error('物模型创建失败：未返回有效ID')
        return
      }
      thingModelId.value = tmRes.data.id
      // 更新产品的thingModelId
      const updateRes = await productApi.update(product.value.id, { thingModelId: thingModelId.value })
      if (!updateRes || updateRes.code !== 200) {
        ElMessage.error('更新产品关联物模型失败')
        return
      }
    }

    // 再次检查thingModelId
    if (!thingModelId.value) {
      ElMessage.error('物模型ID无效，无法保存功能点')
      return
    }

    saveData.thingModelId = thingModelId.value

    if (isEditPoint.value) {
      // 更新功能点
      await thingModelApi.updatePoint(pointForm.id, saveData)
    } else {
      // 创建功能点
      const res = await thingModelApi.createPoint(saveData)
      if (res && res.code === 200) {
        saveData.id = res.data.id
      }
    }

    // 重新加载功能点列表
    await loadThingModelPoints()

    showPointDialog.value = false
    ElMessage.success(isEditPoint.value ? '更新成功' : '添加成功')
    resetPointForm()
  } catch (e) {
    console.error('保存功能点失败:', e)
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  }
}

const resetPointForm = () => {
  Object.assign(pointForm, {
    id: null, pointId: '', name: '', dataType: 'int',
    access: 'readWrite', unit: '', rangeMin: '', rangeMax: '',
    maxLength: null, defaultValue: '', description: ''
  })
  enumValues.value = []
  isEditPoint.value = false
}

const handleExportJson = () => {
  const json = {
    productId: product.value.pid,
    productName: product.value.name,
    productModel: product.value.model,
    productBrand: product.value.brand,
    category: product.value.category,
    protocol: product.value.protocol,
    version: '1.0.0',
    properties: properties.value,
    events: events.value,
    commands: commands.value
  }

  const blob = new Blob([JSON.stringify(json, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${product.value.model}_v1.0.0.json`
  a.click()
  URL.revokeObjectURL(url)

  ElMessage.success('导出成功')
}

const handlePublishThingModel = async () => {
  await ElMessageBox.confirm('确定发布物模型版本?', '提示', { type: 'info' })
  ElMessage.success('发布成功')
}

// 加载产品数据
onMounted(async () => {
  const id = route.params.id
  console.log('产品详情页, ID:', id)

  try {
    const res = await productApi.getById(id)
    console.log('获取产品数据:', res)
    if (res && res.code === 200 && res.data) {
      product.value = res.data
      thingModelId.value = res.data.thingModelId

      // 如果有物模型ID，加载功能点
      if (thingModelId.value) {
        await loadThingModelPoints()
      }
    } else {
      ElMessage.error('获取产品详情失败')
    }
  } catch (e) {
    console.error('获取产品详情失败:', e)
    ElMessage.error('获取产品详情失败')
  }
})

// 加载物模型功能点
const loadThingModelPoints = async () => {
  if (!thingModelId.value) return

  try {
    const res = await thingModelApi.getPoints(thingModelId.value)
    console.log('获取物模型功能点:', res)
    if (res && res.code === 200 && res.data) {
      thingModelPoints.value = res.data
    }
  } catch (e) {
    console.error('获取物模型功能点失败:', e)
  }
}
</script>

<style scoped>
.product-detail {
  max-width: 1400px;
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

.lock-icon {
  margin-left: 4px;
  color: #909399;
  font-size: 12px;
}

.info-card {
  margin-bottom: 20px;
}

.product-image {
  width: 240px;
  height: 160px;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-image:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 品类渐变背景 */
.product-image.category-bed {
  background: linear-gradient(135deg, #E0F2FE 0%, #7DD3FC 50%, #38BDF8 100%);
  color: #0369A1;
}

.product-image.category-sofa {
  background: linear-gradient(135deg, #FEF3C7 0%, #FCD34D 50%, #FBBF24 100%);
  color: #B45309;
}

.product-image.category-pillow {
  background: linear-gradient(135deg, #DCFCE7 0%, #86EFAC 50%, #4ADE80 100%);
  color: #15803D;
}

.product-image.category-default {
  background: linear-gradient(135deg, #F3F4F6 0%, #D1D5DB 50%, #9CA3AF 100%);
  color: #4B5563;
}

/* SVG图标 */
.category-svg {
  width: 100px;
  height: 66px;
  opacity: 0.9;
}

.category-label {
  position: absolute;
  bottom: 12px;
  font-size: 13px;
  font-weight: 500;
  color: currentColor;
  opacity: 0.8;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.image-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.thing-model-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 8px;
}

.tab-header {
  margin-bottom: 12px;
}

.enum-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.enum-item {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
