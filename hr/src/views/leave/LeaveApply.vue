<template>
  <div class="leave-apply-container">
    <div class="header-section">
      <h2>请假申请</h2>
      <el-button @click="$router.back()" :icon="Back">返回</el-button>
    </div>

    <el-card class="apply-card">
      <el-form 
        :model="leaveForm" 
        :rules="rules" 
        ref="leaveFormRef" 
        label-width="100px"
        class="apply-form"
      >
        <el-form-item label="请假类型" prop="typeId">
          <el-select 
            v-model="leaveForm.typeId" 
            placeholder="请选择请假类型"
            class="form-input"
          >
            <el-option 
              v-for="type in leaveTypes" 
              :key="type.typeId" 
              :label="type.type"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="请假天数" prop="leaveDays">
          <el-input-number 
            v-model="leaveForm.leaveDays" 
            :min="1" 
            :max="30"
            class="form-input"
          />
        </el-form-item>

        <el-form-item label="请假时间" prop="leaveTime">
          <el-date-picker
            v-model="leaveForm.leaveTime"
            type="datetime"
            placeholder="选择请假时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disabledDate"
            :shortcuts="datePickerOptions.shortcuts"
            class="form-input"
          />
        </el-form-item>

        <el-form-item label="请假原因" prop="leaveReason">
          <el-input
            v-model="leaveForm.leaveReason"
            type="textarea"
            :rows="4"
            placeholder="请输入请假原因"
            class="form-input"
          />
        </el-form-item>

        <el-form-item>
          <div class="form-buttons">
            <el-button @click="$router.back()">取消</el-button>
            <el-button type="primary" @click="submitForm" :loading="loading">
              提交申请
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Back } from '@element-plus/icons-vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const leaveFormRef = ref(null)
const loading = ref(false)
const leaveTypes = ref([])

const leaveForm = reactive({
  employeeId: Number(localStorage.getItem('employeeId')),
  typeId: null,
  leaveDays: 1,
  leaveTime: '',
  leaveReason: ''
})

const rules = {
  employeeId: [
    { required: true, message: '员工ID不能为空', trigger: 'blur' },
    { type: 'number', message: '员工ID必须是数字', trigger: 'blur' }
  ],
  typeId: [
    { required: true, message: '请选择请假类型', trigger: 'change' },
    { type: 'number', message: '请假类型必须是数字', trigger: 'change' }
  ],
  leaveDays: [
    { required: true, message: '请输入请假天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 30, message: '请假天数在1-30天之间', trigger: 'blur' }
  ],
  leaveTime: [
    { required: true, message: '请选择请假时间', trigger: 'change' }
  ],
  leaveReason: [
    { required: true, message: '请输入请假原因', trigger: 'blur' },
    { min: 5, max: 200, message: '请假原因在5-200字之间', trigger: 'blur' }
  ]
}

// 禁用今天之前的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 获取请假类型列表
const fetchLeaveTypes = async () => {
  try {
    const response = await axios.get('/leavesType/query')
    
    console.log('获取请假类型响应:', response.data)
    
    if (response.data.code === 200) {
      leaveTypes.value = response.data.data
      console.log('请假类型列表:', leaveTypes.value)
    } else {
      ElMessage.error(response.data.msg || '获取请假类型失败')
    }
  } catch (error) {
    console.error('获取请假类型失败：', error)
    console.error('错误详情：', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url
    })
    ElMessage.error('获取请假类型失败，请稍后重试')
  }
}

// 提交表单
const submitForm = async () => {
  if (!leaveFormRef.value) return

  await leaveFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const submitData = {
          employeeId: leaveForm.employeeId,
          leaveTime: leaveForm.leaveTime,
          leaveDays: Number(leaveForm.leaveDays),
          typeId: Number(leaveForm.typeId),
          leaveReason: leaveForm.leaveReason
        }

        console.log('提交请假申请数据:', submitData)

        const response = await axios({
          method: 'post',
          url: '/leaveRequest/add',
          data: submitData,
          headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })

        if (response.data.code === 200) {
          ElMessage.success('请假申请提交成功')
          router.back()
        } else {
          ElMessage.error(response.data.msg || '请假申请提交失败')
        }
      } catch (error) {
        console.error('提交请假申请错误：', error)
        console.error('错误详情：', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: error.response?.data,
          url: error.config?.url,
          submitData: leaveForm,
          token: localStorage.getItem('token')
        })
        
        if (error.response?.status === 403) {
          ElMessage.error('没有权限，请重新登录')
          router.push('/login')
        } else if (error.response?.status === 400) {
          ElMessage.error(error.response.data.msg || '请求参数错误，请检查输入')
        } else {
          ElMessage.error('请假申请提交失败，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

// 修改日期选择器配置
const datePickerOptions = {
  shortcuts: [
    {
      text: '今天',
      value: new Date()
    },
    {
      text: '明天',
      value: (() => {
        const date = new Date()
        date.setTime(date.getTime() + 3600 * 1000 * 24)
        return date
      })()
    }
  ]
}

// 组件初始化时获取请假类型
onMounted(async () => {
  await fetchLeaveTypes()
})
</script>

<style scoped>
.leave-apply-container {
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

.apply-card {
  max-width: 800px;
  margin: 0 auto;
}

.apply-form {
  padding: 20px;
}

.form-input {
  width: 100%;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-select-dropdown__item) {
  padding: 0 20px;
}

:deep(.el-select-dropdown__item.selected) {
  color: #409EFF;
  font-weight: bold;
}
</style> 