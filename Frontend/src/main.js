import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axiosInstance from './utils/axios'

// 导入Bootstrap样式和脚本
import 'bootstrap/dist/css/bootstrap.min.css'
import * as bootstrap from 'bootstrap'

// 将Bootstrap设置为全局变量，以便在组件中直接使用
window.bootstrap = bootstrap

// 定义Vue特性标志，解决警告问题
window.__VUE_PROD_HYDRATION_MISMATCH_DETAILS__ = false

// 创建Vue应用实例
const app = createApp(App)

// 注册路由
app.use(router)

// 将axios实例设置为全局属性
app.config.globalProperties.$axios = axiosInstance

// 挂载应用
app.mount('#app')
