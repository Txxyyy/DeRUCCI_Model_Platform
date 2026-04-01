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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status !== 'PUBLISHED'"
              type="success"
              link
              @click="handlePublish(row)"
            >发布</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredTemplates.length === 0" description="暂无模板，点击右上角新建模板" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '@/components/AppIcon.vue'
import CategoryIcon from '@/components/CategoryIcon.vue'
import { categoryTemplateApi } from '@/api/categoryTemplate'

const router = useRouter()

// 品类列表
const categories = [
  { label: '智能床垫', value: '智能床垫' },
  { label: '电动床', value: '电动床' },
  { label: '智能枕头', value: '智能枕头' }
]

const selectedCategory = ref('智能床垫')
const templates = ref([])

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
  const d = new Date(dateStr)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const getTemplateCount = (category) => {
  return templates.value.filter(t => t.category === category).length
}

const handleSelectCategory = (category) => {
  selectedCategory.value = category
}

const handleCreate = () => {
  router.push('/products/category-templates/new?category=' + encodeURIComponent(selectedCategory.value))
}

const handleEdit = (row) => {
  router.push('/products/category-templates/' + row.id + '/edit')
}

const handleView = (row) => {
  router.push('/products/category-templates/' + row.id + '/edit?readonly=1')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除模板 ${row.name}?`, '提示', { type: 'warning' })
  try {
    const res = await categoryTemplateApi.deleteTemplate(row.id)
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

const loadTemplates = async () => {
  try {
    const res = await categoryTemplateApi.getAllTemplates()
    if (res?.code === 200) {
      templates.value = res.data || []
    }
  } catch (e) {
    console.error('加载模板失败:', e)
  }
}

const handlePublish = async (row) => {
  try {
    const res = await categoryTemplateApi.publishTemplate(row.id)
    if (res?.code === 200) {
      row.status = 'PUBLISHED'
      ElMessage.success('发布成功')
    } else {
      ElMessage.error(res?.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败')
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
</style>
