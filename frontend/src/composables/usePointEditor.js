import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

export const dataTypeOptions = [
  { value: 'int', icon: '#', label: 'int 整数', desc: '温度、湿度、计数' },
  { value: 'float', icon: 'π', label: 'float 浮点', desc: '精确数值、坐标' },
  { value: 'bool', icon: '◉', label: 'bool 布尔', desc: '开关、启停状态' },
  { value: 'string', icon: 'Aa', label: 'string 字符串', desc: 'IP地址、标签' },
  { value: 'enum', icon: '≡', label: 'enum 枚举', desc: '模式、档位选择' },
  { value: 'struct', icon: '{}', label: 'struct 结构体', desc: '复杂嵌套数据' },
  { value: 'array', icon: '[]', label: 'array 数组', desc: '多个同类型值' }
]

export const commonUnits = ['℃', '%', 'bpm', 'mm', 'kg', 'lux', 'V', 'mA', 'Hz', 'rpm']

export function usePointEditor() {
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
    description: '',
    elementType: 'int'
  })

  const enumValues = ref([])
  const arrayDefaultItems = ref([])

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
    description: [{ max: 200, message: '描述不超过200字符', trigger: 'blur' }]
  }

  const selectDataType = (type) => {
    pointForm.dataType = type
    handleDataTypeChange()
  }

  const handleDataTypeChange = () => {
    enumValues.value = []
    arrayDefaultItems.value = []
    pointForm.elementType = 'int'
    pointForm.rangeMin = ''
    pointForm.rangeMax = ''
    pointForm.step = ''
    pointForm.maxLength = null
    pointForm.trueLabel = ''
    pointForm.falseLabel = ''
  }

  const resetPointForm = () => {
    Object.assign(pointForm, {
      id: null, pointId: '', name: '', dataType: 'int',
      access: 'readWrite', unit: '', rangeMin: '', rangeMax: '',
      step: '', maxLength: null, defaultValue: '',
      trueLabel: '', falseLabel: '', description: '', elementType: 'int'
    })
    enumValues.value = []
    arrayDefaultItems.value = []
  }

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

  const validateBusinessRules = () => {
    const dt = pointForm.dataType
    if (pointForm.unit && !/^[\u4e00-\u9fa5a-zA-Z0-9%℃°/²³μ]+$/.test(pointForm.unit)) {
      ElMessage.warning('单位包含不支持的字符'); return false
    }
    if (pointForm.unit && pointForm.unit.length > 20) {
      ElMessage.warning('单位不超过20字符'); return false
    }
    if ((dt === 'int' || dt === 'float') && pointForm.rangeMin !== '' && pointForm.rangeMax !== '') {
      const min = Number(pointForm.rangeMin), max = Number(pointForm.rangeMax)
      if (isNaN(min) || isNaN(max)) { ElMessage.warning('取值范围必须是有效数字'); return false }
      if (dt === 'int' && (!Number.isInteger(min) || !Number.isInteger(max))) { ElMessage.warning('int 类型取值范围必须为整数'); return false }
      if (dt === 'int' && (min < -2147483648 || max > 2147483647)) { ElMessage.warning('int 范围超出 -2147483648 ~ 2147483647'); return false }
      if (min >= max) { ElMessage.warning('最小值必须小于最大值'); return false }
      if (pointForm.step !== '' && pointForm.step !== null) {
        const step = Number(pointForm.step)
        if (isNaN(step) || step <= 0) { ElMessage.warning('步长必须为正数'); return false }
        if (step > max - min) { ElMessage.warning('步长不能大于取值范围'); return false }
      }
    }
    if (dt === 'string' && pointForm.maxLength != null) {
      if (pointForm.maxLength < 1 || pointForm.maxLength > 10240) { ElMessage.warning('最大长度范围 1-10240'); return false }
    }
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
    if (dt === 'bool') {
      if (pointForm.trueLabel && pointForm.trueLabel.length > 20) { ElMessage.warning('true含义不超过20字符'); return false }
      if (pointForm.falseLabel && pointForm.falseLabel.length > 20) { ElMessage.warning('false含义不超过20字符'); return false }
    }
    if (dt === 'array') {
      if (!pointForm.elementType) { ElMessage.warning('数组类型必须指定元素类型'); return false }
      if (!pointForm.maxLength || pointForm.maxLength < 1) { ElMessage.warning('最大长度必须 >= 1'); return false }
      if (arrayDefaultItems.value.length > pointForm.maxLength) { ElMessage.warning('默认值元素个数不能超过最大长度'); return false }
    }
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

  const addEnumValue = () => {
    enumValues.value.push({ value: '', description: '' })
  }

  const removeEnumValue = (index) => {
    enumValues.value.splice(index, 1)
  }

  const addArrayItem = () => {
    if (!pointForm.maxLength || arrayDefaultItems.value.length >= pointForm.maxLength) return
    if (pointForm.elementType === 'bool') {
      arrayDefaultItems.value.push(false)
    } else if (pointForm.elementType === 'int' || pointForm.elementType === 'float') {
      arrayDefaultItems.value.push(pointForm.elementType === 'int' ? 0 : 0.0)
    } else {
      arrayDefaultItems.value.push('')
    }
  }

  const removeArrayItem = (index) => {
    arrayDefaultItems.value.splice(index, 1)
  }

  const makePointJson = (pointType) => {
    return computed(() => {
      const type = typeof pointType === 'function' ? pointType() : (pointType?.value ?? pointType ?? 'properties')
      const schema = {
        identifier: pointForm.pointId || 'identifier',
        name: pointForm.name || '功能名称',
        type: type === 'properties' ? 'property' : type === 'events' ? 'event' : 'command',
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
      } else if (pointForm.dataType === 'array') {
        schema.dataType.specs = {
          elementType: pointForm.elementType || 'int',
          maxLength: pointForm.maxLength || 10
        }
        if (arrayDefaultItems.value.length > 0) {
          schema.dataType.specs.defaultValue = arrayDefaultItems.value
        }
        schema.accessMode = pointForm.access
      }
      if (pointForm.description) schema.description = pointForm.description
      return JSON.stringify(schema, null, 2)
    })
  }

  const fillFormFromPoint = (point) => {
    resetPointForm()
    pointForm.id = point.id
    pointForm.pointId = point.pointId || ''
    pointForm.name = point.name || ''
    pointForm.dataType = point.dataType || 'int'
    pointForm.access = point.access || 'readWrite'
    pointForm.unit = point.unit || ''
    pointForm.step = point.step || ''
    pointForm.maxLength = point.maxLength || null
    pointForm.defaultValue = point.defaultValue || ''
    pointForm.trueLabel = point.trueLabel || ''
    pointForm.falseLabel = point.falseLabel || ''
    pointForm.description = point.description || ''

    if (point.rangeJson) {
      try {
        const range = JSON.parse(point.rangeJson)
        pointForm.rangeMin = range.min ?? ''
        pointForm.rangeMax = range.max ?? ''
      } catch {}
    }

    if (point.enumValuesJson) {
      try {
        const parsed = JSON.parse(point.enumValuesJson)
        if (Array.isArray(parsed)) {
          enumValues.value = parsed
        } else if (parsed && typeof parsed === 'object') {
          enumValues.value = Object.entries(parsed).map(([value, description]) => ({ value, description }))
        } else {
          enumValues.value = []
        }
      } catch { enumValues.value = [] }
    } else {
      enumValues.value = []
    }

    if (point.defaultValue && point.defaultValue.startsWith('[')) {
      try { arrayDefaultItems.value = JSON.parse(point.defaultValue) } catch { arrayDefaultItems.value = [] }
    } else {
      arrayDefaultItems.value = []
    }
    pointForm.elementType = point.elementType || 'int'
  }

  const buildPointData = (pointType) => {
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
      pointType: pointType === 'properties' ? 'PROPERTY' : pointType === 'events' ? 'EVENT' : 'COMMAND'
    }

    if ((pointForm.dataType === 'int' || pointForm.dataType === 'float') && pointForm.rangeMin && pointForm.rangeMax) {
      saveData.rangeJson = JSON.stringify({ min: Number(pointForm.rangeMin), max: Number(pointForm.rangeMax) })
    }

    if (pointForm.dataType === 'enum' && enumValues.value.length > 0) {
      saveData.enumValuesJson = JSON.stringify(enumValues.value)
    }

    if (pointForm.dataType === 'array') {
      saveData.elementType = pointForm.elementType
      if (arrayDefaultItems.value.length > 0) {
        saveData.defaultValue = JSON.stringify(arrayDefaultItems.value)
      }
    }

    // 处理步长
    if ((pointForm.dataType === 'int' || pointForm.dataType === 'float') && pointForm.step) {
      saveData.step = pointForm.step
    }

    // 处理 bool 标签
    if (pointForm.dataType === 'bool') {
      saveData.trueLabel = pointForm.trueLabel
      saveData.falseLabel = pointForm.falseLabel
    }

    return saveData
  }

  return {
    pointForm,
    enumValues,
    arrayDefaultItems,
    pointRules,
    selectDataType,
    handleDataTypeChange,
    resetPointForm,
    normalizePointForm,
    validateBusinessRules,
    addEnumValue,
    removeEnumValue,
    addArrayItem,
    removeArrayItem,
    makePointJson,
    fillFormFromPoint,
    buildPointData
  }
}
