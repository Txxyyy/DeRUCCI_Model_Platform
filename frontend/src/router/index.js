import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        meta: { title: '仪表盘' },
        component: () => import('../views/Dashboard.vue')
      },
      // 产品管理
      {
        path: 'products',
        name: 'Products',
        meta: { title: '产品列表', permission: 'PRODUCT:R' },
        component: () => import('../views/product/ProductList.vue')
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        meta: { title: '产品详情', permission: 'PRODUCT:R' },
        component: () => import('../views/product/ProductDetail.vue')
      },
      {
        path: 'products/category-templates',
        name: 'CategoryTemplates',
        meta: { title: '品类标准模板', permission: 'CATEGORY_MODEL:R' },
        component: () => import('../views/thingmodel/CategoryThingModel.vue')
      },
      // 设备管理
      {
        path: 'devices',
        name: 'Devices',
        meta: { title: '设备列表', permission: 'DEVICE:R' },
        component: () => import('../views/device/DeviceList.vue')
      },
      {
        path: 'devices/monitor',
        name: 'DeviceMonitor',
        meta: { title: '设备监控', permission: 'DEVICE:R' },
        component: () => import('../views/device/DeviceMonitor.vue')
      },
      // OTA管理
      {
        path: 'ota/firmwares',
        name: 'Firmwares',
        meta: { title: '固件仓库', permission: 'OTA:R' },
        component: () => import('../views/ota/FirmwareList.vue')
      },
      {
        path: 'ota/tasks',
        name: 'OtaTasks',
        meta: { title: '升级任务', permission: 'OTA:R' },
        component: () => import('../views/ota/OtaTaskList.vue')
      },
      {
        path: 'ota',
        redirect: '/ota/firmwares'
      },
      // 用户管理（仅ADMIN）
      {
        path: 'users',
        name: 'Users',
        meta: { title: '用户管理', permission: 'USER:RW', adminOnly: true },
        component: () => import('../views/user/UserList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 登录页：已登录则跳首页
  if (to.path === '/login') {
    if (token) return next('/')
    return next()
  }

  // 需要认证的页面
  if (to.matched.some(r => r.meta.requiresAuth !== false)) {
    if (!token) {
      return next('/login')
    }

    // 权限检查
    const permission = to.meta.permission
    if (permission) {
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      if (user) {
        // adminOnly 检查
        if (to.meta.adminOnly && user.role !== 'ADMIN') {
          return next('/dashboard')
        }
        if (user.role === 'ADMIN') return next()
        const [mod, access] = permission.split(':')
        const perms = user.permissions || []
        const hasAccess = access === 'R'
          ? perms.includes(`${mod}:R`) || perms.includes(`${mod}:RW`)
          : perms.includes(permission)
        if (!hasAccess) {
          // 无权限，跳仪表盘
          return next('/dashboard')
        }
      }
    }
  }

  next()
})

export default router
