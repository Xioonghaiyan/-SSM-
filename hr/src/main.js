import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const app = createApp(App)

// 设置 axios 默认配置
axios.defaults.timeout = 5000
axios.defaults.withCredentials = true
axios.defaults.baseURL = '/api'
axios.defaults.headers.common['Content-Type'] = 'application/json;charset=utf-8'
axios.defaults.headers.common['Accept'] = 'application/json'

// 请求拦截器
axios.interceptors.request.use(
  config => {
    // 添加跨域请求头
    config.headers['Access-Control-Allow-Origin'] = '*'
    config.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, OPTIONS'
    config.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization'

    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 403:
          localStorage.removeItem('token')
          localStorage.removeItem('employeeId')
          localStorage.removeItem('permissions')
          ElMessage.error('权限不足或登录已过期，请重新登录')
          router.push('/login')
          break
        case 400:
          ElMessage.error(error.response.data.msg || '请求参数错误')
          break
        default:
          ElMessage.error(error.response.data?.msg || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

app.use(ElementPlus)
app.use(router)
app.mount('#app') 