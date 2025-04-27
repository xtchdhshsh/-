<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utlis/axios'

const router = useRouter()
const loading = ref(false)

const form = ref({
    username: '',
    password: '',
    confirmPassword: '',
    userType: 'student'
})

// 密码强度检查
const passwordStrength = ref(0)
const checkPasswordStrength = () => {
    const hasLetter = /[a-zA-Z]/.test(form.value.password)
    const hasNumber = /\d/.test(form.value.password)
    const hasSpecial = /[^a-zA-Z0-9]/.test(form.value.password)
    passwordStrength.value = [hasLetter, hasNumber, hasSpecial].filter(Boolean).length
}

// 验证密码一致性
const validatePassword = () => {
    return form.value.password === form.value.confirmPassword
}
const handleRegister = async () => {
    if (!form.value.username || !form.value.password || !form.value.confirmPassword) {
        return ElMessage.warning('请填写完整信息')
    }
    if (form.value.password.length < 8 || form.value.password.length > 20) {
        return ElMessage.warning('密码长度需在 8 - 20位之间')
    }
    if (!validatePassword()) {
        return ElMessage.warning('两次密码输入不一致')
    }

    try {
        loading.value = true

        const endpoint = form.value.userType === 'student' ? '/Register' : '/TeacherRegister'
        const res = await axios.post(endpoint, new URLSearchParams({
            username: form.value.username,
            password: form.value.password
        }), {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        })
        
        if (res.data === "注册成功") {
            ElMessage.success('注册成功')
            router.push('/login?from=register')
        } else {
            // console.log(res.data)
            ElMessage.error(res.data || '注册失败')
        }
    } catch (err) {
        // HACK: 数据库还没有验证相同用户名重复注册
        if (err.response?.data?.includes('已存在')) {
            ElMessage.error('用户名已存在')
        } else {
            ElMessage.error(err.message || '注册失败')
        }
    } finally {
        loading.value = false
    }
}
</script>

<template>
    <div class="container">
        <h2>账号注册</h2>
        
        <el-form 
            class="form"
            @submit.prevent="handleRegister"
        >
            <!-- 用户名输入 -->
            <div class="form-group">
                <label>用户名</label>
                <el-input
                    v-model="form.username"
                    type="text"
                    placeholder="3 - 20 位字符"
                    :maxlength="20"
                    style="font-size: 1rem;"
                >
                    <template #prefix>
                        <span>👤</span>
                    </template>
                </el-input>
            </div>

            <!-- 密码输入 -->
            <div class="form-group">
                <label>密码</label>
                <el-input
                    v-model="form.password"
                    type="password"
                    placeholder="8 - 20 位字母数字组合"
                    show-password
                    @input="checkPasswordStrength"
                    :maxlength="20"
                    style="font-size: 1rem;"
                >
                    <template #prefix>
                        <span>🔒</span>
                    </template>
                </el-input>
                <div v-if="form.password" class="password-strength">
                    <!-- 显示密码强度样式 -->
                    <div :class="['strength-bar', { active: passwordStrength >= 1 }]"></div>
                    <div :class="['strength-bar', { active: passwordStrength >= 2 }]"></div>
                    <div :class="['strength-bar', { active: passwordStrength >= 3 }]"></div>
                    <!-- FIXME: 字体样式不生效 -->
                    <span class="strength-text">
                        {{
                            passwordStrength === 1 ? '弱' :
                            passwordStrength === 2 ? '中' :
                            passwordStrength === 3 ? '强' : ''
                        }}
                    </span>
                </div>
            </div>

            <!-- 确认密码 -->
            <div class="form-group">
                <label>确认密码</label>
                <el-input
                    v-model="form.confirmPassword"
                    type="password"
                    placeholder="再次输入密码"
                    show-password
                    :class="{ 'is-error': form.confirmPassword && !validatePassword() }"
                    style="font-size: 1rem;"
                >
                    <template #prefix>
                        <span>🔒</span>
                    </template>
                </el-input>
                <span 
                    v-if="form.confirmPassword && !validatePassword()"
                    class="error-msg"
                >
                    两次密码输入不一致
                </span>
            </div>

            <div class="form-group">
                <label>用户类型</label>
                <el-radio-group v-model="form.userType">
                    <el-radio :value="'student'">学生</el-radio>
                    <el-radio :value="'teacher'">教师</el-radio>
                </el-radio-group>
            </div>

            <button 
                type="submit" 
                class="submit-btn"
                :disabled="loading"
            >
                {{ loading ? '注册中...' : '注册' }}
            </button>
        </el-form>

        <p class="toggle">
            已有账号？<RouterLink to="/login">立即登录</RouterLink>
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
    gap: clamp(1rem, 2vw, 1.2rem);
}
.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
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
.form-group :deep(.el-input__wrapper.is-focus),
.form-group :deep(.el-input__wrapper:hover) {
    border-color: hsl(200, 80%, 50%);
}
.form-group :deep(.el-input__wrapper.is-error) {
    border-color: hsl(0, 70%, 50%);
}

/* 密码 */
.password-strength {
    margin-top: 4px;
    display: flex;
    align-items: center;
    gap: 8px;
}
.strength-bar {
    height: 4px;
    background: hsl(0deg 0% 85%);
    border-radius: 2px;
    flex: 1;
    transition: all 0.3s;
}
.strength-bar.active {
    background: hsl(var(--strength-color));
}
.strength-bar:nth-child(1).active {
    --strength-color: 0, 70%, 50%;
}
.strength-bar:nth-child(2).active {
    --strength-color: 30, 80%, 50%;
}
.strength-bar:nth-child(3).active {
    --strength-color: 120, 60%, 50%;
}
.strength-text {
    width: 20px;
    font-size: 1rem;
    color: hsl(var(--strength-color));
}
.error-msg {
    padding: 0.4rem 0.6rem;
    background-color: hsl(0, 85%, 95%);
    border-left: 3px solid hsl(0, 64%, 48%);
    border-radius: 0 6px 6px 0;
    font-size: 0.9rem;
    color: hsl(0, 70%, 50%);
    animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(-5px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 提交注册 */
.submit-btn {
    width: 100%;
    margin-top: clamp(1rem, 1vw, 1.5rem);
    padding: clamp(0.7rem, 2vmin, 0.9rem);
    background-color: hsl(200deg 40% 30%);
    border: none;
    border-radius: clamp(4px, 1vw, 6px);
    font-size: inherit;
    font-weight: 500;
    letter-spacing: 0.1rem;
    color: white;
    cursor: pointer;
    transition: background-color 0.2s;
}
.submit-btn:hover {
    background-color: hsl(200deg 45% 20%);
}
.submit-btn:disabled {
    background-color: hsl(200deg 30% 40%);
    cursor: not-allowed;
}

/* 切换登录 */
.toggle {
    margin-top: clamp(1rem, 3vmin, 1.5rem);
    text-align: center;
    font-size: clamp(0.8rem, 2vmin, 1rem);
    color: hsl(0deg 0% 40%);
}
.toggle a {
    text-decoration: none;
    font-weight: 500;
    color: hsl(200deg 60% 40%);
}
</style>