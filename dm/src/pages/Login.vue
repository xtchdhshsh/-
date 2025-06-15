<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utlis/axios'

const router = useRouter()
const emit = defineEmits()
const loginForm = ref(null)
const loading = ref(false)

const form = ref({
  username: localStorage.getItem('savedUsername') || '',
  // password: localStorage.getItem('savedPassword') || '',
  remember: localStorage.getItem('rememberPassword') !== null 
            ? localStorage.getItem('rememberPassword') === 'true' 
            : true,
  userType: localStorage.getItem('savedUserType') || 'student'
})

const handleLogin = async () => {
  try {
    // HACK: 数据库还没做登录表单验证
    await loginForm.value.validate()

    loading.value = true

    const endpoint = form.value.userType === 'student' ? '/login' : '/TeacherLogin'
    const res = await axios.post(endpoint, new URLSearchParams({
      username: form.value.username,
      password: form.value.password
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })

    if (res.data && res.data !== 'login fail') {
      const token = res.data
      localStorage.setItem('token', token)

      // 登陆成功才保存数据
      if (form.value.remember) {
        localStorage.setItem('savedUsername', form.value.username)
        // localStorage.setItem('savedPassword', form.value.password)
        localStorage.setItem('rememberPassword', 'true')
        localStorage.setItem('savedUserType', form.value.userType)
      } else {
        localStorage.removeItem('savedUsername')
        // localStorage.removeItem('savedPassword')
        localStorage.removeItem('rememberPassword')
        localStorage.removeItem('savedUserType')
      }

      emit('login-success', form.value.username, form.value.userType) // 传到 APP.vue
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('登陆失败')
    }
  } catch (error) {
    // HACK: 数据库还没做验证
    if (error.response?.status === 401) {
      ElMessage.error('用户名或密码错误')
    } else {
      // ElMessage.error(error.message || '登录失败')
      ElMessage.error(error.message)
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (form.value.remember) {
    // form.value.password = localStorage.getItem('savedPassword') || ''
    form.value.userType = localStorage.getItem('savedUserType') || 'student'
  }
})
</script>

<template>
  <div class="container">
    <h1 style="line-height: 100%;">欢迎登陆</h1>
    
    <el-form
      ref="loginForm"
      :model="form"
      @submit.prevent="handleLogin"
      class="form"
    >
      <div class="form-group">
        <label>账号 :</label>
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            type="text"
            placeholder="请输入账号"
            style="font-size: 1rem;"
          >
            <template #prefix>
              <span>👤</span>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <div class="form-group">
        <label>密码 :</label>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
            style="font-size: 1rem;"
          >
            <template #prefix>
              <span>🔒</span>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <div class="form-group user-type-row">
      <label>用户类型 :</label>
      <div class="user-type-options">
        <el-radio-group v-model="form.userType">
          <el-radio :value="'student'">学生</el-radio>
          <el-radio :value="'teacher'">教师</el-radio>
        </el-radio-group>
        <el-checkbox v-model="form.remember">记住密码</el-checkbox>
      </div>
    </div>


      <button 
        type="submit" 
        class="submit-btn"
        :disabled="loading"
      >
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </el-form>

    <p class="toggle">
      没有账号？<RouterLink to="/register">立即注册</RouterLink>
    </p>
  </div>
</template>

<style scoped>
/* 窗口 */
.container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: min(90vw, 680px);
  height: auto;
  padding: clamp(1rem, 4vw, 2rem);
  background: white;
  border: 1px solid black;
  border-radius: clamp(8px, 1.5vw, 12px);
  font-size: clamp(1rem, 2.5vw, 1.2rem);
  overflow: auto;
}

/* 表单 */
.form {
  margin: clamp(1rem, 2vw, 2rem) 0;
  display: flex;
  flex-direction: column;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.user-type-row .user-type-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-group label {
  font-weight: 700;
  color: hsl(0deg 0% 30%);
}
.form-group :deep(.el-input__wrapper) {
  width: 100%;
  padding: clamp(0.5rem, 1.5vw, 0.8rem);
  background: transparent;
  border: 1px solid hsl(0deg 0% 85%);
  border-radius: clamp(4px, 1vw, 6px);
  box-shadow: none !important;
  font-size: inherit;
  transition: border-color 0.2s;
}
.form-group :deep(.el-input__wrapper.is-focus) {
  border-color: hsla(210, 80%, 48%, 0.3);
}

/* 提交登录 */
.submit-btn {
  width: 100%;
  margin-top: clamp(1rem, 3vw, 1.5rem);
  padding: clamp(0.7rem, 2vmin, 0.9rem);
  background-color: hsl(200deg 40% 30%);
  border: none;
  border-radius: clamp(4px, 1vw, 6px);
  cursor: pointer;
  font-size: inherit;
  font-weight: 500;
  color: white;
  letter-spacing: 0.05em;
  transition: background-color 0.2s;
}
.submit-btn:hover {
  background-color: hsl(210deg 45% 25%);
}
.submit-btn:disabled {
  background-color: hsl(210deg 30% 40%);
  cursor: not-allowed;
}

/* 切换登录 */
.toggle {
  text-align: center;
  margin-top: clamp(1rem, 3vmin, 1.5rem);
  font-size: clamp(0.8rem, 2vmin, 1rem);
  color: hsl(0deg 0% 40%);
}
.toggle a {
  color: hsl(210deg 60% 40%);
  text-decoration: none;
  font-weight: 500;
}
</style>
