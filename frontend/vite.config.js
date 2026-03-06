import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/thing-models': {
        target: 'http://localhost:8083/api',
        changeOrigin: true
      },
      '/thing-model': {
        target: 'http://localhost:8083/api',
        changeOrigin: true
      }
    }
  }
})
