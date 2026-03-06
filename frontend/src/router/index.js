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
      {
        path: 'users',
        name: 'Users',
        meta: { title: '用户管理' },
        component: () => import('../views/user/UserList.vue')
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
        path: 'products/categories',
        name: 'ProductCategories',
        meta: { title: '产品分类' },
        component: () => import('../views/product/ProductCategories.vue')
      },
      // 物模型
      {
        path: 'thing-models/category',
        name: 'CategoryThingModel',
        meta: { title: '品类物模型' },
        component: () => import('../views/thingmodel/CategoryThingModel.vue')
      },
      {
        path: 'thing-models/templates',
        name: 'ThingModelTemplates',
        meta: { title: '模板库' },
        component: () => import('../views/thingmodel/ThingModelTemplates.vue')
      },
      {
        path: 'thing-models/my',
        name: 'ThingModelMy',
        meta: { title: '我的模型' },
        component: () => import('../views/thingmodel/ThingModelList.vue')
      },
      {
        path: 'thing-models',
        redirect: '/thing-models/my'
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
