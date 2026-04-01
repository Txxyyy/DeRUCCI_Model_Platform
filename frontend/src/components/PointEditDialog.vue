<template>
  <el-dialog
    :model-value="modelValue"
    :title="isEdit ? '编辑功能点' : '添加功能点'"
    width="940px"
    class="point-dialog"
    @update:model-value="$emit('update:modelValue', $event)"
  >
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

          <!-- int/float -->
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

          <!-- bool -->
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

          <!-- string -->
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

          <!-- enum -->
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

          <!-- array -->
          <template v-if="pointForm.dataType === 'array'">
            <el-form-item label="读写类型" prop="access">
              <el-radio-group v-model="pointForm.access">
                <el-radio label="readOnly">只读</el-radio>
                <el-radio label="readWrite">可读可写</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="元素类型 *">
              <el-select v-model="pointForm.elementType" style="width: 200px">
                <el-option label="整数 (int)" value="int" />
                <el-option label="浮点数 (float)" value="float" />
                <el-option label="字符串 (string)" value="string" />
                <el-option label="布尔值 (bool)" value="bool" />
              </el-select>
            </el-form-item>
            <el-form-item label="最大长度 *">
              <el-input-number v-model="pointForm.maxLength" :min="1" :max="10000" style="width: 200px" />
            </el-form-item>
            <el-form-item label="默认值">
              <div class="array-editor">
                <div class="array-items">
                  <div v-for="(item, idx) in arrayDefaultItems" :key="idx" class="array-item">
                    <el-input
                      v-if="pointForm.elementType === 'string'"
                      v-model="arrayDefaultItems[idx]"
                      placeholder="请输入"
                      size="small"
                      style="width: 120px"
                    />
                    <el-input-number
                      v-else-if="pointForm.elementType === 'int' || pointForm.elementType === 'float'"
                      v-model="arrayDefaultItems[idx]"
                      size="small"
                      style="width: 120px"
                    />
                    <el-switch
                      v-else-if="pointForm.elementType === 'bool'"
                      v-model="arrayDefaultItems[idx]"
                      size="small"
                    />
                    <el-button type="danger" link size="small" :disabled="arrayDefaultItems.length <= 0" @click="removeArrayItem(idx)">
                      <AppIcon name="minus" :size="12" />
                    </el-button>
                  </div>
                </div>
                <el-button
                  type="primary"
                  link
                  size="small"
                  :disabled="!pointForm.maxLength || arrayDefaultItems.length >= pointForm.maxLength"
                  @click="addArrayItem"
                >
                  <AppIcon name="plus" :size="12" style="margin-right:3px" />添加元素
                </el-button>
                <span v-if="pointForm.maxLength" class="array-hint">最多 {{ pointForm.maxLength }} 个</span>
              </div>
            </el-form-item>
          </template>

          <!-- struct -->
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
        <pre class="point-json-preview">{{ jsonPreview }}</pre>
        <div class="preview-hint">配置实时同步 · 所见即所得</div>
      </div>
    </div>

    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">确定保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, toRef } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { usePointEditor, dataTypeOptions, commonUnits } from '@/composables/usePointEditor'

const props = defineProps({
  modelValue: Boolean,
  point: Object,
  isEdit: Boolean,
  pointType: { type: String, default: 'properties' }
})

const emit = defineEmits(['update:modelValue', 'save'])

const {
  pointForm, enumValues, arrayDefaultItems, pointRules,
  selectDataType, resetPointForm, normalizePointForm,
  validateBusinessRules, addEnumValue, removeEnumValue,
  addArrayItem, removeArrayItem, makePointJson, fillFormFromPoint, buildPointData
} = usePointEditor()

const pointFormRef = ref(null)
const saving = ref(false)
const pointTypeRef = toRef(props, 'pointType')
const jsonPreview = makePointJson(pointTypeRef)

// 当弹窗打开时，填充或重置表单
watch(() => props.modelValue, (visible) => {
  if (visible) {
    if (props.isEdit && props.point) {
      fillFormFromPoint(props.point)
    } else if (!props.isEdit && props.point) {
      fillFormFromPoint(props.point)
      pointForm.id = null
    } else {
      resetPointForm()
    }
  }
})

const handleSave = async () => {
  if (saving.value) return
  normalizePointForm()

  const valid = await pointFormRef.value.validate().catch(() => false)
  if (!valid) return
  if (!validateBusinessRules()) return

  saving.value = true
  try {
    const data = buildPointData(props.pointType)
    emit('save', data)
    emit('update:modelValue', false)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
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

.array-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.array-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.array-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.array-hint {
  font-size: 12px;
  color: #909399;
}
</style>
