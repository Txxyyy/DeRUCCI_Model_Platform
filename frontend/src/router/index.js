import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
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
        meta: { title: '产品列表' },
        component: () => import('../views/product/ProductList.vue')
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        meta: { title: '产品详情' },
        component: () => import('../views/product/ProductDetail.vue')
      },
      {
        path: 'products/category-templates',
        name: 'CategoryTemplates',
        meta: { title: '品类标准模板' },
        component: () => import('../views/thingmodel/CategoryThingModel.vue')
      },
      // 设备管理
      {
        path: 'devices',
        name: 'Devices',
        meta: { title: '设备列表' },
        component: () => import('../views/device/DeviceList.vue')
      },
      {
        path: 'devices/monitor',
        name: 'DeviceMonitor',
        meta: { title: '设备监控' },
        component: () => import('../views/device/DeviceMonitor.vue')
      },
      // OTA管理
      {
        path: 'ota/firmwares',
        name: 'Firmwares',
        meta: { title: '固件仓库' },
        component: () => import('../views/ota/FirmwareList.vue')
      },
      {
        path: 'ota/tasks',
        name: 'OtaTasks',
        meta: { title: '升级任务' },
        component: () => import('../views/ota/OtaTaskList.vue')
      },
      {
        path: 'ota',
        redirect: '/ota/firmwares'
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
