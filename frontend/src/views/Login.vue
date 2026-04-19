<template>
    <div class="login-container">
        <el-card class="login-card">
            <template #header>
                <div class="card-header">
                    <h2>实验室管理系统</h2>
                </div>
            </template>
            <el-form :model="loginForm" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
                <el-form-item prop="username">
                    <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="text" @click="$router.push('/register')">没有账号？去注册</el-button>
                </el-form-item>
            </el-form>
        </el-card>
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
}

.login-card {
    width: 400px;
}

.card-header {
    text-align: center;
}

.card-header h2 {
    margin: 0;
    color: #303133;
}
</style>
