<template>
  <div class="department-container">
    <div v-if="loading">加载中...</div>
    <div v-else-if="departmentList.length === 0">暂无数据</div>
    
    <el-card class="department-card">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="输入部门名称搜索"
          clearable
          @clear="handleSearch"
          style="width: 300px;"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        
        <el-button 
          type="primary" 
          @click="handleAdd"
          :icon="Plus"
        >
          新增部门
        </el-button>
      </div>

      <!-- 部门列表表格 -->
      <el-table 
        :data="departmentList" 
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column 
          prop="departmentId" 
          label="部门ID" 
          width="120"
          align="center"
        />
        <el-table-column 
          prop="departmentName" 
          label="部门名称" 
          min-width="200"
          align="center"
        />
        <el-table-column 
          label="操作" 
          width="120"
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑部门对话框 -->
    <el-dialog
      title="新增部门"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="departmentFormRef"
        :model="departmentForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="departmentName">
          <el-input 
            v-model="departmentForm.departmentName"
            placeholder="请输入部门名称"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="submitting"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

// 添加调试日志
console.log('Department component loaded')

const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const departmentList = ref([])
const departmentFormRef = ref(null)

const departmentForm = ref({
  departmentId: null,
  departmentName: ''
})

const rules = {
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

// 获取部门列表
const fetchDepartmentList = async () => {
  loading.value = true
  try {
    console.log('开始获取部门列表...')
    const response = await axios.get('/department/query')
    
    if (response.data.code === 200) {
      departmentList.value = response.data.data
      console.log('获取到的部门列表:', departmentList.value)
    } else {
      ElMessage.error(response.data.msg || '获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表错误：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url
    })
    ElMessage.error('获取部门列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  fetchDepartmentList()
}

// 处理添加
const handleAdd = () => {
  departmentForm.value = {
    departmentId: null,
    departmentName: ''
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除该部门吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      console.log('删除部门:', {
        departmentId: row.departmentId
      })

      const response = await axios({
        method: 'delete',
        url: '/department/remove',
        params: {
          departmentId: row.departmentId
        }
      })

      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        fetchDepartmentList()  // 刷新列表
      } else {
        ElMessage.error(response.data.msg || '该部门有员工存在，不能删除')
      }
    } catch (error) {
      console.error('删除部门错误：', error)
      console.error('错误详情：', {
        departmentId: row.departmentId,
        url: error.config?.url,
        params: error.config?.params,
        status: error.response?.status,
        responseData: error.response?.data
      })
      ElMessage.error('该部门有员工存在，不能删除')
    }
  }).catch(() => {
    // 用户取消删除操作
    ElMessage.info('已取消删除')
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!departmentFormRef.value) return
  
  await departmentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const response = await axios({
          method: 'post',
          url: '/department/add',
          params: {
            departmentName: departmentForm.value.departmentName
          }
        })

        if (response.data.code === 200) {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          fetchDepartmentList()
        } else {
          ElMessage.error(response.data.msg || '添加失败')
        }
      } catch (error) {
        console.error('提交部门表单错误：', error)
        console.error('错误详情：', {
          url: error.config?.url,
          params: error.config?.params,
          status: error.response?.status,
          responseData: error.response?.data
        })
        ElMessage.error('添加失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 确保组件挂载时获取数据
onMounted(() => {
  console.log('Department component mounted')
  fetchDepartmentList()
})
</script>

<style scoped>
.department-container {
  padding: 20px;
  min-height: 100px;
}

.department-card {
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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
</style> 