import api from './index'

// 产品物模型 API（与产品一一对应）
export const productThingModelApi = {
  // 获取物模型
  getThingModel(id) {
    return api.get(`/thing-models/${id}`)
  },

  // 获取物模型列表
  getThingModels(params) {
    return api.get('/thing-models', { params })
  },

  // 创建物模型
  createThingModel(data) {
    return api.post('/thing-models', data)
  },

  // 更新物模型
  updateThingModel(id, data) {
    return api.put(`/thing-models/${id}`, data)
  },

  // 删除物模型
  deleteThingModel(id) {
    return api.delete(`/thing-models/${id}`)
  },

  // 获取物模型下的所有功能点
  getPoints(thingModelId) {
    return api.get(`/thing-models/${thingModelId}/points`)
  },

  // 获取功能点详情
  getPointById(id) {
    return api.get(`/thing-model/points/${id}`)
  },

  // 创建功能点
  createPoint(data) {
    return api.post('/thing-model/points', data)
  },

  // 更新功能点
  updatePoint(id, data) {
    return api.put(`/thing-model/points/${id}`, data)
  },

  // 删除功能点
  deletePoint(id) {
    return api.delete(`/thing-model/points/${id}`)
  }
}
