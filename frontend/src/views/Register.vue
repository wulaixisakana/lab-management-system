<template>
    <div class="register-container">
        <el-card class="register-card">
            <template #header>
                <div class="card-header">
                    <h2>用户注册</h2>
                </div>
            </template>
            <el-form :model="registerForm" :rules="rules" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password />
                </el-form-item>
                <el-form-item prop="realName">
                    <el-input v-model="registerForm.realName" placeholder="真实姓名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input v-model="registerForm.phone" placeholder="手机号" prefix-icon="Phone" />
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="Message" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button type="text" @click="$router.push('/login')">已有账号？去登录</el-button>
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

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const registerForm = ref({
    username: '',
    password: '',
    confirmPassword: '',
    realName: '',
    phone: '',
    email: ''
})

const validatePass = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请输入密码'))
    } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
    } else {
        callback()
    }
}

const validateConfirmPass = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.value.password) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
    ],
    password: [{ validator: validatePass, trigger: 'blur' }],
    confirmPassword: [{ validator: validateConfirmPass, trigger: 'blur' }],
    realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ]
}

const handleRegister = async () => {
    await formRef.value.validate()
    loading.value = true
    try {
        await authApi.register(registerForm.value)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
    width: 450px;
}

.card-header {
    text-align: center;
}

.card-header h2 {
    margin: 0;
    color: #303133;
}
</style>
