<template>
  <div class="leave-container">
    <div class="header-section">
      <h2>请假管理</h2>
      <el-button 
        type="primary" 
        @click="$router.push('/leave/apply')"
        class="apply-button"
        :icon="Plus"
      >
        请假申请
      </el-button>
    </div>
    
    <!-- 部门筛选 -->
    <el-card class="filter-card" v-if="isAdmin">
      <div class="filter-header">
        <span class="section-title">部门筛选</span>
        <el-select
          v-model="selectedDepartment"
          placeholder="请选择部门"
          clearable
          @change="handleDepartmentChange"
          class="department-select"
          :disabled="userPermission === 1"
        >
          <el-option
            v-for="dept in departmentList"
            :key="dept.departmentId"
            :label="dept.departmentName"
            :value="dept.departmentId"
          />
        </el-select>
      </div>
    </el-card>

    <!-- 请假记录表格 -->
    <el-card class="leave-card">
      <template #header>
        <div class="card-header">
          <span>{{ isAdmin ? (selectedDepartment ? `${getDepartmentName(selectedDepartment)}的请假记录` : '部门请假记录') : '我的请假记录' }}</span>
        </div>
      </template>
      
      <el-table 
        :data="leaveRecords" 
        style="width: 100%"
        v-loading="loading"
        border
        stripe
        highlight-current-row
      >
        <el-table-column 
          prop="employeeId" 
          label="员工ID" 
          width="120"
          align="center"
        />
        <el-table-column 
          label="请假时间" 
          width="180"
          align="center"
        >
          <template #default="scope">
            {{ formatDateTime(scope.row.leaveTime) }}
          </template>
        </el-table-column>
        <el-table-column 
          label="请假天数" 
          width="100"
          align="center"
        >
          <template #default="scope">
            <span class="days-text">{{ scope.row.leaveDays }}天</span>
          </template>
        </el-table-column>
        <el-table-column 
          label="请假类型" 
          width="120"
          align="center"
        >
          <template #default="scope">
            {{ scope.row.type }}
          </template>
        </el-table-column>
        <el-table-column 
          prop="leaveReason" 
          label="请假原因"
          min-width="250"
          show-overflow-tooltip
        >
          <template #default="scope">
            <div class="reason-text">{{ scope.row.leaveReason }}</div>
          </template>
        </el-table-column>
        <el-table-column 
          label="审核状态" 
          width="160"
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <div class="status-column">
              <el-tag :type="getStatusType(scope.row.approvalStatus)">
                {{ getStatusText(scope.row.approvalStatus) }}
              </el-tag>
              <el-button
                v-if="scope.row.approvalStatus === 0 && isAdmin"
                type="primary"
                size="small"
                @click="handleApproval(scope.row)"
                :loading="scope.row.approving"
              >
                审核
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加空状态提示 -->
      <el-empty
        v-if="!loading && leaveRecords.length === 0"
        :description="getEmptyText()"
      />
    </el-card>

    <!-- 添加审核对话框 -->
    <el-dialog
      v-model="approvalDialogVisible"
      title="请假审核"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div v-loading="!currentLeave" class="approval-form">
        <template v-if="currentLeave">
          <p class="info-item"><strong>员工ID：</strong>{{ currentLeave.employeeId }}</p>
          <p class="info-item"><strong>部门：</strong>{{ getDepartmentName(currentLeave.departmentId) }}</p>
          <p class="info-item"><strong>请假类型：</strong>{{ currentLeave.type }}</p>
          <p class="info-item"><strong>请假天数：</strong>{{ currentLeave.leaveDays }}天</p>
          <p class="info-item"><strong>请假时间：</strong>{{ formatDateTime(currentLeave.leaveTime) }}</p>
          <p class="info-item"><strong>请假原因：</strong>{{ currentLeave.leaveReason }}</p>
          <p class="info-item"><strong>审核状态：</strong>
            <el-tag :type="getStatusType(currentLeave.approvalStatus)">
              {{ getStatusText(currentLeave.approvalStatus) }}
            </el-tag>
          </p>
        </template>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="approvalDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitApproval" 
            :loading="approvalSubmitting"
            :disabled="!currentLeave?.id"
          >
            确认审核
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'
import { getDepartmentName } from '../utils/nameMapping'

const leaveRecords = ref([])
const loading = ref(false)
const employeeId = ref('')
const userPermission = ref(2)
const departmentList = ref([])
const selectedDepartment = ref(null)

// 添加审核相关的响应式变量
const approvalDialogVisible = ref(false)
const currentLeave = ref(null)
const approvalFormRef = ref(null)
const approvalSubmitting = ref(false)
const approvalForm = reactive({})

// 添加计算属性判断是否为管理员（包括超级管理员和部门管理员）
const isAdmin = computed(() => userPermission.value === 0 || userPermission.value === 1)

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  try {
    const date = new Date(dateTime)
    if (isNaN(date.getTime())) {
      return dateTime // 如果转换失败，返回原始值
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateTime // 发生错误时返回原始值
  }
}

const getStatusType = (status) => {
  const types = {
    0: 'info',    // 待审核
    1: 'success', // 已通过
    2: 'danger'   // 已拒绝
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return texts[status] || '未知'
}

const getLeaveTypeName = (typeName) => {
  return typeName || '未知类型'
}

const getEmptyText = () => {
  if (!isAdmin.value) {
    return '暂无请假记录'
  }
  return selectedDepartment.value ? '该部门暂无请假记录' : '请选择部门查看请假记录'
}

// 获取部门列表
const fetchDepartmentList = async () => {
  loading.value = true
  try {
    const employeeId = localStorage.getItem('employeeId')
    if (!employeeId) {
      ElMessage.error('未找到员工ID，请重新登录')
      return []
    }

    let response
    if (userPermission.value === 0) {
      // 超级管理员获取所有部门
      response = await axios.get('/department/query')
    } else if (userPermission.value === 1) {
      // 部门管理员只获取自己的部门
      response = await axios.get(`/department/queryOne/${employeeId}`)
    }

    if (response.data.code === 200) {
      if (userPermission.value === 0) {
        // 超级管理员显示所有部门
        departmentList.value = response.data.data
      } else if (userPermission.value === 1) {
        // 部门管理员只显示自己的部门
        const department = response.data.data
        departmentList.value = [{
          departmentId: department.departmentId,
          departmentName: department.departmentName
        }]
        // 自动选择该部门并加载请假记录
        selectedDepartment.value = department.departmentId
        await handleDepartmentChange(department.departmentId)
      }
      console.log('部门列表:', departmentList.value)
    } else {
      ElMessage.error(response.data.msg || '获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      employeeId: localStorage.getItem('employeeId'),
      userPermission: userPermission.value
    })
    ElMessage.error('获取部门列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 修改部门选择处理函数
const handleDepartmentChange = async (departmentId) => {
  if (!departmentId) {
    leaveRecords.value = []
    return
  }
  
  loading.value = true
  try {
    console.log('查询部门请假记录:', departmentId)
    const response = await axios.get(`/leaveRequest/queryByDepartmentId/${departmentId}`)

    if (response.data.code === 200) {
      leaveRecords.value = response.data.data.map(record => {
        let formattedLeaveTime = record.leaveTime
        try {
          const date = new Date(record.leaveTime)
          if (!isNaN(date.getTime())) {
            formattedLeaveTime = date.toLocaleString('zh-CN')
          }
        } catch (error) {
          console.error('日期格式化错误:', error)
        }
        return {
          ...record,
          leaveTime: formattedLeaveTime
        }
      })
      
      console.log('部门请假记录:', leaveRecords.value)
    } else {
      ElMessage.error(response.data.msg || '获取请假记录失败')
    }
  } catch (error) {
    console.error('获取部门请假记录错误：', error)
    ElMessage.error('获取请假记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const fetchLeaveRecords = async () => {
  loading.value = true
  try {
    const employeeId = localStorage.getItem('employeeId')
    if (!employeeId) {
      ElMessage.error('未找到员工ID，请重新登录')
      return
    }

    console.log('获取请假记录，员工ID:', employeeId)
    
    // 根据权限调用不同的接口
    const response = await axios.get(`/leaveRequest/queryOne/${employeeId}`)

    if (response.data.code === 200) {
      leaveRecords.value = response.data.data.map(record => {
        // 安全地处理日期
        let formattedLeaveTime = record.leaveTime
        try {
          const date = new Date(record.leaveTime)
          if (!isNaN(date.getTime())) {
            formattedLeaveTime = date.toLocaleString('zh-CN')
          }
        } catch (error) {
          console.error('日期格式化错误:', error)
        }
        return {
          ...record,
          leaveTime: formattedLeaveTime
        }
      })
      
      console.log('获取到的请假记录:', leaveRecords.value)
    } else {
      ElMessage.error(response.data.msg || '获取请假记录失败')
    }
  } catch (error) {
    console.error('获取请假记录错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      employeeId: localStorage.getItem('employeeId')
    })
    ElMessage.error('获取请假记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 修改处理审核按钮点击
const handleApproval = async (row) => {
  try {
    console.log('点击审核按钮，行数据:', row)  // 添加日志
    // 先获取最新的请假记录详情
    const response = await axios.get(`/leaveRequest/queryOne/${row.employeeId}`)
    
    if (response.data.code === 200) {
      // 找到对应的请假记录
      const leaveRecord = response.data.data.find(item => item.id === row.id)
      
      if (leaveRecord) {
        console.log('找到请假记录:', leaveRecord)  // 添加日志
        currentLeave.value = leaveRecord  // 直接保存整个记录，不做额外处理
        approvalDialogVisible.value = true
      } else {
        ElMessage.warning('未找到该请假记录')
      }
    } else {
      ElMessage.error(response.data.msg || '获取请假记录失败')
    }
  } catch (error) {
    console.error('获取请假记录详情错误：', error)
    ElMessage.error('获取请假记录详情失败，请稍后重试')
  }
}

// 修改提交审核结果
const submitApproval = async () => {
  console.log('当前请假记录:', currentLeave.value)  // 添加日志

  if (!currentLeave.value || !currentLeave.value.id) {
    console.error('请假记录或ID不存在:', currentLeave.value)  // 添加错误日志
    ElMessage.warning('请假记录ID不存在')
    return
  }

  try {
    approvalSubmitting.value = true
    
    const leaveId = currentLeave.value.id
    console.log('提交审核，请假ID:', leaveId)  // 添加日志

    const response = await axios({
      method: 'post',
      url: '/leaveRequest/update',
      params: {
        id: leaveId
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('审核完成')
      approvalDialogVisible.value = false
      // 刷新请假记录
      if (selectedDepartment.value) {
        await handleDepartmentChange(selectedDepartment.value)
      }
    } else {
      ElMessage.error(response.data.msg || '审核失败')
    }
  } catch (error) {
    console.error('审核提交错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      currentLeave: currentLeave.value  // 添加完整的当前请假记录到错误日志
    })
    ElMessage.error('审核失败，请稍后重试')
  } finally {
    approvalSubmitting.value = false
  }
}

onMounted(async () => {
  const storedPermissions = localStorage.getItem('permissions')
  if (storedPermissions !== null) {
    userPermission.value = Number(storedPermissions)
  }

  employeeId.value = localStorage.getItem('employeeId')
  
  // 根据权限执行不同的初始化逻辑
  if (isAdmin.value) {
    await fetchDepartmentList()
    if (userPermission.value === 1) {
      // 部门管理员不需要进一步操作，因为 fetchDepartmentList 已经加载了数据
      console.log('部门管理员初始化完成')
    } else {
      // 超级管理员需要清空选择
      selectedDepartment.value = null
      leaveRecords.value = []
    }
  } else {
    // 普通员工
    await fetchLeaveRecords()
  }
})
</script>

<style scoped>
.leave-container {
  padding: 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
  color: #303133;
}

.leave-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-date-picker) {
  width: 100%;
}

:deep(.el-select) {
  width: 240px;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.filter-header {
  display: flex;
  align-items: center;
  padding: 10px;
}

.section-title {
  font-weight: bold;
  color: #303133;
  margin-right: 20px;
}

.department-select {
  width: 240px;
}

:deep(.el-select) {
  margin-right: 20px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-table) {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-button .el-icon) {
  margin-right: 6px;
}

/* 添加表格列对齐样式 */
:deep(.el-table .cell) {
  padding: 0 12px;
}

/* 优化空状态样式 */
:deep(.el-empty) {
  padding: 40px 0;
}

/* 优化表格内容溢出样式 */
:deep(.el-table .el-tooltip__trigger) {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 优化表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: bold;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

.days-text {
  color: #409EFF;
  font-weight: bold;
}

.reason-text {
  color: #606266;
  line-height: 1.5;
}

:deep(.el-table .cell) {
  padding: 12px;
}

:deep(.el-tag) {
  font-weight: bold;
}

.apply-button {
  font-size: 14px;
  padding: 12px 20px;
  transition: all 0.3s;
}

.apply-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.3);
}

.status-column {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.approval-form {
  padding: 20px;
  min-height: 200px;  /* 添加最小高度 */
}

.info-item {
  margin-bottom: 12px;
  line-height: 1.5;
  display: flex;
  align-items: center;  /* 修改为 center 以便标签垂直居中 */
}

.info-item strong {
  color: #606266;
  margin-right: 12px;  /* 增加右边距 */
  min-width: 80px;
  text-align: right;
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

:deep(.el-tag) {
  font-weight: bold;
  margin-left: 4px;  /* 添加左边距 */
}
</style> 