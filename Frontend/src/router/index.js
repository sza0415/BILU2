import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import TaskList from '../views/TaskList.vue'
import Search from '../views/RecordSearch.vue'
import CaseManagement from '../views/CaseManagement.vue'
import TemplateManagement from '../views/TemplateManagement.vue'
import PackageManagement from '../views/PackageManagement.vue'
import AdminManagement from '../views/AdminManagement.vue'
import RecordManagement from '../views/RecordManagement.vue'

// 笔录相关组件
import RecordDetail from '../components/record/RecordDetail.vue'
import RecordEdit from '../components/record/RecordEdit.vue'
import RecordVerify from '../components/record/RecordVerify.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/task',
    name: 'TaskList',
    component: TaskList
  },
  {
    path: '/search',
    name: 'Search',
    component: Search
  },
  {
    path: '/record',
    name: 'RecordManagement',
    component: RecordManagement
  },
  {
    path: '/cases',
    name: 'CaseManagement',
    component: CaseManagement
  },
  {
    path: '/templates',
    name: 'TemplateManagement',
    component: TemplateManagement
  },
  {
    path: '/package',
    name: 'PackageManagement',
    component: PackageManagement
  },
  {
    path: '/admin',
    name: 'AdminManagement',
    component: AdminManagement
  },
  // 笔录相关路由
  {
    path: '/record/edit/:id?',
    name: 'RecordEdit',
    component: RecordEdit,
    props: route => ({ recordId: route.params.id })
  },
  {
    path: '/record/verify/:id',
    name: 'RecordVerify',
    component: RecordVerify,
    props: true
  },
  {
    path: '/record/:id',
    name: 'RecordDetail',
    component: RecordDetail,
    props: route => ({ recordId: route.params.id })
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 获取本地存储的用户信息
  const userInfo = localStorage.getItem('userInfo')
  
  // 如果访问的是登录页面，直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果用户未登录且访问的不是登录页面，则重定向到登录页面
  if (!userInfo && to.path !== '/login') {
    console.log('用户未登录，重定向到登录页面')
    next('/login')
    return
  }
  
  // 用户已登录，放行
  next()
})

export default router