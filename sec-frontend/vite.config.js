import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 说明：Element Plus 已在 main.js 中全量注册（app.use(ElementPlus) + 全量样式），
// 这里不再使用 unplugin 的 ElementPlusResolver 按需引入，否则会与全量引入冲突，
// 导致 Vite 开发时对组件样式依赖“按需动态预构建”，首次进入某些页面出现
// net::ERR_ABORTED 并强制刷新（表现为页面短暂白屏/渲染不成功）。
export default defineConfig({
  plugins: [
    vue()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 启动时就把大依赖整体预构建好，避免开发过程中动态优化依赖触发刷新（白屏）
  optimizeDeps: {
    include: ['element-plus', 'element-plus/es', '@element-plus/icons-vue', 'echarts', 'dayjs']
  },
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/smart_restaurant' + path
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    }
  }
})
