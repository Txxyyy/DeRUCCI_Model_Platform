<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-content">
        <svg class="brand-logo" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect width="48" height="48" rx="12" fill="rgba(255,255,255,0.15)"/>
          <path
d="M10 35V14l14 11 14-11v21" stroke="#FFFFFF" stroke-width="3"
                stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h1 class="brand-title">慕思 IoT</h1>
        <p class="brand-subtitle">智能设备管理平台</p>
        <p class="brand-desc">统一管理智能床垫、电动床、智能枕头等产品的设备接入、物模型配置与OTA升级</p>
      </div>
      <div class="brand-footer">
        <span>&copy; 2026 DeRUCCI. All rights reserved.</span>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-form-area">
      <div class="login-form-wrapper">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">请输入您的账号和密码</p>

        <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-hint">
          <span>默认管理员: admin / admin123</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    const msg = e.response?.data?.message || '登录失败，请检查用户名和密码'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
}

/* 左侧品牌区 */
.login-brand {
  flex: 0 0 45%;
  background: linear-gradient(135deg, #0B1E3D 0%, #1E4DA3 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px;
  position: relative;
}

.brand-content {
  text-align: center;
  max-width: 400px;
}

.brand-logo {
  width: 64px;
  height: 64px;
  margin-bottom: 24px;
}

.brand-title {
  color: #FFFFFF;
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 0.02em;
}

.brand-subtitle {
  color: #60AEFC;
  font-size: 16px;
  margin: 0 0 24px;
  font-weight: 500;
}

.brand-desc {
  color: rgba(255,255,255,0.6);
  font-size: 14px;
  line-height: 1.8;
  margin: 0;
}

.brand-footer {
  position: absolute;
  bottom: 32px;
  color: rgba(255,255,255,0.3);
  font-size: 12px;
}

/* 右侧登录区 */
.login-form-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F6F8;
}

.login-form-wrapper {
  width: 380px;
  padding: 40px;
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  margin: 0 0 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #6B7280;
  margin: 0 0 32px;
}

.login-hint {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: #9CA3AF;
}

/* 响应式：移动端隐藏左侧 */
@media (max-width: 768px) {
  .login-brand {
    display: none;
  }
  .login-form-area {
    padding: 20px;
  }
}
</style>
