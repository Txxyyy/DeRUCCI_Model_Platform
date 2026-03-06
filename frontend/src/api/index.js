import axios from 'axios'

const api = axios.create({
  // 使用相对路径，通过Vite代理访问后端
  baseURL: '/api',
  timeout: 10000
})

// Request interceptor
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 静默处理错误，让页面使用mock数据
    return Promise.reject(error)
  }
)

export default api
