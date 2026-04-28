<template>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <el-icon class="login-logo"><Microscope /></el-icon>
                <h2>实验室管理系统</h2>
                <p>欢迎回来，请登录您的账号</p>
            </div>
            <el-form :model="loginForm" :rules="rules" ref="formRef" @submit.prevent="handleLogin" size="large">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%; height: 42px; font-size: 15px;">登 录</el-button>
                </el-form-item>
                <div class="login-footer">
                    <span>还没有账号？</span>
                    <el-button type="primary" link @click="$router.push('/register')">立即注册</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const loginForm = ref({
    username: '',
    password: ''
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
    await formRef.value.validate()
    loading.value = true
    try {
        const res = await authApi.login(loginForm.value)
        userStore.setToken(res.token)
        userStore.setUser(res.user)
        ElMessage.success('登录成功')
        router.push('/')
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;
}

.login-container::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    background: rgba(255,255,255,0.08);
    border-radius: 50%;
    top: -200px;
    right: -100px;
}

.login-container::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: rgba(255,255,255,0.05);
    border-radius: 50%;
    bottom: -150px;
    left: -100px;
}

.login-card {
    width: 420px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    z-index: 1;
}

.login-header {
    text-align: center;
    margin-bottom: 30px;
}

.login-logo {
    font-size: 48px;
    color: #667eea;
    margin-bottom: 12px;
}

.login-header h2 {
    margin: 0 0 8px 0;
    color: #303133;
    font-size: 24px;
}

.login-header p {
    margin: 0;
    color: #909399;
    font-size: 14px;
}

.login-footer {
    text-align: center;
    color: #909399;
    font-size: 14px;
}

@media (max-width: 768px) {
    .login-card {
        width: 90%;
        padding: 24px;
    }

    .login-logo {
        font-size: 36px;
    }

    .login-header h2 {
        font-size: 20px;
    }
}
</style>
