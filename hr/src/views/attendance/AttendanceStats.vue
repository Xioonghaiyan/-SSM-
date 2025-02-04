<template>
  <div class="stats-container">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getDepartmentName, fetchDepartments } from '../../utils/nameMapping'

const loading = ref(false)
const departmentList = ref([])
const selectedDepartment = ref(null)
const attendanceList = ref([])

// 处理部门选择变化
const handleDepartmentChange = async (departmentId) => {
  if (!departmentId) {
    attendanceList.value = []
    return
  }
  
  loading.value = true
  try {
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

// 初始化数据
onMounted(async () => {
  const departments = await fetchDepartments()
  departmentList.value = departments
})
</script>

<style scoped>
.stats-container {
  padding: 20px;
}

.query-card {
  margin-bottom: 20px;
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
</style> 