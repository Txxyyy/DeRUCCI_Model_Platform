import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 认证接口 → auth-service (8086)
      '/api/auth': {
        target: 'http://127.0.0.1:8086',
        changeOrigin: true,
        secure: false
      },
      // 物模型接口 → thing-model-service (8083)
      '/api/thing-models': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        secure: false
      },
      '/api/thing-model': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        secure: false
      },
      // OTA接口 → ota-service (8085)
      '/api/ota': {
        target: 'http://127.0.0.1:8085',
        changeOrigin: true,
        secure: false
      },
      // 设备接口 → device-service (8084)
      '/api/devices': {
        target: 'http://127.0.0.1:8084',
        changeOrigin: true,
        secure: false
      },
      // 用户接口 → user-service (8081)
      '/api/users': {
        target: 'http://127.0.0.1:8081',
        changeOrigin: true,
        secure: false
      },
      // 产品接口 → product-service (8082)
      '/api': {
        target: 'http://127.0.0.1:8082',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
