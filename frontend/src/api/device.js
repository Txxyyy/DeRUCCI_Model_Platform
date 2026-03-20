import api from './index'

export const deviceApi = {
  getList(params) {
    return api.get('/devices', { params })
  },
  getById(id) {
    return api.get(`/devices/${id}`)
  },
  getByKey(deviceKey) {
    return api.get(`/devices/key/${deviceKey}`)
  },
  getBySn(serialNumber) {
    return api.get(`/devices/sn/${serialNumber}`)
  },
  create(data) {
    return api.post('/devices', data)
  },
  update(id, data) {
    return api.put(`/devices/${id}`, data)
  },
  delete(id) {
    return api.delete(`/devices/${id}`)
  },
  online(id) {
    return api.put(`/devices/${id}/online`)
  },
  offline(id) {
    return api.put(`/devices/${id}/offline`)
  }
}
