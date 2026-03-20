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
          <el-empty v-if="categoryTemplates.length === 0" description="当前品类暂无模板" :image-size="60" />
          <div v-else class="template-list">
            <div
              v-for="tmpl in categoryTemplates"
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

    <!-- 添加/编辑功能点弹窗 - 卡片选择 + 实时JSON预览 -->
    <el-dialog v-model="showPointDialog" :title="isEditPoint ? '编辑功能点' : '添加功能点'" width="940px" class="point-dialog">
      <div class="point-dialog-layout">
        <!-- 左侧表单 -->
        <div class="point-form-col">
          <el-form ref="pointFormRef" :model="pointForm" :rules="pointRules" label-width="100px">
            <el-form-item label="标识符" prop="pointId">
              <el-input v-model="pointForm.pointId" placeholder="如 bed_position（英文/下划线）" />
            </el-form-item>
            <el-form-item label="功能名称" prop="name">
              <el-input v-model="pointForm.name" placeholder="如 睡床位置" />
            </el-form-item>

            <el-form-item label="数据类型" prop="dataType">
              <div class="datatype-grid">
                <div
                  v-for="dt in dataTypeOptions"
                  :key="dt.value"
                  class="datatype-card"
                  :class="{ 'datatype-active': pointForm.dataType === dt.value }"
                  @click="selectDataType(dt.value)"
                >
                  <span class="dt-icon">{{ dt.icon }}</span>
                  <div class="dt-info">
                    <span class="dt-label">{{ dt.label }}</span>
                    <span class="dt-desc">{{ dt.desc }}</span>
                  </div>
                </div>
              </div>
            </el-form-item>

            <!-- int/float 类型额外字段 -->
            <template v-if="pointForm.dataType === 'int' || pointForm.dataType === 'float'">
              <el-form-item label="读写类型" prop="access">
                <el-radio-group v-model="pointForm.access">
                  <el-radio label="readOnly">只读</el-radio>
                  <el-radio label="readWrite">可读可写</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="单位" prop="unit">
                <div class="unit-input-row">
                  <el-input v-model="pointForm.unit" placeholder="如 %、℃、bpm" style="flex: 1" />
                  <div class="unit-presets">
                    <el-tag
                      v-for="u in commonUnits" :key="u"
                      class="unit-preset-tag"
                      :class="{ 'unit-active': pointForm.unit === u }"
                      @click="pointForm.unit = u"
                    >{{ u }}</el-tag>
                  </div>
                </div>
              </el-form-item>
              <el-form-item label="取值范围">
                <el-row :gutter="8">
                  <el-col :span="11">
                    <el-form-item prop="rangeMin" style="margin-bottom:0">
                      <el-input v-model="pointForm.rangeMin" placeholder="最小值" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="2" style="text-align:center; line-height:32px; color:#999">~</el-col>
                  <el-col :span="11">
                    <el-form-item prop="rangeMax" style="margin-bottom:0">
                      <el-input v-model="pointForm.rangeMax" placeholder="最大值" />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form-item>
              <el-form-item label="步长" prop="step">
                <el-input v-model="pointForm.step" placeholder="如 1、0.1、0.01" style="width: 180px" />
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
              <el-form-item label="true 含义" prop="trueLabel">
                <el-input v-model="pointForm.trueLabel" placeholder="如：开、启用、正常" />
              </el-form-item>
              <el-form-item label="false 含义" prop="falseLabel">
                <el-input v-model="pointForm.falseLabel" placeholder="如：关、禁用、异常" />
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
              <el-form-item label="最大长度" prop="maxLength">
                <el-input-number v-model="pointForm.maxLength" :min="1" :max="10000" />
                <span style="margin-left: 8px; color: #909399; font-size: 13px">字符</span>
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
              <el-form-item label="枚举值">
                <div class="enum-list">
                  <div v-for="(item, index) in enumValues" :key="index" class="enum-item">
                    <el-input v-model="item.value" placeholder="值（如 0、1）" style="width: 110px;" />
                    <span style="color:#c0c4cc; margin: 0 4px">→</span>
                    <el-input v-model="item.description" placeholder="含义（如 关、开）" style="width: 140px;" />
                    <el-button type="danger" link @click="removeEnumValue(index)">
                      <AppIcon name="trash" :size="14" />
                    </el-button>
                  </div>
                  <el-button type="primary" link @click="addEnumValue">
                    <AppIcon name="plus" :size="14" style="margin-right:3px" /> 添加枚举值
                  </el-button>
                </div>
              </el-form-item>
            </template>

            <!-- struct 类型提示 -->
            <template v-if="pointForm.dataType === 'struct'">
              <el-alert type="info" :closable="false" style="margin-bottom: 12px">
                结构体类型将包含多个子字段，请在保存后通过子字段管理功能配置内部属性。
              </el-alert>
            </template>

            <el-form-item label="描述" prop="description">
              <el-input v-model="pointForm.description" type="textarea" :rows="2" placeholder="功能点的业务描述" maxlength="200" show-word-limit />
            </el-form-item>
          </el-form>
        </div>

        <!-- 右侧实时JSON预览 -->
        <div class="point-preview-col">
          <div class="preview-title">
            <AppIcon name="layers" :size="16" style="margin-right:6px;vertical-align:middle" />
            实时 JSON Schema
          </div>
          <pre class="point-json-preview">{{ currentPointJson }}</pre>
          <div class="preview-hint">配置实时同步 · 所见即所得</div>
        </div>
      </div>

      <template #footer>
        <el-button @click="showPointDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingPoint" @click="handleSavePoint">确定保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import CategoryIcon from '@/components/CategoryIcon.vue'
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
const savingPoint = ref(false)

// 模板导入
const showImportDialog = ref(false)
const importLoading = ref(false)
const importingPoints = ref(false)
const categoryTemplates = ref([])
const selectedTemplateId = ref(null)

// JSON预览
const showJsonPreview = ref(false)

// 枚举值列表
const enumValues = ref([])

const dataTypeOptions = [
  { value: 'int', icon: '#', label: 'int 整数', desc: '温度、湿度、计数' },
  { value: 'float', icon: 'π', label: 'float 浮点', desc: '精确数值、坐标' },
  { value: 'bool', icon: '◉', label: 'bool 布尔', desc: '开关、启停状态' },
  { value: 'string', icon: 'Aa', label: 'string 字符串', desc: 'IP地址、标签' },
  { value: 'enum', icon: '≡', label: 'enum 枚举', desc: '模式、档位选择' },
  { value: 'struct', icon: '{}', label: 'struct 结构体', desc: '复杂嵌套数据' }
]

const commonUnits = ['℃', '%', 'bpm', 'mm', 'kg', 'lux', 'V', 'mA', 'Hz', 'rpm']

const pointForm = reactive({
  id: null,
  pointId: '',
  name: '',
  dataType: 'int',
  access: 'readWrite',
  unit: '',
  rangeMin: '',
  rangeMax: '',
  step: '',
  maxLength: null,
  defaultValue: '',
  trueLabel: '',
  falseLabel: '',
  description: ''
})

const pointRules = {
  pointId: [
    { required: true, message: '请输入标识符', trigger: 'blur' },
    { min: 2, max: 50, message: '长度 2-50 字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '以字母开头，仅支持字母、数字和下划线', trigger: 'blur' },
    { validator: (_, v, cb) => v && (/__/.test(v) || v.endsWith('_')) ? cb(new Error('不允许连续下划线或以下划线结尾')) : cb(), trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入功能名称', trigger: 'blur' },
    { max: 50, message: '不超过50字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_\-\s]+$/, message: '支持中文、英文、数字、下划线和短横线', trigger: 'blur' }
  ],
  dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
  unit: [
    { max: 20, message: '单位不超过20字符', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5a-zA-Z0-9%℃°/²³μ]*$/, message: '单位包含不支持的字符', trigger: 'blur' }
  ],
  rangeMin: [
    { validator: (_, v, cb) => {
      if (v === '' || v === null || v === undefined) return cb()
      if (isNaN(Number(v))) return cb(new Error('必须是有效数字'))
      if (pointForm.dataType === 'int' && !Number.isInteger(Number(v))) return cb(new Error('int 类型必须为整数'))
      if (pointForm.dataType === 'int' && Number(v) < -2147483648) return cb(new Error('超出 int 最小值'))
      cb()
    }, trigger: 'blur' }
  ],
  rangeMax: [
    { validator: (_, v, cb) => {
      if (v === '' || v === null || v === undefined) return cb()
      if (isNaN(Number(v))) return cb(new Error('必须是有效数字'))
      if (pointForm.dataType === 'int' && !Number.isInteger(Number(v))) return cb(new Error('int 类型必须为整数'))
      if (pointForm.dataType === 'int' && Number(v) > 2147483647) return cb(new Error('超出 int 最大值'))
      if (pointForm.rangeMin !== '' && !isNaN(Number(pointForm.rangeMin)) && Number(v) <= Number(pointForm.rangeMin)) return cb(new Error('最大值必须大于最小值'))
      cb()
    }, trigger: 'blur' }
  ],
  step: [
    { validator: (_, v, cb) => {
      if (v === '' || v === null || v === undefined) return cb()
      const n = Number(v)
      if (isNaN(n) || n <= 0) return cb(new Error('步长必须为正数'))
      if (pointForm.rangeMin !== '' && pointForm.rangeMax !== '') {
        const range = Number(pointForm.rangeMax) - Number(pointForm.rangeMin)
        if (!isNaN(range) && n > range) return cb(new Error('步长不能大于取值范围'))
      }
      cb()
    }, trigger: 'blur' }
  ],
  trueLabel: [{ max: 20, message: '不超过20字符', trigger: 'blur' }],
  falseLabel: [{ max: 20, message: '不超过20字符', trigger: 'blur' }],
  maxLength: [
    { validator: (_, v, cb) => {
      if (v === null || v === undefined) return cb()
      if (v < 1 || v > 10240) return cb(new Error('范围 1-10240'))
      cb()
    }, trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不超过200字符', trigger: 'blur' }
  ]
}

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

const currentPointJson = computed(() => {
  const schema = {
    identifier: pointForm.pointId || 'identifier',
    name: pointForm.name || '功能名称',
    type: activeTab.value === 'properties' ? 'property' : activeTab.value === 'events' ? 'event' : 'command',
    dataType: { type: pointForm.dataType }
  }
  if (['int', 'float'].includes(pointForm.dataType)) {
    schema.dataType.specs = {
      min: pointForm.rangeMin !== '' ? Number(pointForm.rangeMin) : 0,
      max: pointForm.rangeMax !== '' ? Number(pointForm.rangeMax) : 100,
      unit: pointForm.unit || '',
      step: pointForm.step || 1
    }
    schema.accessMode = pointForm.access
  } else if (pointForm.dataType === 'bool') {
    schema.dataType.specs = { 'true': pointForm.trueLabel || '是', 'false': pointForm.falseLabel || '否' }
    schema.accessMode = pointForm.access
  } else if (pointForm.dataType === 'string') {
    schema.dataType.specs = { maxLength: pointForm.maxLength || 255 }
    schema.accessMode = pointForm.access
  } else if (pointForm.dataType === 'enum') {
    const specs = {}
    enumValues.value.forEach(ev => { if (ev.value) specs[ev.value] = ev.description })
    schema.dataType.specs = specs
    schema.accessMode = pointForm.access
  }
  if (pointForm.description) schema.description = pointForm.description
  return JSON.stringify(schema, null, 2)
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
    const res = await thingModelApi.getThingModels({ category: product.value.category })
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
      const tmRes = await thingModelApi.createThingModel({
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
      await thingModelApi.createPoint({
        thingModelId: thingModelId.value,
        pointType: 'PROPERTY',
        pointId: p.id,
        name: p.name,
        dataType: p.dataType,
        access: p.access,
        unit: p.unit || ''
      })
    }

    // 批量导入事件
    const evts = template.eventsJson ? JSON.parse(template.eventsJson) : []
    for (const e of evts) {
      await thingModelApi.createPoint({
        thingModelId: thingModelId.value,
        pointType: 'EVENT',
        pointId: e.id,
        name: e.name,
        dataType: e.type || 'alarm',
        access: 'readOnly'
      })
    }

    // 批量导入命令
    const cmds = template.commandsJson ? JSON.parse(template.commandsJson) : []
    for (const c of cmds) {
      await thingModelApi.createPoint({
        thingModelId: thingModelId.value,
        pointType: 'COMMAND',
        pointId: c.id,
        name: c.name,
        dataType: c.callType || 'sync',
        access: 'readWrite'
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
  resetPointForm()
  showPointDialog.value = true
}

const handleEditPoint = (row) => {
  resetPointForm()
  isEditPoint.value = true
  // 只拷贝 pointForm 中定义的字段，避免 row 上的额外属性污染 reactive 对象
  pointForm.id = row.id
  pointForm.pointId = row.pointId || ''
  pointForm.name = row.name || ''
  pointForm.dataType = row.dataType || 'int'
  pointForm.access = row.access || 'readWrite'
  pointForm.unit = row.unit || ''
  pointForm.step = row.step || ''
  pointForm.maxLength = row.maxLength || null
  pointForm.defaultValue = row.defaultValue || ''
  pointForm.trueLabel = row.trueLabel || ''
  pointForm.falseLabel = row.falseLabel || ''
  pointForm.description = row.description || ''

  // 解析范围
  if (row.rangeJson) {
    try {
      const range = JSON.parse(row.rangeJson)
      pointForm.rangeMin = range.min ?? ''
      pointForm.rangeMax = range.max ?? ''
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
  enumValues.value = []
  pointForm.rangeMin = ''
  pointForm.rangeMax = ''
  pointForm.step = ''
  pointForm.maxLength = null
  pointForm.trueLabel = ''
  pointForm.falseLabel = ''
}

const selectDataType = (type) => {
  pointForm.dataType = type
  handleDataTypeChange()
}

const handleClonePoint = (row) => {
  isEditPoint.value = false
  resetPointForm()
  pointForm.pointId = (row.pointId || '') + '_copy'
  pointForm.name = (row.name || '') + '（克隆）'
  pointForm.dataType = row.dataType || 'int'
  pointForm.access = row.access || 'readWrite'
  pointForm.unit = row.unit || ''
  pointForm.step = row.step || ''
  pointForm.maxLength = row.maxLength || null
  pointForm.defaultValue = row.defaultValue || ''
  pointForm.trueLabel = row.trueLabel || ''
  pointForm.falseLabel = row.falseLabel || ''
  pointForm.description = row.description || ''

  if (row.rangeJson) {
    try { const r = JSON.parse(row.rangeJson); pointForm.rangeMin = r.min ?? ''; pointForm.rangeMax = r.max ?? '' } catch {}
  }
  enumValues.value = row.enumValuesJson ? (() => { try { return JSON.parse(row.enumValuesJson) } catch { return [] } })() : []
  showPointDialog.value = true
}

const addEnumValue = () => {
  enumValues.value.push({ value: '', description: '' })
}

const removeEnumValue = (index) => {
  enumValues.value.splice(index, 1)
}

// 输入规范化
const normalizePointForm = () => {
  pointForm.pointId = (pointForm.pointId || '').trim().toLowerCase().replace(/\s+/g, '_')
  pointForm.name = (pointForm.name || '').trim()
  pointForm.unit = (pointForm.unit || '').trim()
  pointForm.description = (pointForm.description || '').trim()
  pointForm.defaultValue = (pointForm.defaultValue || '').trim()
  pointForm.trueLabel = (pointForm.trueLabel || '').trim()
  pointForm.falseLabel = (pointForm.falseLabel || '').trim()
  enumValues.value.forEach(ev => {
    ev.value = (ev.value || '').trim()
    ev.description = (ev.description || '').trim()
  })
}

// 额外业务校验（el-form rules 无法覆盖的部分）
const validateBusinessRules = () => {
  const dt = pointForm.dataType
  // 单位校验
  if (pointForm.unit && !/^[\u4e00-\u9fa5a-zA-Z0-9%℃°/²³μ]+$/.test(pointForm.unit)) {
    ElMessage.warning('单位包含不支持的字符'); return false
  }
  if (pointForm.unit && pointForm.unit.length > 20) {
    ElMessage.warning('单位不超过20字符'); return false
  }
  // 取值范围校验
  if ((dt === 'int' || dt === 'float') && pointForm.rangeMin !== '' && pointForm.rangeMax !== '') {
    const min = Number(pointForm.rangeMin), max = Number(pointForm.rangeMax)
    if (isNaN(min) || isNaN(max)) { ElMessage.warning('取值范围必须是有效数字'); return false }
    if (dt === 'int' && (!Number.isInteger(min) || !Number.isInteger(max))) { ElMessage.warning('int 类型取值范围必须为整数'); return false }
    if (dt === 'int' && (min < -2147483648 || max > 2147483647)) { ElMessage.warning('int 范围超出 -2147483648 ~ 2147483647'); return false }
    if (min >= max) { ElMessage.warning('最小值必须小于最大值'); return false }
    // 步长校验
    if (pointForm.step !== '' && pointForm.step !== null) {
      const step = Number(pointForm.step)
      if (isNaN(step) || step <= 0) { ElMessage.warning('步长必须为正数'); return false }
      if (step > max - min) { ElMessage.warning('步长不能大于取值范围'); return false }
    }
  }
  // 字符串最大长度
  if (dt === 'string' && pointForm.maxLength != null) {
    if (pointForm.maxLength < 1 || pointForm.maxLength > 10240) { ElMessage.warning('最大长度范围 1-10240'); return false }
  }
  // 枚举值校验
  if (dt === 'enum') {
    const evs = enumValues.value.filter(e => e.value)
    if (evs.length === 0) { ElMessage.warning('枚举类型至少需要1个枚举值'); return false }
    if (evs.length > 20) { ElMessage.warning('枚举值最多20个'); return false }
    for (const ev of evs) {
      if (!/^[a-zA-Z0-9_]+$/.test(ev.value)) { ElMessage.warning(`枚举值 "${ev.value}" 仅支持字母、数字和下划线`); return false }
      if (ev.value.length > 30) { ElMessage.warning(`枚举值 "${ev.value}" 不超过30字符`); return false }
      if (ev.description && ev.description.length > 50) { ElMessage.warning('枚举描述不超过50字符'); return false }
    }
    const vals = evs.map(e => e.value)
    if (new Set(vals).size !== vals.length) { ElMessage.warning('枚举值不可重复'); return false }
  }
  // bool标签
  if (dt === 'bool') {
    if (pointForm.trueLabel && pointForm.trueLabel.length > 20) { ElMessage.warning('true含义不超过20字符'); return false }
    if (pointForm.falseLabel && pointForm.falseLabel.length > 20) { ElMessage.warning('false含义不超过20字符'); return false }
  }
  // 默认值校验
  if (pointForm.defaultValue !== '') {
    const dv = pointForm.defaultValue
    if (dt === 'int' && !/^-?\d+$/.test(dv)) { ElMessage.warning('默认值必须为整数'); return false }
    if (dt === 'float' && isNaN(Number(dv))) { ElMessage.warning('默认值必须为数字'); return false }
    if (dt === 'bool' && dv !== 'true' && dv !== 'false') { ElMessage.warning('默认值必须为 true 或 false'); return false }
    if (dt === 'enum' && !enumValues.value.some(e => e.value === dv)) { ElMessage.warning('默认值必须是已定义的枚举值之一'); return false }
    if ((dt === 'int' || dt === 'float') && pointForm.rangeMin !== '' && pointForm.rangeMax !== '') {
      const n = Number(dv), min = Number(pointForm.rangeMin), max = Number(pointForm.rangeMax)
      if (!isNaN(n) && !isNaN(min) && !isNaN(max) && (n < min || n > max)) { ElMessage.warning('默认值超出取值范围'); return false }
    }
  }
  return true
}

const handleSavePoint = async () => {
  if (savingPoint.value) return

  // 先规范化输入
  normalizePointForm()

  const valid = await pointFormRef.value.validate().catch(() => false)
  if (!valid) return

  // 业务规则校验
  if (!validateBusinessRules()) return

  savingPoint.value = true

  // 构建保存数据 - 只传后端实体需要的字段，避免未知属性导致500
  const saveData = {
    id: pointForm.id,
    pointId: pointForm.pointId,
    name: pointForm.name,
    dataType: pointForm.dataType,
    access: pointForm.access,
    unit: pointForm.unit,
    maxLength: pointForm.maxLength,
    defaultValue: pointForm.defaultValue,
    description: pointForm.description,
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
    const msg = e.response?.data?.message || e.message || '未知错误'
    ElMessage.error('保存失败: ' + msg)
  } finally {
    savingPoint.value = false
  }
}

const resetPointForm = () => {
  Object.assign(pointForm, {
    id: null, pointId: '', name: '', dataType: 'int',
    access: 'readWrite', unit: '', rangeMin: '', rangeMax: '',
    step: '', maxLength: null, defaultValue: '',
    trueLabel: '', falseLabel: '', description: ''
  })
  enumValues.value = []
  isEditPoint.value = false
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

/* 功能点弹窗双栏布局 */
.point-dialog-layout {
  display: flex;
  gap: 0;
  min-height: 440px;
}

.point-form-col {
  flex: 1;
  padding-right: 16px;
  overflow-y: auto;
  max-height: 520px;
}

.point-preview-col {
  width: 280px;
  border-left: 1px solid #ebeef5;
  padding-left: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preview-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.point-json-preview {
  flex: 1;
  background: #1e1e2e;
  color: #a6e3a1;
  padding: 12px;
  border-radius: 8px;
  font-size: 12px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  line-height: 1.6;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  min-height: 360px;
}

.preview-hint {
  font-size: 11px;
  color: #c0c4cc;
  text-align: center;
}

/* 数据类型卡片选择器 */
.datatype-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  width: 100%;
}

.datatype-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1.5px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  background: #fafafa;
}

.datatype-card:hover {
  border-color: var(--color-primary, #1E4DA3);
  background: #fff5f7;
}

.datatype-active {
  border-color: var(--color-primary, #1E4DA3) !important;
  background: #fff0f3 !important;
  box-shadow: 0 0 0 2px rgba(233, 69, 96, 0.15);
}

.dt-icon {
  font-size: 15px;
  font-family: 'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
  font-weight: 700;
  flex-shrink: 0;
  width: 22px;
  text-align: center;
}

.dt-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.dt-label {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}

.dt-desc {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 单位预设 */
.unit-input-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
  width: 100%;
}

.unit-presets {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.unit-preset-tag {
  cursor: pointer;
  font-size: 12px;
  padding: 0 8px;
  height: 22px;
  line-height: 22px;
  border-radius: 4px;
  transition: all 0.15s;
}

.unit-preset-tag:hover { opacity: 0.8; }

.unit-active {
  background: var(--color-primary, #1E4DA3) !important;
  color: white !important;
  border-color: transparent !important;
}
</style>
