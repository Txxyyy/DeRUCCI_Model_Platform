<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <!-- Logo区域 -->
      <div class="sidebar-logo">
        <svg class="logo-svg" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect width="36" height="36" rx="9" fill="#1E4DA3"/>
          <path
d="M8 26V11l10 8 10-8v15" stroke="#FFFFFF" stroke-width="2.8"
                stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <div class="logo-text-block">
          <span class="logo-text-main">慕思</span>
          <span class="logo-text-sub">产品管理平台</span>
        </div>
      </div>

      <!-- 菜单 -->
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
        :collapse="false"
      >
        <el-menu-item index="/dashboard">
          <AppIcon name="home" :size="18" class="menu-icon" />
          <span>仪表盘</span>
        </el-menu-item>

        <el-sub-menu v-if="canAccess('PRODUCT')" index="/products">
          <template #title>
            <AppIcon name="package" :size="18" class="menu-icon" />
            <span>产品管理</span>
          </template>
          <el-menu-item index="/products">产品列表</el-menu-item>
          <el-menu-item index="/products/category-templates">品类标准模板</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="canAccess('DEVICE')" index="/devices">
          <template #title>
            <AppIcon name="monitor" :size="18" class="menu-icon" />
            <span>设备管理</span>
          </template>
          <el-menu-item index="/devices">设备列表</el-menu-item>
          <el-menu-item index="/devices/monitor">设备监控</el-menu-item>
        </el-sub-menu>

        <el-sub-menu v-if="canAccess('OTA')" index="/ota">
          <template #title>
            <AppIcon name="upload" :size="18" class="menu-icon" />
            <span>OTA管理</span>
          </template>
          <el-menu-item index="/ota/firmwares">固件仓库</el-menu-item>
          <el-menu-item index="/ota/tasks">升级任务</el-menu-item>
        </el-sub-menu>

        <el-menu-item v-if="authStore.isAdmin" index="/users">
          <AppIcon name="users" :size="18" class="menu-icon" />
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 底部用户区域 -->
      <div class="sidebar-footer">
        <div class="sidebar-user">
          <div class="user-avatar">{{ avatarLetter }}</div>
          <div class="user-info">
            <div class="user-name">{{ displayName }}</div>
            <div class="user-role">{{ displayRole }}</div>
          </div>
        </div>
      </div>
    </el-aside>

    <el-container>
      <el-header class="top-header">
        <div class="header-breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-trigger">
              <AppIcon name="user" :size="16" />
              <span>{{ displayName }}</span>
              <AppIcon name="chevron-down" :size="14" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import AppIcon from '@/components/AppIcon.vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const displayName = computed(() => authStore.user?.nickname || authStore.user?.username || '用户')
const displayRole = computed(() => {
  const roleMap = { 'ADMIN': '管理员', 'DEVELOPER': '开发者', 'FAE': 'FAE工程师' }
  return roleMap[authStore.user?.role] || '用户'
})
const avatarLetter = computed(() => (displayName.value || 'U').charAt(0).toUpperCase())

function canAccess(module) {
  return authStore.hasPermission(module, 'R')
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    authStore.logout()
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.sidebar { background: #0B1E3D; display: flex; flex-direction: column; border-right: 1px solid rgba(255,255,255,0.06); }
.sidebar-logo { height: 64px; display: flex; align-items: center; padding: 0 18px; gap: 10px; border-bottom: 1px solid rgba(255,255,255,0.06); flex-shrink: 0; }
.logo-svg { width: 36px; height: 36px; flex-shrink: 0; }
.logo-text-block { display: flex; flex-direction: column; gap: 1px; }
.logo-text-main { color: #FFFFFF; font-size: 16px; font-weight: 700; font-family: 'Inter', -apple-system, sans-serif; letter-spacing: 0.02em; line-height: 1.2; }
.logo-text-sub { color: #8FA3BF; font-size: 11px; font-weight: 400; line-height: 1.2; letter-spacing: 0.03em; }
.sidebar-menu { flex: 1; border-right: none; background: #0B1E3D !important; padding: 8px 0; overflow-y: auto; }
.sidebar-menu .menu-icon { flex-shrink: 0; margin-right: 10px; color: inherit; }
:deep(.sidebar-menu .el-menu-item), :deep(.sidebar-menu .el-sub-menu__title) { color: #A8C4E0; background: transparent !important; height: 40px; line-height: 40px; border-radius: 8px; margin: 2px 10px; padding-left: 14px !important; font-size: 13.5px; transition: all 0.15s ease; display: flex; align-items: center; }
:deep(.sidebar-menu .el-menu-item:hover), :deep(.sidebar-menu .el-sub-menu__title:hover) { background: rgba(255,255,255,0.05) !important; color: #E5E7EB !important; }
:deep(.sidebar-menu .el-menu-item.is-active) { background: rgba(96, 174, 252, 0.15) !important; color: #FFFFFF !important; position: relative; }
:deep(.sidebar-menu .el-menu-item.is-active::before) { content: ''; position: absolute; left: -10px; top: 50%; transform: translateY(-50%); width: 3px; height: 18px; background: #60AEFC; border-radius: 0 2px 2px 0; }
:deep(.sidebar-menu .el-sub-menu.is-active > .el-sub-menu__title) { color: #60AEFC !important; }
:deep(.sidebar-menu .el-sub-menu .el-menu) { background: transparent !important; }
:deep(.sidebar-menu .el-sub-menu .el-menu-item) { color: #7A9EC0 !important; background: transparent !important; margin-left: 18px; font-size: 13px; }
:deep(.sidebar-menu .el-sub-menu .el-menu-item:hover) { color: #D1D5DB !important; background: rgba(255,255,255,0.04) !important; }
:deep(.sidebar-menu .el-sub-menu .el-menu-item.is-active) { color: #FFFFFF !important; background: rgba(96, 174, 252, 0.12) !important; }
:deep(.el-sub-menu__icon-arrow) { color: #6B8BAD !important; }
.sidebar-footer { padding: 12px; border-top: 1px solid rgba(255,255,255,0.06); flex-shrink: 0; }
.sidebar-user { display: flex; align-items: center; gap: 10px; padding: 8px 6px; border-radius: 8px; cursor: pointer; transition: background 0.15s; }
.sidebar-user:hover { background: rgba(255,255,255,0.05); }
.user-avatar { width: 30px; height: 30px; border-radius: 8px; background: #1E4DA3; color: white; font-size: 13px; font-weight: 700; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.user-info .user-name { font-size: 13px; font-weight: 600; color: #E5E7EB; }
.user-info .user-role { font-size: 11px; color: #4B5563; }
.top-header { background: #FFFFFF; border-bottom: 1px solid #EAECF0; box-shadow: none; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; height: 56px; }
.header-breadcrumb { flex: 1; }
.header-right { display: flex; align-items: center; }
.user-trigger { display: flex; align-items: center; gap: 6px; cursor: pointer; color: #374151; font-size: 13px; font-weight: 500; padding: 6px 10px; border-radius: 8px; transition: background 0.15s; }
.user-trigger:hover { background: #F5F6F8; }
.main-content { background: #F5F6F8; padding: 20px; overflow-y: auto; }
</style>
