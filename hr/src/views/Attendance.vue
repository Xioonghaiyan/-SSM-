<template>
  <div class="attendance-container">
    <h2>考勤管理</h2>
    
    <el-tabs 
      v-model="activeTab" 
      class="attendance-tabs"
      @tab-change="handleTabChange"
    >
      <!-- 考勤统计标签页 -->
      <el-tab-pane label="考勤统计" name="stats">
        <el-card class="query-card">
          <div class="query-header">
            <span class="section-title">部门筛选</span>
            <el-select
              v-model="selectedDepartment"
              placeholder="请选择部门"
              clearable
              @change="handleDepartmentChange"
              class="department-select"
            >
              <el-option
                v-for="dept in departmentList"
                :key="dept.departmentId"
                :label="dept.departmentName"
                :value="dept.departmentId"
              />
            </el-select>
          </div>

          <!-- 考勤记录表格 -->
          <el-table
            v-if="selectedDepartment"
            :data="attendanceList"
            style="width: 100%"
            v-loading="loading"
            border
          >
            <el-table-column
              prop="employeeId"
              label="员工ID"
              width="100"
              align="center"
            />
            <el-table-column
              prop="departmentId"
              label="部门"
              width="120"
              align="center"
            >
              <template #default="scope">
                {{ getDepartmentName(scope.row.departmentId) }}
              </template>
            </el-table-column>
            <el-table-column
              label="打卡天数"
              width="100"
              align="center"
            >
              <template #default="scope">
                <span class="success-text">{{ scope.row.daysWorked }}天</span>
              </template>
            </el-table-column>
            <el-table-column
              label="请假天数"
              width="100"
              align="center"
            >
              <template #default="scope">
                <span class="info-text">{{ scope.row.leaveDays }}天</span>
              </template>
            </el-table-column>
            <el-table-column
              label="考勤状态"
              width="100"
              align="center"
            >
              <template #default="scope">
                <el-tag :type="getAttendanceStatusType(scope.row)">
                  {{ getAttendanceStatusText(scope.row) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <!-- 提示信息 -->
          <el-empty
            v-else
            description="请选择部门查看考勤记录"
          />
        </el-card>
      </el-tab-pane>

      <!-- 考勤打卡标签页 -->
      <el-tab-pane label="考勤打卡" name="clockIn">
        <!-- 普通员工的打卡界面 -->
        <el-card class="attendance-card">
          <div class="department-info">
            所属部门：{{ departmentName || '未分配' }}
          </div>
          
          <div class="clock-in-section">
            <div class="current-date">
              {{ currentDate }}
            </div>
            
            <el-button 
              type="primary" 
              size="large" 
              @click="handleClockIn"
              :disabled="hasClockIn"
            >
              {{ hasClockIn ? '今日已打卡' : '打卡' }}
            </el-button>
            
            <div v-if="clockInTime" class="clock-in-time">
              打卡时间：{{ clockInTime }}
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { getDepartmentName, fetchDepartments } from '../utils/nameMapping'

const userPermission = ref(2)
const employeeId = ref('')
const hasClockIn = ref(false)
const clockInTime = ref('')
const currentDate = ref('')
const router = useRouter()
const loading = ref(false)
const departmentList = ref([])
const selectedDepartment = ref(null)
const attendanceList = ref([])

// 添加部门名称
const departmentName = ref('')

// 添加标签页激活状态
const activeTab = ref('stats')

// 更新当前日期
const updateCurrentDate = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long'
  })
}

// 处理打卡
const handleClockIn = async () => {
  try {
    const response = await axios({
      method: 'post',
      url: '/attendance/add',
      params: {
        employeeId: employeeId.value
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('打卡成功！')
      hasClockIn.value = true
      // 设置打卡时间为当前时间
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
    } else {
      ElMessage.error(response.data.msg || '打卡失败')
    }
  } catch (error) {
    console.error('打卡错误：', error)
    ElMessage.error('打卡失败，请稍后重试')
  }
}

// 处理部门选择变化
const handleDepartmentChange = async (departmentId) => {
  if (!departmentId) {
    attendanceList.value = []
    return
  }
  
  loading.value = true
  try {
    // 修改请求路径为正确的后端接口路径
    const response = await axios.get(`/stats/queryByDepartmentId/${departmentId}`, {
      params: {
        departmentId
      }
    })

    if (response.data.code === 200) {
      attendanceList.value = response.data.data
      console.log('部门考勤记录:', {
        departmentId,
        data: response.data.data
      })
    } else {
      ElMessage.error(response.data.msg || '获取考勤记录失败')
    }
  } catch (error) {
    console.error('获取考勤记录错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      departmentId,
      url: error.config?.url,
      params: error.config?.params,
      headers: error.config?.headers
    })
    ElMessage.error('获取考勤记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取考勤状态类型
const getAttendanceStatusType = (record) => {
  const currentDay = new Date().getDate()
  const missedDays = currentDay - (record.daysWorked + record.leaveDays)
  
  if (missedDays > 3) return 'danger'
  if (missedDays > 0) return 'warning'
  return 'success'
}

// 获取考勤状态文本
const getAttendanceStatusText = (record) => {
  const currentDay = new Date().getDate()
  const missedDays = currentDay - (record.daysWorked + record.leaveDays)
  
  if (missedDays > 3) return '异常'
  if (missedDays > 0) return '警告'
  return '正常'
}

// 监听标签页变化
const handleTabChange = async (tab) => {
  if (tab === 'stats') {
    // 如果有选中的部门，重新加载数据
    if (selectedDepartment.value) {
      await handleDepartmentChange(selectedDepartment.value)
    }
  }
}

// 修改初始化逻辑
onMounted(async () => {
  // 从 localStorage 获取员工ID和权限信息
  employeeId.value = localStorage.getItem('employeeId')
  const storedPermissions = localStorage.getItem('permissions')
  if (storedPermissions !== null) {
    userPermission.value = Number(storedPermissions)
  }
  
  // 设置当前日期
  updateCurrentDate()
  
  // 获取部门列表
  const departments = await fetchDepartments()
  departmentList.value = departments
  
  // 获取当前用户的部门信息
  const deptId = localStorage.getItem('departmentId')
  if (deptId) {
    departmentName.value = getDepartmentName(Number(deptId))
    // 如果是普通员工，自动选择自己的部门
    if (userPermission.value === 2) {
      selectedDepartment.value = Number(deptId)
      await handleDepartmentChange(selectedDepartment.value)
    }
  }
})
</script>

<style scoped>
.attendance-container {
  padding: 20px;
}

.query-card {
  margin-bottom: 20px;
}

.query-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-right: 20px;
  color: #303133;
}

.department-select {
  width: 240px;
}

:deep(.el-table) {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-tag) {
  font-weight: bold;
}

.success-text {
  color: #67C23A;
  font-weight: bold;
}

.info-text {
  color: #409EFF;
  font-weight: bold;
}

.warning-text {
  color: #E6A23C;
  font-weight: bold;
}

.attendance-card {
  margin-top: 20px;
}

.clock-in-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
}

/* 添加当前日期样式 */
.current-date {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 30px;
}

.clock-in-time {
  margin-top: 20px;
  font-size: 16px;
  color: #409EFF;
}

.el-button {
  width: 200px;
  height: 60px;
  font-size: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #303133;
}

.department-info {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
  font-size: 16px;
  color: #606266;
}

.attendance-tabs {
  margin-top: 20px;
}

:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 25px;
}

:deep(.el-tabs__item.is-active) {
  font-weight: bold;
}

.query-card {
  margin-top: 20px;
}

.query-header {
  background-color: #f5f7fa;
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
}

.section-title {
  font-weight: bold;
  color: #303133;
  margin-right: 20px;
}

.department-select {
  width: 240px;
}

/* 美化表格样式 */
:deep(.el-table) {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: bold;
  padding: 12px 0;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table--border) {
  border: 1px solid #ebeef5;
}

/* 状态标签样式 */
:deep(.el-tag) {
  padding: 5px 12px;
  font-weight: bold;
}

.success-text {
  color: #67C23A;
  font-weight: bold;
}

.info-text {
  color: #409EFF;
  font-weight: bold;
}

.warning-text {
  color: #E6A23C;
  font-weight: bold;
}

/* 添加标签页切换动画 */
:deep(.el-tab-pane) {
  transition: opacity 0.3s;
}

:deep(.el-tabs__content) {
  overflow: visible;
}

/* 优化标签页样式 */
:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #dcdfe6;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

:deep(.el-tabs__item:hover) {
  color: #409EFF;
}

:deep(.el-tabs__item.is-active) {
  font-weight: bold;
  color: #409EFF;
}
</style> 