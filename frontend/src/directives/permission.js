import { useAuthStore } from '@/stores/auth'

/**
 * v-permission 指令
 * 用法: v-permission="'PRODUCT:RW'" 或 v-permission="'OTA:PUSH'"
 * 支持 access 格式（R/RW）和 action 格式（PUBLISH/DELETE/OFFLINE/PUSH）
 * 无权限时隐藏元素
 */
export const permissionDirective = {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

function checkPermission(el, binding) {
  const value = binding.value
  if (!value) return

  const authStore = useAuthStore()
  const [module, accessOrAction] = value.split(':')
  const actionValues = ['PUBLISH', 'DELETE', 'OFFLINE', 'PUSH']

  let hasAccess
  if (actionValues.includes(accessOrAction)) {
    hasAccess = authStore.hasActionPermission(module, accessOrAction)
  } else {
    hasAccess = authStore.hasPermission(module, accessOrAction || 'R')
  }

  el.style.display = hasAccess ? '' : 'none'
}

/**
 * v-permission-disabled 指令
 * 用法: v-permission-disabled="'DEVICE:RW'"
 * 无权限时禁用按钮（添加 disabled 样式），而不是隐藏
 */
export const permissionDisabledDirective = {
  mounted(el, binding) {
    checkPermissionDisabled(el, binding)
  },
  updated(el, binding) {
    checkPermissionDisabled(el, binding)
  }
}

function checkPermissionDisabled(el, binding) {
  const value = binding.value
  if (!value) return

  const authStore = useAuthStore()
  const [module, accessOrAction] = value.split(':')
  const actionValues = ['PUBLISH', 'DELETE', 'OFFLINE', 'PUSH']

  let hasAccess
  if (actionValues.includes(accessOrAction)) {
    hasAccess = authStore.hasActionPermission(module, accessOrAction)
  } else {
    hasAccess = authStore.hasPermission(module, accessOrAction || 'R')
  }

  if (!hasAccess) {
    el.setAttribute('disabled', 'true')
    el.classList.add('is-disabled')
    el.style.pointerEvents = 'none'
  } else {
    el.removeAttribute('disabled')
    el.classList.remove('is-disabled')
    el.style.pointerEvents = ''
  }
}

export default {
  install(app) {
    app.directive('permission', permissionDirective)
    app.directive('permission-disabled', permissionDisabledDirective)
  }
}
