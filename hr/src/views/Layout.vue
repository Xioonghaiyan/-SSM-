<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="logo">
        <span>HR管理系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <!-- 超级管理员菜单 -->
        <template v-if="userPermission === 0">
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-menu-item index="/employee">
            <el-icon><User /></el-icon>
            <span>员工管理</span>
          </el-menu-item>

          <el-menu-item index="/department">
            <el-icon><Grid /></el-icon>
            <span>部门管理</span>
          </el-menu-item>

          <el-menu-item index="/leave">
            <el-icon><Calendar /></el-icon>
            <span>请假管理</span>
          </el-menu-item>

          <el-menu-item index="/attendance">
            <el-icon><Clock /></el-icon>
            <span>考勤管理</span>
          </el-menu-item>
        </template>

        <!-- 部门管理员菜单 -->
        <template v-else-if="userPermission === 1">
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-menu-item index="/employee">
            <el-icon><User /></el-icon>
            <span>员工管理</span>
          </el-menu-item>

          <el-menu-item index="/leave">
            <el-icon><Calendar /></el-icon>
            <span>请假管理</span>
          </el-menu-item>

          <el-menu-item index="/attendance">
            <el-icon><Clock /></el-icon>
            <span>考勤管理</span>
          </el-menu-item>
        </template>

        <!-- 普通员工菜单 -->
        <template v-else>
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-menu-item index="/attendance/clock">
            <el-icon><Clock /></el-icon>
            <span>考勤打卡</span>
          </el-menu-item>

          <el-menu-item index="/leave">
            <el-icon><Calendar /></el-icon>
            <span>请假管理</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container>
      <el-header>
        <div class="header-left">
          <el-icon class="toggle-sidebar" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-icon class="avatar-icon"><UserFilled /></el-icon>
              <span class="username">员工ID: {{ employeeId }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  User, 
  Calendar, 
  Clock, 
  ArrowDown, 
  HomeFilled, 
  Fold, 
  Expand, 
  UserFilled, 
  DataLine, 
  Timer,
  Grid
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)
const employeeId = ref('')
const userPermission = ref(2)
const isCollapse = ref(false)
const statsData = ref({
  employeeId: '',
  departmentId: '',
  leaveDays: 0,
  daysWorked: 0,
  daysMissed: 0
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 获取统计数据
const fetchStatsData = async () => {
  try {
    employeeId.value = localStorage.getItem('employeeId')
    if (!employeeId.value) {
      ElMessage.error('未找到员工ID，请重新登录')
      router.push('/login')
      return
    }

    console.log('开始请求统计数据:', {
      url: `/stats/query/${employeeId.value}`,
      employeeId: employeeId.value
    })

    const response = await axios({
      method: 'get',
      url: `/stats/query/${employeeId.value}`  // 移除重复的 /api 前缀
    })

    console.log('统计数据响应:', response.data)

    if (response.data.code === 200) {
      // 处理统计数据
      console.log('获取统计数据成功:', response.data.data)
    } else {
      ElMessage.error(response.data.msg || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      employeeId: employeeId.value,
      token: localStorage.getItem('token')
    })
    
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('employeeId')
      localStorage.removeItem('permissions')
      router.push('/login')
    } else {
      ElMessage.error('获取统计数据失败，请稍后重试')
    }
  }
}

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/dashboard') {
      fetchStatsData()
    }
  }
)

// 初始化时如果在 dashboard 页面就获取数据
onMounted(() => {
  if (route.path === '/dashboard') {
    fetchStatsData()
  }
})

// 初始化数据
onMounted(() => {
  // 从 localStorage 获取权限信息
  const storedPermissions = localStorage.getItem('permissions')
  employeeId.value = localStorage.getItem('employeeId')
  
  console.log('从localStorage获取的权限值:', storedPermissions)
  
  // 如果没有权限信息，跳转到登录页
  if (!employeeId.value) {
    router.push('/login')
    return
  }

  // 设置用户权限 - 修改这里的逻辑
  if (storedPermissions !== null) {
    userPermission.value = Number(storedPermissions)
    console.log('设置的用户权限:', userPermission.value)
  } else {
    userPermission.value = 2 // 只有在没有权限值时才设置默认值
    console.log('设置默认用户权限:', userPermission.value)
  }

  // 检查路由权限
  checkRoutePermission()
})

// 检查路由权限
const checkRoutePermission = () => {
  const currentPath = route.path

  // 定义权限路由映射
  const permissionRoutes = {
    0: ['/dashboard', '/employee', '/attendance', '/leave', '/department', '/layout'],  // 超级管理员
    1: ['/dashboard', '/employee', '/attendance', '/leave', '/layout'],  // 部门管理员
    2: ['/dashboard', '/attendance/clock', '/leave', '/layout']  // 普通员工
  }

  // 检查当前路由是否在用户权限允许的范围内
  if (!permissionRoutes[userPermission.value]?.includes(currentPath)) {
    // 如果当前路由不在权限范围内，重定向到第一个允许的路由
    router.push(permissionRoutes[userPermission.value][0])
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 清除登录信息
      localStorage.removeItem('token')
      localStorage.removeItem('employeeId')
      localStorage.removeItem('permissions')
      // 跳转到登录页
      router.push('/login')
    })
  }
}

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3648;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.menu {
  border-right: none;
  height: calc(100vh - 60px);
}

:deep(.el-menu-item) {
  height: 56px;
  line-height: 56px;
  padding: 0 20px !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

:deep(.el-menu-item:hover) {
  background-color: #263445 !important;
}

:deep(.el-menu-item .el-icon) {
  width: 24px;
  height: 24px;
  margin-right: 16px;
}

/* 头部样式 */
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.avatar-icon {
  font-size: 20px;
  margin-right: 8px;
}

.username {
  margin: 0 8px;
}

/* 主要内容区样式 */
.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

/* 动画效果 */
.el-aside,
.el-menu {
  transition: all 0.3s;
}
</style> 