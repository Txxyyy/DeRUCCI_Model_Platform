import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8082',
        changeOrigin: true,
        secure: false,
        bypass(req) {
          req.headers.host = 'localhost:8082'
        }
      },
      '/thing-models': {
        target: 'http://127.0.0.1:8083/api',
        changeOrigin: true,
        secure: false,
        bypass(req) {
          req.headers.host = 'localhost:8083'
        }
      },
      '/thing-model': {
        target: 'http://127.0.0.1:8083/api',
        changeOrigin: true,
        secure: false,
        bypass(req) {
          req.headers.host = 'localhost:8083'
        }
      }
    }
  }
})
