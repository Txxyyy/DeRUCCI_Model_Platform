import api from './index'

// 品类标准模板 API（可被产品导入）
export const categoryTemplateApi = {
  // 获取品类模板列表（全量）
  getAllTemplates() {
    return api.get('/thing-model/templates')
  },

  // 按品类获取模板列表
  getTemplatesByCategory(category) {
    return api.get(`/thing-model/templates/${category}`)
  },

  // 获取品类模板详情
  getTemplateById(id) {
    return api.get(`/thing-model/template/${id}`)
  },

  // 创建品类模板
  createTemplate(data) {
    return api.post('/thing-model/templates', data)
  },

  // 更新品类模板
  updateTemplate(id, data) {
    return api.put(`/thing-model/templates/${id}`, data)
  },

  // 删除品类模板
  deleteTemplate(id) {
    return api.delete(`/thing-model/templates/${id}`)
  },

  // 发布品类模板
  publishTemplate(id) {
    return api.put(`/thing-model/templates/${id}/publish`)
  }
}
