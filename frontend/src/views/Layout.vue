<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">
        <h3>慕思IoT</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
        :collapse="false"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-sub-menu index="/products">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>产品管理</span>
          </template>
          <el-menu-item index="/products">产品列表</el-menu-item>
          <el-menu-item index="/products/categories">产品分类</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/thing-models">
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>物模型</span>
          </template>
          <el-menu-item index="/thing-models/category">品类物模型 ⭐</el-menu-item>
          <el-menu-item index="/thing-models/templates">模板库</el-menu-item>
          <el-menu-item index="/thing-models/my">我的模型</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/devices">
          <template #title>
            <el-icon><Monitor /></el-icon>
            <span>设备管理</span>
          </template>
          <el-menu-item index="/devices">设备列表</el-menu-item>
          <el-menu-item index="/devices/monitor">设备监控</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/ota">
          <template #title>
            <el-icon><UploadFilled /></el-icon>
            <span>OTA管理</span>
          </template>
          <el-menu-item index="/ota/firmwares">固件仓库</el-menu-item>
          <el-menu-item index="/ota/tasks">升级任务</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              <span>管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { House, User, Goods, Connection, Monitor, UploadFilled, UserFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #1a1a2e;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #16213e;
  border-bottom: 1px solid #0f3460;
}

.logo h3 {
  color: #e94560;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.sidebar-menu {
  border-right: none;
  background-color: #1a1a2e;
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: #a0aec0;
  background-color: transparent;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: #16213e;
  color: #fff;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #e94560 !important;
  color: #fff !important;
}

.sidebar-menu .el-sub-menu .el-menu-item {
  background-color: #16213e;
}

.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background-color: #0f3460;
}

:deep(.el-sub-menu__title) {
  color: #a0aec0 !important;
}

:deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #e94560 !important;
}

:deep(.el-sub-menu .el-sub-menu__title) {
  color: #a0aec0 !important;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-breadcrumb {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
