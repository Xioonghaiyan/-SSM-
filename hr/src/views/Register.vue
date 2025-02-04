<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
      <el-form 
        :model="registerForm" 
        :rules="rules" 
        ref="registerFormRef" 
        label-width="80px"
      >
        <!-- 员工ID -->
        <el-form-item label="员工ID" prop="employeeId">
          <el-input 
            v-model="registerForm.employeeId"
            placeholder="请输入员工ID"
          ></el-input>
        </el-form-item>

        <!-- 姓名 -->
        <el-form-item label="姓名" prop="name">
          <el-input 
            v-model="registerForm.name" 
            placeholder="请输入姓名"
          ></el-input>
        </el-form-item>

        <!-- 部门 -->
        <el-form-item label="部门" prop="departmentId">
          <el-select 
            v-model="registerForm.departmentId" 
            placeholder="请选择部门"
            @change="handleDepartmentChange"
          >
            <el-option 
              v-for="item in departments" 
              :key="item.departmentId" 
              :label="item.departmentName" 
              :value="item.departmentId"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 岗位 -->
        <el-form-item label="岗位" prop="positionId">
          <el-select 
            v-model="registerForm.positionId" 
            placeholder="请选择岗位"
            :disabled="!registerForm.departmentId"
          >
            <el-option 
              v-for="item in positions" 
              :key="item.positionId"
              :label="item.positionName"
              :value="item.positionId"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const registerFormRef = ref()

// 表单数据
const registerForm = reactive({
  employeeId: '',
  name: '',
  departmentId: null,
  positionId: null
})

// 部门和岗位数据
const departments = ref([])
const positions = ref([])

// 表单验证规则
const rules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' },
    { pattern: /^\d+$/, message: '员工ID必须为数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  positionId: [
    { required: true, message: '请选择岗位', trigger: 'change' }
  ]
}

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const response = await axios({
      method: 'get',
      url: '/api/department/query',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    if (response.data.code === 200) {
      departments.value = response.data.data
      console.log('部门列表:', departments.value)
    } else {
      ElMessage.error(response.data.msg || '获取部门列表失败')
    }
  } catch (error) {
    console.error('获取部门列表错误:', error)
    ElMessage.error('获取部门列表失败')
  }
}

// 获取岗位列表
const fetchPositions = async (departmentId) => {
  try {
    console.log('正在获取部门ID为', departmentId, '的岗位列表')
    const response = await axios({
      method: 'get',
      url: `/api/position/query/${departmentId}`,
      headers: {
        'Content-Type': 'application/json'
      }
    })
    if (response.data.code === 200) {
      positions.value = response.data.data
      console.log('岗位列表:', positions.value)
    } else {
      ElMessage.error(response.data.msg || '获取岗位列表失败')
    }
  } catch (error) {
    console.error('获取岗位列表错误:', error)
    ElMessage.error('获取岗位列表失败')
  }
}

// 部门选择变化时获取对应的岗位列表
const handleDepartmentChange = (value) => {
  console.log('选择了新的部门:', value)
  registerForm.positionId = null  // 清空已选择的岗位
  positions.value = []           // 清空岗位列表
  if (value) {
    fetchPositions(value)       // 获取新的岗位列表
  }
}

// 提交表单
const submitForm = async () => {
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await axios({
          method: 'post',
          url: '/api/user/register',
          data: registerForm,
          headers: {
            'Content-Type': 'application/json'
          }
        })

        if (response.data.code === 200) {
          ElMessageBox.confirm(
            '注册成功！是否立即前往登录？',
            '注册成功',
            {
              confirmButtonText: '前往登录',
              cancelButtonText: '留在当前页',
              type: 'success',
            }
          )
          .then(() => {
            router.push('/login')
          })
          .catch(() => {
            resetForm()
          })
        } else {
          ElMessage.error(response.data.msg || '注册失败')
        }
      } catch (error) {
        console.error('请求错误：', error)
        console.error('错误详情:', error.response?.data)
        ElMessage.error(error.response?.data?.msg || '注册失败，请稍后重试')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  registerFormRef.value.resetFields()
}

// 页面加载时获取部门列表
onMounted(() => {
  fetchDepartments()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px;
}

.register-card {
  width: 400px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.el-form-item:last-child {
  margin-bottom: 0;
  text-align: center;
}

.el-form-item:last-child .el-button {
  margin: 0 10px;
}
</style> 