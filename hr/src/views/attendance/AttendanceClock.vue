<template>
  <div class="clock-container">
    <el-card class="attendance-card">
      <div class="department-info">
        所属部门：{{ departmentName || '未分配' }}
      </div>
      
      <div class="clock-in-section">
        <div class="current-date">
          {{ currentDate }}
        </div>
        
        <div class="current-time">
          {{ currentTime }}
        </div>
        
        <el-button 
          type="primary" 
          size="large" 
          @click="handleClockIn"
          :disabled="hasClockIn"
          class="clock-button"
        >
          {{ hasClockIn ? '今日已打卡' : '打卡' }}
        </el-button>
        
        <div v-if="clockInTime" class="clock-in-info">
          <div class="clock-in-time">打卡时间：{{ clockInTime }}</div>
          <div :class="['clock-in-status', getStatusClass]">
            打卡状态：{{ clockInStatus === 0 ? '正常' : '迟到' }}
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getDepartmentName } from '../../utils/nameMapping'

const departmentName = ref('')
const currentDate = ref('')
const currentTime = ref('')
const hasClockIn = ref(false)
const clockInTime = ref('')
const clockInStatus = ref(null)
let timer = null

// 更新当前日期和时间
const updateDateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  })
  
  currentTime.value = now.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 处理打卡
const handleClockIn = async () => {
  try {
    const employeeId = localStorage.getItem('employeeId')
    if (!employeeId) {
      ElMessage.error('未找到员工ID，请重新登录')
      return
    }

    console.log('发送打卡请求:', {
      employeeId,
      url: '/attendance/add'
    })

    // 发送打卡请求
    const response = await axios({
      method: 'post',
      url: '/attendance/add',
      params: {
        employeeId: Number(employeeId)
      },
      headers: {
        'Content-Type': 'application/json;charset=utf-8'
      }
    })

    console.log('打卡响应:', response.data)

    if (response.data.code === 200) {
      const now = new Date()
      clockInTime.value = now.toLocaleString('zh-CN', {
        hour12: false,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
      
      // 根据当前时间判断打卡状态
      const hour = now.getHours()
      clockInStatus.value = hour < 9 ? 0 : 1
      hasClockIn.value = true
      
      // 根据打卡状态显示不同的提示信息
      if (clockInStatus.value === 0) {
        ElMessage.success('打卡成功！')
      } else {
        ElMessage({
          message: '打卡成功，但已超过规定时间',
          type: 'warning'
        })
      }
    } else {
      ElMessage.error(response.data.msg || '打卡失败')
    }
  } catch (error) {
    console.error('打卡错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      employeeId: localStorage.getItem('employeeId'),
      config: {
        url: error.config?.url,
        method: error.config?.method,
        params: error.config?.params,
        headers: error.config?.headers
      }
    })
    
    if (error.response?.status === 404) {
      ElMessage.error('接口不存在，请检查接口地址')
    } else {
      ElMessage.error(error.response?.data?.msg || '打卡失败，请稍后重试')
    }
  }
}

// 获取状态样式类
const getStatusClass = computed(() => {
  return clockInStatus.value === 0 ? 'status-success' : 'status-warning'
})

// 初始化
onMounted(() => {
  // 获取部门信息
  const deptId = localStorage.getItem('departmentId')
  if (deptId) {
    departmentName.value = getDepartmentName(Number(deptId))
  }
  
  // 初始化时间显示
  updateDateTime()
  
  // 启动定时器，每秒更新时间
  timer = setInterval(updateDateTime, 1000)
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.clock-container {
  padding: 20px;
}

.attendance-card {
  max-width: 600px;
  margin: 0 auto;
}

.department-info {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  font-size: 16px;
  color: #606266;
  margin-bottom: 20px;
}

.clock-in-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
}

.current-date {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.current-time {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 30px;
  font-family: monospace;
}

.clock-button {
  width: 200px;
  height: 60px;
  font-size: 20px;
  margin: 20px 0;
}

.clock-in-info {
  margin-top: 20px;
  text-align: center;
}

.clock-in-time {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.clock-in-status {
  font-size: 16px;
  font-weight: bold;
}

.status-success {
  color: #67C23A;
}

.status-warning {
  color: #E6A23C;
}

:deep(.el-card__body) {
  padding: 20px;
}

.el-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.3);
}

.el-button {
  transition: all 0.3s;
}
</style> 