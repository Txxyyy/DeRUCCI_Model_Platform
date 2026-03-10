import api from './index'

export const categoryApi = {
  getList() {
    return api.get('/categories')
  },
  create(data) {
    return api.post('/categories', data)
  },
  update(id, data) {
    return api.put(`/categories/${id}`, data)
  },
  delete(id) {
    return api.delete(`/categories/${id}`)
  }
}

export const productApi = {
  getList(params) {
    return api.get('/products', { params })
  },
  getById(id) {
    return api.get(`/products/${id}`)
  },
  getByCode(code) {
    return api.get(`/products/code/${code}`)
  },
  create(data) {
    return api.post('/products', data)
  },
  update(id, data) {
    return api.put(`/products/${id}`, data)
  },
  delete(id) {
    return api.delete(`/products/${id}`)
  },
  publish(id) {
    return api.put(`/products/${id}/publish`)
  },
  offline(id) {
    return api.put(`/products/${id}/offline`)
  }
}
