import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authApi from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isDeveloper = computed(() => user.value?.role === 'DEVELOPER')
  const isFAE = computed(() => user.value?.role === 'FAE')
  const assignedProductIds = computed(() => user.value?.assignedProductIds || [])

  function hasPermission(module, access = 'R') {
    if (!user.value) return false
    if (user.value.role === 'ADMIN') return true
    const perms = user.value.permissions || []
    if (access === 'R') {
      return perms.includes(`${module}:R`) || perms.includes(`${module}:RW`)
    }
    return perms.includes(`${module}:${access}`)
  }

  function hasActionPermission(module, action) {
    if (!user.value) return false
    if (user.value.role === 'ADMIN') return true
    const perms = user.value.permissions || []
    return perms.includes(`${module}:${action}`)
  }

  function hasProductAccess(productId) {
    if (!user.value) return false
    if (user.value.role !== 'DEVELOPER') return true
    const ids = user.value.assignedProductIds || []
    return ids.includes(productId)
  }

  async function login(username, password) {
    const res = await authApi.login(username, password)
    const data = res.data
    token.value = data.accessToken
    refreshToken.value = data.refreshToken
    user.value = data.user
    localStorage.setItem('token', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('user', JSON.stringify(data.user))
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    router.push('/login')
  }

  async function doRefreshToken() {
    try {
      const res = await authApi.refresh(refreshToken.value)
      const data = res.data
      token.value = data.accessToken
      refreshToken.value = data.refreshToken
      user.value = data.user
      localStorage.setItem('token', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      localStorage.setItem('user', JSON.stringify(data.user))
      return data.accessToken
    } catch {
      logout()
      return null
    }
  }

  return { token, refreshToken, user, isLoggedIn, isAdmin, isDeveloper, isFAE, assignedProductIds, hasPermission, hasActionPermission, hasProductAccess, login, logout, doRefreshToken }
})
