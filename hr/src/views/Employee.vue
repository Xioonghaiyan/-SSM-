<template>
  <div class="employee-container">
    <el-card class="employee-card">
      <div class="search-bar">
        <div class="search-inputs">
          <el-input
            v-model="searchForm.employeeId"
            placeholder="输入员工ID搜索"
            clearable
            @clear="handleSearch"
            style="width: 200px; margin-right: 10px;"
          >
          </el-input>
          
          <el-input
            v-model="searchForm.departmentId"
            placeholder="输入部门ID搜索"
            clearable
            @clear="handleSearch"
            style="width: 200px; margin-right: 10px;"
          >
          </el-input>

          <el-button 
            type="primary" 
            :icon="Search"
            @click="handleSearch"
            :loading="searching"
          >
            查询
          </el-button>
        </div>

        <el-button 
          v-if="canManageEmployee"
          type="primary" 
          @click="handleAdd"
          :icon="Plus"
        >
          新增员工
        </el-button>
      </div>

      <!-- 员工列表表格 -->
      <el-table 
        :data="employeeList" 
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
          prop="name" 
          label="姓名" 
          width="120"
          align="center"
        />
        <el-table-column 
          prop="age" 
          label="年龄" 
          width="80"
          align="center"
        />
        <el-table-column 
          prop="departmentName"
          label="部门" 
          width="120"
          align="center"
        />
        <el-table-column 
          prop="positionName"
          label="岗位" 
          width="120"
          align="center"
        />
        <el-table-column 
          prop="phone" 
          label="手机号" 
          width="150"
          align="center"
        />
        <el-table-column 
          prop="email" 
          label="邮箱"
          min-width="200"
          align="center"
        />
        <el-table-column 
          label="权限" 
          width="100"
          align="center"
        >
          <template #default="scope">
            <el-tag :type="getPermissionTagType(scope.row.permissions)">
              {{ getPermissionText(scope.row.permissions) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column 
          label="操作" 
          width="200"
          align="center"
          fixed="right"
          v-if="canManageEmployee"
        >
          <template #default="scope">
            <el-button
              v-if="canEdit(scope.row)"
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="canDelete(scope.row)"
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 添加员工对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增员工' : '修改员工'"
        width="500px"
        v-loading="dialogLoading"
        :close-on-click-modal="false"
        @closed="handleDialogClose"
      >
        <el-form
          ref="employeeFormRef"
          :model="employeeForm"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item 
            v-if="dialogType === 'add'"
            label="员工ID" 
            prop="employeeId"
          >
            <el-input v-model.number="employeeForm.employeeId" />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="employeeForm.name" />
          </el-form-item>
          <el-form-item label="部门" prop="departmentId">
            <el-select 
              v-model="employeeForm.departmentId"
              placeholder="请选择部门"
              @change="handleDepartmentChange"
              style="width: 100%"
            >
              <el-option
                v-for="dept in departmentList"
                :key="dept.departmentId"
                :label="dept.departmentName"
                :value="dept.departmentId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="岗位" prop="positionId">
            <el-select 
              v-model="employeeForm.positionId"
              placeholder="请先选择部门"
              :disabled="!employeeForm.departmentId"
              style="width: 100%"
            >
              <el-option
                v-for="pos in positionList"
                :key="pos.positionId"
                :label="pos.positionName"
                :value="pos.positionId"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false" :disabled="dialogLoading">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="dialogLoading">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import axios from 'axios'
import { getDepartmentName, getPositionName, fetchDepartments, fetchPositions } from '../utils/nameMapping'

const loading = ref(false)
const employeeList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 声明 dialogLoading
const dialogLoading = ref(false)

// 获取员工列表
const fetchEmployeeList = async () => {
  loading.value = true
  try {
    console.log('开始请求员工列表...')
    const response = await axios({
      method: 'get',
      url: '/user/query',
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })
    console.log('员工列表响应:', response.data)

    if (response.data.code === 200) {
      employeeList.value = response.data.data
      total.value = response.data.data.length
      
      if (employeeList.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
        await fetchEmployeeList()
      }
    } else {
      ElMessage.error(response.data.msg || '获取员工列表失败')
    }
  } catch (error) {
    console.error('获取员工列表错误：', error)
    ElMessage.error('获取员工列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理每页显示数量变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1  // 重置到第一页
  fetchEmployeeList()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchEmployeeList()
}

// 获取权限标签类型
const getPermissionTagType = (permission) => {
  const types = {
    0: 'danger',   // 管理员
    1: 'warning',  // 主管
    2: 'info',     // 普通员工
    3: ''          // 其他
  }
  return types[permission] || ''
}

// 获取权限文本
const getPermissionText = (permission) => {
  const texts = {
    0: '管理员',
    1: '主管',
    2: '员工',
    3: '其他'
  }
  return texts[permission] || '未知'
}

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const employeeFormRef = ref()
const employeeForm = ref({
  employeeId: '',
  name: '',
  departmentId: '',
  positionId: ''
})

// 表单验证规则
const rules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' },
    { type: 'number', message: '员工ID必须为数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请输入部门ID', trigger: 'blur' },
    { type: 'number', message: '部门ID必须为数字', trigger: 'blur' }
  ],
  positionId: [
    { required: true, message: '请输入岗位ID', trigger: 'blur' },
    { type: 'number', message: '岗位ID必须为数字', trigger: 'blur' }
  ]
}

// 添加部门和岗位列表
const departmentList = ref([])
const positionList = ref([])

// 获取岗位列表并缓存
const fetchPositionList = async (departmentId) => {
  if (!departmentId) {
    positionList.value = []
    return
  }
  
  try {
    const positions = await fetchPositions(departmentId)
    positionList.value = positions
  } catch (error) {
    console.error('获取岗位列表错误：', error)
    ElMessage.error('获取岗位列表失败，请稍后重试')
  }
}

// 处理部门选择变化
const handleDepartmentChange = (departmentId) => {
  employeeForm.value.positionId = ''  // 清空岗位选择
  if (departmentId) {
    fetchPositionList(departmentId)
  } else {
    positionList.value = []  // 如果没有选择部门，清空岗位列表
  }
}

// 修改 handleAdd
const handleAdd = async () => {
  dialogType.value = 'add'
  employeeForm.value = {
    employeeId: '',
    name: '',
    departmentId: '',
    positionId: ''
  }
  // 获取部门列表
  const departments = await fetchDepartments()
  departmentList.value = departments
  dialogVisible.value = true
}

// 修改 handleEdit
const handleEdit = async (row) => {
  // 检查权限
  if (!canEdit(row)) {
    ElMessage.warning('您没有权限编辑管理员账号')
    return
  }

  dialogType.value = 'edit'
  dialogVisible.value = true
  dialogLoading.value = true

  try {
    // 获取部门列表
    const departments = await fetchDepartments()
    departmentList.value = departments
    
    const response = await axios.get(`/user/queryId/${row.employeeId}`)
    if (response.data.code === 200) {
      const employeeData = response.data.data
      employeeForm.value = {
        ...employeeData,
        employeeId: row.employeeId
      }
      // 获取岗位列表
      await fetchPositionList(employeeData.departmentId)
    } else {
      ElMessage.error(response.data.msg || '获取员工信息失败')
      dialogVisible.value = false
    }
  } catch (error) {
    console.error('获取员工信息错误：', error)
    ElMessage.error('获取员工信息失败，请稍后重试')
    dialogVisible.value = false
  } finally {
    dialogLoading.value = false
  }
}

// 处理删除
const handleDelete = (row) => {
  // 检查权限
  if (!canDelete(row)) {
    ElMessage.warning('您没有权限删除管理员账号')
    return
  }

  ElMessageBox.confirm(
    '确定要删除该员工吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await axios.delete(`/employee/delete/${row.employeeId}`)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        fetchEmployees()  // 刷新列表
      } else {
        ElMessage.error(response.data.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除员工错误：', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  })
}

// 处理表单提交
const handleSubmit = async () => {
  if (dialogLoading.value) return

  await employeeFormRef.value.validate(async (valid) => {
    if (valid) {
      dialogLoading.value = true
      try {
        const url = dialogType.value === 'add' ? '/user/register' : '/user/update'
        const response = await axios({
          method: 'post',
          url,
          data: dialogType.value === 'add' 
            ? {
                // RegisterDto 的必要字段
                employeeId: employeeForm.value.employeeId,
                name: employeeForm.value.name,
                positionId: employeeForm.value.positionId,
                departmentId: employeeForm.value.departmentId
              }
            : employeeForm.value,  // UpdateDto 的字段保持不变
          headers: {
            'Content-Type': 'application/json'
          }
        })

        console.log('提交响应:', {
          type: dialogType.value,
          url,
          data: response.data
        })

        if (response.data.code === 200) {
          ElMessage.success(dialogType.value === 'add' 
            ? '添加成功，初始密码为：123456' 
            : '修改成功'
          )
          dialogVisible.value = false
          // 重置表单
          employeeForm.value = {
            employeeId: '',
            name: '',
            departmentId: '',
            positionId: ''
          }
          // 刷新列表
          fetchEmployeeList()
        } else {
          ElMessage.error(response.data.msg || (dialogType.value === 'add' ? '添加失败' : '修改失败'))
        }
      } catch (error) {
        console.error('提交表单错误：', error)
        console.error('错误详情：', {
          type: dialogType.value,
          url: error.config?.url,
          data: error.config?.data,
          status: error.response?.status,
          statusText: error.response?.statusText,
          responseData: error.response?.data
        })
        ElMessage.error(dialogType.value === 'add' ? '添加失败，请稍后重试' : '修改失败，请稍后重试')
      } finally {
        dialogLoading.value = false
      }
    }
  })
}

// 监听对话框关闭
const handleDialogClose = () => {
  dialogLoading.value = false
  employeeFormRef.value?.resetFields()
  // 重置表单数据
  employeeForm.value = {
    employeeId: '',
    name: '',
    departmentId: '',
    positionId: ''
  }
}

// 搜索相关数据
const searchForm = ref({
  employeeId: '',
  departmentId: ''
})
const searching = ref(false)

// 修改搜索处理函数
const handleSearch = async () => {
  // 如果两个输入框都为空，显示提示
  if (!searchForm.value.employeeId && !searchForm.value.departmentId) {
    ElMessage.warning('请输入员工ID或部门ID')
    return
  }

  searching.value = true
  try {
    // 构建查询参数
    const params = {}
    if (searchForm.value.employeeId) {
      params.employeeId = parseInt(searchForm.value.employeeId)
    }
    if (searchForm.value.departmentId) {
      params.departmentId = parseInt(searchForm.value.departmentId)
    }

    const response = await axios.get('/user/search', { params })
    
    if (response.data.code === 200) {
      employeeList.value = response.data.data
      if (employeeList.value.length === 0) {
        ElMessage.info('未找到匹配的员工')
      }
    } else {
      ElMessage.error(response.data.msg || '查询失败')
    }
  } catch (error) {
    console.error('搜索员工错误：', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    searching.value = false
  }
}

const userPermission = ref(Number(localStorage.getItem('permissions')))

// 判断是否可以编辑员工
const canEdit = (row) => {
  // 如果是超级管理员，可以编辑所有人
  if (userPermission.value === 0) {
    return true
  }
  // 如果不是超级管理员，不能编辑管理员账号
  if (row.permissions === 0) {
    return false
  }
  // 其他情况可以编辑
  return true
}

// 判断是否可以删除员工
const canDelete = (row) => {
  // 如果是超级管理员，可以删除所有人（除了自己）
  if (userPermission.value === 0) {
    return row.employeeId !== localStorage.getItem('employeeId')
  }
  // 如果不是超级管理员，不能删除管理员账号
  if (row.permissions === 0) {
    return false
  }
  // 其他情况可以删除
  return true
}

// 修改权限判断逻辑
const canManageEmployee = computed(() => {
  const userPermission = Number(localStorage.getItem('permissions'))
  const userDepartmentId = Number(localStorage.getItem('departmentId'))
  
  // 超级管理员可以管理所有员工
  if (userPermission === 0) {
    return true
  }
  
  // 部门管理员只有在部门ID为1时才能管理员工
  if (userPermission === 1) {
    return userDepartmentId === 1
  }
  
  return false
})

onMounted(async () => {
  // 先获取部门列表
  const departments = await fetchDepartments()
  departmentList.value = departments
  // 再获取员工列表
  await fetchEmployeeList()
  // 获取每个部门的岗位列表
  const departmentIds = new Set(employeeList.value.map(emp => emp.departmentId))
  for (const deptId of departmentIds) {
    await fetchPositionList(deptId)
  }
})
</script>

<style scoped>
.employee-container {
  padding: 20px;
}

.employee-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
}

:deep(.el-table--border) {
  border-radius: 4px;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}

:deep(.el-button) {
  margin-left: 10px;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-inputs {
  display: flex;
  align-items: center;
}

:deep(.el-input) {
  margin-right: 10px;
}

:deep(.el-input__wrapper) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 4px;
}

/* 添加禁用状态的样式 */
.el-button.is-disabled {
  cursor: not-allowed;
  opacity: 0.7;
}
</style> 