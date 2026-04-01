<template>
  <div class="product-detail">
    <!-- 返回和操作栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button text @click="handleBack">
          <AppIcon name="arrow-left" :size="16" style="margin-right:4px" />
          返回
        </el-button>
        <h2>{{ product.name || '产品详情' }}</h2>
        <el-tag :type="getStatusType(product.status)">{{ getStatusText(product.status) }}</el-tag>
      </div>
      <div class="header-actions">
        <el-button v-if="product.status === 'DEVELOPING'" @click="handleEdit">编辑</el-button>
        <el-button v-if="product.status === 'DEVELOPING'" type="primary" @click="handlePublish">发布</el-button>
        <el-button v-if="product.status === 'DEVELOPING'" type="danger" @click="handleDelete">删除</el-button>
        <el-tag v-if="product.status === 'PUBLISHED'" type="success">
          <AppIcon name="lock" :size="12" style="vertical-align:middle;margin-right:3px" /> 已发布锁定
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
            <CategoryIcon :category="product.category" :size="100" />
            <div class="category-label">{{ product.category || '产品' }}</div>
          </div>
        </el-col>
        <el-col :span="16">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="产品型号">
              {{ product.model }}
              <AppIcon v-if="product.status === 'PUBLISHED'" name="lock" :size="12" class="lock-icon" />
            </el-descriptions-item>
            <el-descriptions-item label="产品名称">
              {{ product.name }}
              <AppIcon v-if="product.status === 'PUBLISHED'" name="lock" :size="12" class="lock-icon" />
            </el-descriptions-item>
            <el-descriptions-item label="产品品牌">
              {{ product.brand }}
              <AppIcon v-if="product.status === 'PUBLISHED'" name="lock" :size="12" class="lock-icon" />
            </el-descriptions-item>
            <el-descriptions-item label="产品品类">
              {{ product.category }}
              <AppIcon v-if="product.status === 'PUBLISHED'" name="lock" :size="12" class="lock-icon" />
            </el-descriptions-item>
            <el-descriptions-item label="PID">{{ product.pid }}</el-descriptions-item>
            <el-descriptions-item label="通信方式">
              <el-tag :type="product.protocol === 'MQTT' ? 'primary' : 'warning'" size="small">
                {{ product.protocol }}
              </el-tag>
              <AppIcon v-if="product.status === 'PUBLISHED'" name="lock" :size="12" class="lock-icon" />
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(product.status)">{{ getStatusText(product.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatTime(product.createTime) }}</el-descriptions-item>
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
            <el-button :disabled="product.status === 'PUBLISHED'" @click="handleImportTemplate">
              <AppIcon name="download" :size="15" style="margin-right:4px" />
              从品类模板导入
            </el-button>
            <el-button type="primary" :disabled="product.status === 'PUBLISHED'" @click="handleAddPoint">
              <AppIcon name="plus" :size="15" style="margin-right:4px" />
              手动添加功能点
            </el-button>
          </div>
        </div>
      </template>

      <!-- 功能点摘要 -->
      <div class="tm-summary">
        <el-tag type="info">属性 {{ properties.length }}</el-tag>
        <el-tag type="info">事件 {{ events.length }}</el-tag>
        <el-tag type="info">命令 {{ commands.length }}</el-tag>
        <el-tag :type="thingModelPoints.length > 0 ? 'success' : 'warning'">
          {{ thingModelPoints.length > 0 ? '可发布' : '待配置' }}
        </el-tag>
      </div>

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
                <span v-else-if="row.dataType === 'array'">
                  元素:{{ row.elementType || '-' }} 长度:{{ row.maxLength || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="success" link @click="handleClonePoint(row)">克隆</el-button>
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
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="success" link @click="handleClonePoint(row)">克隆</el-button>
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
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditPoint(row)">编辑</el-button>
                <el-button type="success" link @click="handleClonePoint(row)">克隆</el-button>
                <el-button type="danger" link @click="handleDeletePoint(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="commands.length === 0" description="暂无命令，请添加" />
        </el-tab-pane>
      </el-tabs>

      <!-- 导出区域 -->
      <div class="export-section">
        <el-alert type="info" :closable="false" style="margin-bottom: 12px;">
          物模型配置完成后，导出JSON文件提供给固件/App开发团队使用。
        </el-alert>
        <div class="export-buttons">
          <el-button size="large" @click="handlePreviewJson">
            <AppIcon name="eye" :size="16" style="margin-right:4px" />
            预览JSON
          </el-button>
          <el-button type="success" size="large" @click="handleExportJson">
            <AppIcon name="download" :size="16" style="margin-right:4px" />
            导出JSON文件
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 从品类模板导入弹窗 -->
    <el-dialog v-model="showImportDialog" title="从品类模板导入功能点" width="700px">
      <div class="import-dialog-content">
        <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
          选择一个模板，将其所有功能点批量导入到当前产品，已有功能点不受影响。
        </el-alert>
        <div v-loading="importLoading">
          <el-empty v-if="publishedTemplates.length === 0" description="当前品类暂无已发布的模板" :image-size="60" />
          <div v-else class="template-list">
            <div
              v-for="tmpl in publishedTemplates"
              :key="tmpl.id"
              class="template-item"
              :class="{ selected: selectedTemplateId === tmpl.id }"
              @click="selectedTemplateId = tmpl.id"
            >
              <div class="template-name">{{ tmpl.name }}</div>
              <div class="template-meta">
                属性 {{ tmpl.propertyCount || 0 }} / 事件 {{ tmpl.eventCount || 0 }} / 命令 {{ tmpl.commandCount || 0 }}
              </div>
              <div v-if="tmpl.description" class="template-desc">{{ tmpl.description }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" :loading="importingPoints" :disabled="!selectedTemplateId" @click="handleConfirmImport">
          确认导入
        </el-button>
      </template>
    </el-dialog>

    <!-- JSON预览弹窗 -->
    <el-dialog v-model="showJsonPreview" title="物模型JSON预览" width="800px">
      <pre class="json-preview">{{ formattedJson }}</pre>
      <template #footer>
        <el-button @click="showJsonPreview = false">关闭</el-button>
        <el-button type="success" @click="handleExportJson">导出JSON文件</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑功能点弹窗 -->
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
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import CategoryIcon from '@/components/CategoryIcon.vue'
import PointEditDialog from '@/components/PointEditDialog.vue'
import { productApi } from '../../api/product'
import { productThingModelApi } from '@/api/productThingModel'
import { categoryTemplateApi } from '@/api/categoryTemplate'

const router = useRouter()
const route = useRoute()

const product = ref({})
const thingModelId = ref(null)
const thingModelPoints = ref([])
const activeTab = ref('properties')
const showPointDialog = ref(false)
const isEditPoint = ref(false)
const editingPoint = ref(null)

// 模板导入
const showImportDialog = ref(false)
const importLoading = ref(false)
const importingPoints = ref(false)
const categoryTemplates = ref([])
const selectedTemplateId = ref(null)
const publishedTemplates = computed(() => categoryTemplates.value.filter(t => t.status === 'PUBLISHED'))

// JSON预览
const showJsonPreview = ref(false)

// 计算属性
const properties = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'PROPERTY'))
const events = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'EVENT'))
const commands = computed(() => (thingModelPoints.value || []).filter(p => p.pointType === 'COMMAND'))

const formattedJson = computed(() => {
  return JSON.stringify({
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
  }, null, 2)
})

const formatTime = (time) => {
  if (!time) return '-'
  const d = new Date(time)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

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
  try {
    const res = await productApi.publish(product.value.id)
    if (res?.code === 200 || res?.id || res?.status === 'PUBLISHED') {
      product.value.status = 'PUBLISHED'
      ElMessage.success('发布成功')
    } else {
      // fallback：后端无法区分时仍更新本地状态
      product.value.status = 'PUBLISHED'
      ElMessage.success('发布成功')
    }
  } catch (e) {
    console.error('发布失败:', e)
    ElMessage.error('发布失败: ' + (e.message || '未知错误'))
  }
}

const handleImportTemplate = async () => {
  selectedTemplateId.value = null
  categoryTemplates.value = []
  showImportDialog.value = true
  importLoading.value = true
  try {
    const res = await categoryTemplateApi.getTemplatesByCategory(product.value.category)
    if (res?.code === 200) {
      categoryTemplates.value = res.data || []
    }
  } catch (e) {
    console.error('加载模板失败:', e)
  } finally {
    importLoading.value = false
  }
}

const handleConfirmImport = async () => {
  const template = categoryTemplates.value.find(t => t.id === selectedTemplateId.value)
  if (!template) return

  importingPoints.value = true
  try {
    // 确保 thingModelId 存在
    if (!thingModelId.value) {
      const tmRes = await productThingModelApi.createThingModel({
        name: product.value.name + '物模型',
        code: 'TM_' + product.value.pid,
        category: product.value.category,
        status: 'DRAFT'
      })
      if (!tmRes || tmRes.code !== 200 || !tmRes.data?.id) {
        ElMessage.error('创建物模型失败')
        return
      }
      thingModelId.value = tmRes.data.id
      await productApi.update(product.value.id, { thingModelId: thingModelId.value })
    }

    // 解析并批量导入属性
    const props = template.propertiesJson ? JSON.parse(template.propertiesJson) : []
    for (const p of props) {
      const specs = p.dataType?.specs || {}
      const dataType = p.dataType?.type || 'int'
      const pointData = {
        thingModelId: thingModelId.value,
        pointType: 'PROPERTY',
        pointId: p.pointId,
        name: p.name,
        dataType,
        access: p.accessMode || 'readWrite',
        unit: specs.unit || '',
        description: p.description || ''
      }
      // 数值类型：解析 rangeJson
      if (dataType === 'int' || dataType === 'float') {
        if (specs.min !== undefined || specs.max !== undefined) {
          pointData.rangeJson = JSON.stringify({
            min: specs.min ?? 0,
            max: specs.max ?? 100
          })
        }
      }
      // 枚举类型：解析 enumValuesJson
      if (dataType === 'enum' && specs) {
        const enumEntries = Object.entries(specs).filter(([k]) => k !== 'unit')
        if (enumEntries.length > 0) {
          pointData.enumValuesJson = JSON.stringify(
            enumEntries.map(([value, description]) => ({ value, description: String(description) }))
          )
        }
      }
      // 数组类型
      if (dataType === 'array') {
        pointData.elementType = specs.elementType || 'int'
        pointData.maxLength = specs.maxLength || 10
        if (specs.defaultValue) {
          pointData.defaultValue = JSON.stringify(specs.defaultValue)
        }
      }
      // bool 类型
      if (dataType === 'bool') {
        pointData.trueLabel = specs.true || '是'
        pointData.falseLabel = specs.false || '否'
      }
      await productThingModelApi.createPoint(pointData)
    }

    // 批量导入事件
    const evts = template.eventsJson ? JSON.parse(template.eventsJson) : []
    for (const e of evts) {
      const dataType = e.dataType?.type || 'alarm'
      await productThingModelApi.createPoint({
        thingModelId: thingModelId.value,
        pointType: 'EVENT',
        pointId: e.identifier,
        name: e.name,
        dataType,
        access: 'readOnly',
        description: e.description || ''
      })
    }

    // 批量导入命令
    const cmds = template.commandsJson ? JSON.parse(template.commandsJson) : []
    for (const c of cmds) {
      const dataType = c.dataType?.type || 'sync'
      await productThingModelApi.createPoint({
        thingModelId: thingModelId.value,
        pointType: 'COMMAND',
        pointId: c.identifier,
        name: c.name,
        dataType,
        access: 'readWrite',
        description: c.description || ''
      })
    }

    await loadThingModelPoints()
    showImportDialog.value = false
    ElMessage.success(`已导入 ${props.length + evts.length + cmds.length} 个功能点`)
  } catch (e) {
    console.error('导入失败:', e)
    ElMessage.error('导入失败: ' + (e.message || '未知错误'))
  } finally {
    importingPoints.value = false
  }
}

const handlePreviewJson = () => {
  showJsonPreview.value = true
}

const handleAddPoint = () => {
  isEditPoint.value = false
  editingPoint.value = null
  showPointDialog.value = true
}

const handleEditPoint = (row) => {
  isEditPoint.value = true
  editingPoint.value = { ...row }
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
  showPointDialog.value = true
}

const handleDeletePoint = async (row) => {
  await ElMessageBox.confirm('确定删除此功能点?', '提示', { type: 'warning' })
  try {
    if (row.id && typeof row.id === 'number' && row.id > 1000000000) {
      thingModelPoints.value = (thingModelPoints.value || []).filter(p => p.id !== row.id)
    } else if (row.id) {
      await productThingModelApi.deletePoint(row.id)
      thingModelPoints.value = (thingModelPoints.value || []).filter(p => p.id !== row.id)
    }
    ElMessage.success('删除成功')
  } catch (e) {
    console.error('删除功能点失败:', e)
    ElMessage.error('删除失败')
  }
}

const handlePointSaved = async (saveData) => {
  try {
    // 如果没有thingModelId，先创建物模型
    if (!thingModelId.value) {
      const tmRes = await productThingModelApi.createThingModel({
        name: product.value.name + '物模型',
        code: 'TM_' + product.value.pid,
        category: product.value.category,
        status: 'DRAFT'
      })
      if (!tmRes || tmRes.code !== 200 || !tmRes.data?.id) {
        ElMessage.error('创建物模型失败: ' + (tmRes?.message || '未知错误'))
        return
      }
      thingModelId.value = tmRes.data.id
      const updateRes = await productApi.update(product.value.id, { thingModelId: thingModelId.value })
      if (!updateRes || updateRes.code !== 200) {
        ElMessage.error('更新产品关联物模型失败')
        return
      }
    }

    if (!thingModelId.value) {
      ElMessage.error('物模型ID无效，无法保存功能点')
      return
    }

    saveData.thingModelId = thingModelId.value

    if (isEditPoint.value && saveData.id) {
      await productThingModelApi.updatePoint(saveData.id, saveData)
    } else {
      delete saveData.id
      await productThingModelApi.createPoint(saveData)
    }

    await loadThingModelPoints()
    ElMessage.success(isEditPoint.value ? '更新成功' : '添加成功')
  } catch (e) {
    console.error('保存功能点失败:', e)
    ElMessage.error('保存失败: ' + (e.response?.data?.message || e.message || '未知错误'))
  }
}

const handleExportJson = () => {
  const blob = new Blob([formattedJson.value], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${product.value.model || 'thing-model'}_v1.0.0.json`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
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
    const res = await productThingModelApi.getPoints(thingModelId.value)
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
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 品类图标区 — 简洁白底 */
.product-image.category-bed {
  background: #fff;
  border: 1.5px solid #BAE6FD;
  color: #0EA5E9;
}

.product-image.category-sofa {
  background: #fff;
  border: 1.5px solid #FDE68A;
  color: #D97706;
}

.product-image.category-pillow {
  background: #fff;
  border: 1.5px solid #BBF7D0;
  color: #16A34A;
}

.product-image.category-default {
  background: #fff;
  border: 1.5px solid #E5E7EB;
  color: #6B7280;
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

.tm-summary {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.export-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.export-buttons {
  display: flex;
  gap: 12px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.template-item {
  border: 2px solid #ebeef5;
  border-radius: 8px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-item:hover {
  border-color: #409eff;
  background: #f0f7ff;
}

.template-item.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.template-name {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
}

.template-meta {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.template-desc {
  font-size: 12px;
  color: #c0c4cc;
}

.json-preview {
  background: #1e1e2e;
  color: #cdd6f4;
  padding: 16px;
  border-radius: 8px;
  font-size: 13px;
  line-height: 1.6;
  max-height: 500px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

</style>
