import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建请求实例
const service = axios.create({
  baseURL: '/api/v1',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 用于取消重复请求的Map
const pendingMap = new Map()

function getPendingKey(config) {
  const { url, method, params, data } = config
  return [url, method, JSON.stringify(params), JSON.stringify(data)].join('&')
}

function addPending(config) {
  const key = getPendingKey(config)
  config.cancelToken =
    config.cancelToken ||
    new axios.CancelToken((cancel) => {
      if (!pendingMap.has(key)) {
        pendingMap.set(key, cancel)
      }
    })
}

function removePending(config) {
  const key = getPendingKey(config)
  if (pendingMap.has(key)) {
    const cancel = pendingMap.get(key)
    cancel('取消重复请求')
    pendingMap.delete(key)
  }
}

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 字典类等需要稳定返回的请求可设置 _noCancel，跳过"重复请求自动取消"
    // （避免页面切换/并发时被误取消导致下拉数据为空）
    if (!config._noCancel) {
      removePending(config)
      addPending(config)
    }
    // 统一添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    if (!response.config._noCancel) {
      removePending(response.config)
    }
    const res = response.data
    // 后端统一返回 { code, message, data }
    if (res.code !== undefined && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  (error) => {
    const config = error.config
    // 请求已取消
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }
    // 超时重试
    if (config && !config._retry && (error.code === 'ECONNABORTED' || error.message.includes('timeout'))) {
      config._retry = true
      config.retryCount = config.retryCount || 0
      if (config.retryCount < 2) {
        config.retryCount += 1
        return new Promise((resolve) => setTimeout(() => resolve(service(config)), 1000))
      }
    }
    let message = '网络异常，请稍后重试'
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        message = '登录已过期，请重新登录'
        localStorage.removeItem('token')
        router.push('/login')
      } else if (status === 403) {
        message = '没有权限访问'
      } else if (status === 404) {
        message = '请求资源不存在'
      } else if (status >= 500) {
        message = '服务器内部错误'
      } else if (data && data.message) {
        message = data.message
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
