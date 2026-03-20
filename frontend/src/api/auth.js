import api from './index'

/**
 * 认证相关API（直接使用完整路径，不走 baseURL /api 前缀）
 */
const authApi = {
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },
  refresh(refreshToken) {
    return api.post('/auth/refresh', { refreshToken })
  },
  getMe() {
    return api.get('/auth/me')
  },
  getUserPermissions(userId) {
    return api.get(`/auth/users/${userId}/permissions`)
  },
  updateUserPermissions(userId, data) {
    return api.put(`/auth/users/${userId}/permissions`, data)
  }
}

export default authApi
