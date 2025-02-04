<template>
  <div class="dashboard-container">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon class="header-icon"><DataLine /></el-icon>
            <span>考勤统计</span>
          </div>
          <div class="header-info">
            <el-tag type="info">{{ statsData.departmentName || '未分配部门' }}</el-tag>
            <el-tag type="success" class="ml-2">员工ID: {{ statsData.employeeId }}</el-tag>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-card-header">
              <el-icon class="success"><CircleCheckFilled /></el-icon>
              <span>打卡天数</span>
            </div>
          </template>
          <div class="stat-value success">{{ statsData.daysWorked }}天</div>
        </el-card>

        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-card-header">
              <el-icon class="warning"><Timer /></el-icon>
              <span>迟到天数</span>
            </div>
          </template>
          <div class="stat-value warning">{{ statsData.lateDays }}天</div>
        </el-card>

        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-card-header">
              <el-icon class="info"><Calendar /></el-icon>
              <span>请假天数</span>
            </div>
          </template>
          <div class="stat-value info">{{ statsData.leaveDays }}天</div>
        </el-card>
      </div>

      <!-- 饼图 -->
      <div class="chart-container">
        <div ref="pieChartRef" style="width: 100%; height: 400px;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'
import { getDepartmentName, fetchDepartments } from '../utils/nameMapping'
import { 
  DataLine, 
  CircleCheckFilled, 
  WarningFilled, 
  Timer,
  Calendar
} from '@element-plus/icons-vue'

const pieChartRef = ref(null)
let pieChart = null

const statsData = ref({
  employeeId: '',
  departmentId: '',
  departmentName: '',
  daysWorked: 0,  // 打卡天数
  lateDays: 0,    // 迟到天数
  leaveDays: 0    // 请假天数
})

const router = useRouter()

// 获取统计数据
const fetchStatsData = async () => {
  try {
    const employeeId = localStorage.getItem('employeeId')
    if (!employeeId) {
      ElMessage.error('未找到员工ID，请重新登录')
      router.push('/login')
      return
    }

    // 先获取部门列表以确保能获取部门名称
    await fetchDepartments()

    const response = await axios.get(`/stats/query/${employeeId}`)
    
    if (response.data.code === 200) {
      const data = response.data.data
      // 获取当前日期
      const today = new Date()
      const currentDay = today.getDate()  // 获取当月的天数
      const totalDays = Math.min(currentDay, today.getDay() === 0 || today.getDay() === 6 ? 
        currentDay - Math.floor(currentDay / 7) * 2 : 
        currentDay - Math.floor((currentDay - 1) / 7) * 2)  // 排除周末
      
      statsData.value = {
        employeeId: data.employeeId,
        departmentId: data.departmentId,
        departmentName: getDepartmentName(data.departmentId),
        daysWorked: data.daysWorked,
        leaveDays: data.leaveDays,
        lateDays: Math.max(0, totalDays - data.daysWorked - data.leaveDays)  // 确保不会出现负数
      }
      
      console.log('考勤统计数据:', {
        totalDays,
        daysWorked: data.daysWorked,
        leaveDays: data.leaveDays,
        lateDays: statsData.value.lateDays
      })
      
      // 更新饼图
      initPieChart()
    } else {
      ElMessage.error(response.data.msg || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据错误：', error)
    console.error('错误详情：', {
      employeeId: localStorage.getItem('employeeId'),
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data
    })
    ElMessage.error('获取统计数据失败，请稍后重试')
  }
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return

  if (pieChart) {
    pieChart.dispose()
  }

  pieChart = echarts.init(pieChartRef.value)
  
  const option = {
    title: {
      text: '本月考勤详情',
      subtext: new Date().toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long' 
      }),
      left: 'center',
      top: 20,
      textStyle: {
        fontSize: 20,
        fontWeight: 'bold'
      },
      subtextStyle: {
        fontSize: 14
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}天 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#eee',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      }
    },
    legend: {
      orient: 'vertical',
      left: '5%',
      top: 'middle',
      itemGap: 20,
      textStyle: {
        fontSize: 14
      }
    },
    series: [
      {
        name: '考勤详情',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c}天\n{d}%',
          fontSize: 14,
          fontWeight: 'bold'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        data: [
          { 
            value: statsData.value.daysWorked, 
            name: '打卡成功',
            itemStyle: { color: '#67C23A' }
          },
          { 
            value: statsData.value.lateDays, 
            name: '迟到',
            itemStyle: { color: '#E6A23C' }
          },
          { 
            value: statsData.value.leaveDays, 
            name: '请假',
            itemStyle: { color: '#409EFF' }
          }
        ]
      }
    ]
  }

  pieChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  if (pieChart) {
    pieChart.resize()
  }
}

window.addEventListener('resize', handleResize)

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (pieChart) {
    pieChart.dispose()
  }
})

onMounted(() => {
  fetchStatsData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  height: 100%;
  background-color: #f5f7fa;
}

.dashboard-card {
  height: calc(100% - 40px);
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.header-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409EFF;
}

.header-info {
  display: flex;
  gap: 10px;
}

.stats-cards {
  display: flex;
  justify-content: space-between;
  margin: 20px 0;
  gap: 20px;
  padding: 0 20px;
}

.stat-card {
  flex: 1;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}

.stat-card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  color: #606266;
}

.stat-card-header .el-icon {
  margin-right: 8px;
  font-size: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
}

.success, .success .el-icon {
  color: #67C23A;
}

.warning, .warning .el-icon {
  color: #E6A23C;
}

.info, .info .el-icon {
  color: #409EFF;
}

.chart-container {
  margin: 20px 0;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.05);
}

:deep(.el-card__header) {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 15px;
}

.ml-2 {
  margin-left: 8px;
}
</style> 