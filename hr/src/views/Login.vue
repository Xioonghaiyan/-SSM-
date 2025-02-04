<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <img src="../assets/logo.png" alt="Logo" class="logo">
          <h2>员工管理系统</h2>
        </div>
        
        <el-form 
          :model="loginForm" 
          :rules="rules" 
          ref="loginFormRef" 
          class="login-form"
        >
          <!-- 员工ID -->
          <el-form-item prop="employeeId">
            <el-input 
              v-model.number="loginForm.employeeId" 
              placeholder="请输入员工ID"
              :prefix-icon="User"
              size="large"
            >
            </el-input>
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              show-password
              :prefix-icon="Lock"
              size="large"
            >
            </el-input>
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button 
              type="primary" 
              @click="submitForm" 
              :loading="loading"
              class="submit-btn"
              size="large"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>© 2024 员工管理系统. All rights reserved.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单数据
const loginForm = reactive({
  employeeId: '',
  password: ''
})

// 表单验证规则
const rules = {
  employeeId: [
    { required: true, message: '请输入员工ID', trigger: 'blur' },
    { type: 'number', message: '员工ID必须为数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 提交表单
const submitForm = async () => {
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log('发送登录请求:', {
          url: '/user/login',
          data: loginForm
        })
        
        const response = await axios({
          method: 'post',
          url: '/user/login',
          data: loginForm
        })
        
        console.log('登录响应:', response.data)

        if (response.data.code === 200) {
          const userData = response.data.data
          if (userData && userData.token) {
            // 保存完整的 token（包含 Bearer 前缀）
            const token = userData.token.startsWith('Bearer ') ? userData.token : `Bearer ${userData.token}`
            localStorage.setItem('token', token)
            localStorage.setItem('employeeId', loginForm.employeeId)
            localStorage.setItem('permissions', userData.permissions.toString())
            
            ElMessageBox.alert(
              '登录成功！',
              '成功',
              {
                type: 'success',
                confirmButtonText: '确定',
                callback: () => {
                  router.push('/layout')
                }
              }
            )
          } else {
            ElMessage.error('登录成功但未获取到token')
          }
        } else {
          ElMessage.error(response.data.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录错误：', error)
        console.error('错误详情：', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: error.response?.data,
          config: error.config
        })
        ElMessage.error(error.response?.data?.msg || '登录失败，请稍后重试')
      }
    }
  })
}
</script>

<style scoped>
.login-wrapper {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
}

.login-header h2 {
  color: #303133;
  font-size: 28px;
  margin: 0;
  font-weight: 600;
}

.login-form {
  margin-bottom: 30px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-input__inner) {
  height: 45px;
}

:deep(.el-input__prefix-inner) {
  font-size: 18px;
  color: #909399;
}

.submit-btn {
  width: 100%;
  height: 45px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: transform 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.submit-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
}

/* 添加响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 100%;
    padding: 30px 20px;
  }

  .login-header h2 {
    font-size: 24px;
  }

  .logo {
    width: 60px;
    height: 60px;
  }
}

/* 添加加载动画 */
:deep(.el-loading-spinner) {
  .circular {
    width: 30px;
    height: 30px;
  }
  .el-loading-text {
    color: #fff;
    margin-top: 8px;
  }
}
</style> 