import api from './index'

export const otaApi = {
  // 固件管理
  getFirmwares(params) {
    return api.get('/ota/firmwares', { params })
  },
  getFirmwareById(id) {
    return api.get(`/ota/firmwares/${id}`)
  },
  createFirmware(data) {
    return api.post('/ota/firmwares', data)
  },
  publishFirmware(id) {
    return api.put(`/ota/firmwares/${id}/publish`)
  },

  // OTA任务管理
  getTasks(params) {
    return api.get('/ota/tasks', { params })
  },
  getTaskById(id) {
    return api.get(`/ota/tasks/${id}`)
  },
  createTask(data) {
    return api.post('/ota/tasks', data)
  },
  startTask(id) {
    return api.put(`/ota/tasks/${id}/start`)
  },
  cancelTask(id) {
    return api.put(`/ota/tasks/${id}/cancel`)
  }
}
