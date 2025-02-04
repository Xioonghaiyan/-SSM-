import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/layout',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: '/employee',
        name: 'Employee',
        component: () => import('../views/Employee.vue')
      },
      {
        path: '/leave',
        name: 'Leave',
        component: () => import('../views/Leave.vue')
      },
      {
        path: '/leave/apply',
        name: 'LeaveApply',
        component: () => import('../views/leave/LeaveApply.vue')
      },
      {
        path: '/attendance',
        name: 'Attendance',
        component: () => import('../views/attendance/index.vue'),
        redirect: '/attendance/stats',
        children: [
          {
            path: 'stats',
            name: 'AttendanceStats',
            component: () => import('../views/attendance/AttendanceStats.vue'),
            meta: { title: '考勤统计' }
          },
          {
            path: 'clock',
            name: 'AttendanceClock',
            component: () => import('../views/attendance/AttendanceClock.vue'),
            meta: { title: '考勤打卡' }
          }
        ]
      },
      {
        path: '/department',
        name: 'Department',
        component: () => import('@/views/Department.vue')
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 修改路由权限配置
const routePermissions = {
  '/employee': [0, 1],        // 员工管理：超级管理员和部门管理员
  '/department': [0],         // 部门管理：仅超级管理员
  '/leave': [0, 1, 2],       // 请假管理：所有人可见
  '/attendance': [0, 1],      // 考勤管理：管理员可见
  '/attendance/clock': [2],   // 考勤打卡：仅普通员工可见
  '/dashboard': [0, 1, 2],    // 仪表盘：所有人可见
}

// 修改路由守卫
router.beforeEach((to, from, next) => {
  // 登录和注册页面直接放行
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  const token = localStorage.getItem('token')
  
  // 没有token，重定向到登录页
  if (!token) {
    if (to.path !== '/login') {
      next('/login')
    } else {
      next()
    }
    return
  }

  const permissions = Number(localStorage.getItem('permissions'))

  // 检查路由权限
  const requiredPermissions = routePermissions[to.path]
  if (requiredPermissions && !requiredPermissions.includes(permissions)) {
    // 如果是访问没有权限的页面，重定向到首页
    if (to.path !== '/dashboard') {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 其他情况放行
  next()
})

export default router 