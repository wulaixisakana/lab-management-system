<template>
    <div class="register-container">
        <div class="register-card">
            <div class="register-header">
                <el-icon class="register-logo"><Microscope /></el-icon>
                <h2>创建账号</h2>
                <p>注册新账号以使用实验室管理系统</p>
            </div>
            <el-form :model="registerForm" :rules="rules" ref="formRef" size="large">
                <el-form-item prop="username">
                    <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="registerForm.password" type="password" placeholder="请输入密码（至少6位）" prefix-icon="Lock" show-password />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                    <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" show-password />
                </el-form-item>
                <el-form-item prop="realName">
                    <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="User" />
                </el-form-item>
                <el-form-item prop="phone">
                    <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="Phone" />
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="registerForm.email" placeholder="请输入邮箱" prefix-icon="Message" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%; height: 42px; font-size: 15px;">注 册</el-button>
                </el-form-item>
                <div class="register-footer">
                    <span>已有账号？</span>
                    <el-button type="primary" link @click="$router.push('/login')">立即登录</el-button>
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
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;
    padding: 20px 0;
}

.register-container::before {
    content: '';
    position: absolute;
    width: 600px;
    height: 600px;
    background: rgba(255,255,255,0.08);
    border-radius: 50%;
    top: -200px;
    right: -100px;
}

.register-container::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: rgba(255,255,255,0.05);
    border-radius: 50%;
    bottom: -150px;
    left: -100px;
}

.register-card {
    width: 450px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0,0,0,0.15);
    z-index: 1;
}

.register-header {
    text-align: center;
    margin-bottom: 24px;
}

.register-logo {
    font-size: 42px;
    color: #667eea;
    margin-bottom: 10px;
}

.register-header h2 {
    margin: 0 0 8px 0;
    color: #303133;
    font-size: 22px;
}

.register-header p {
    margin: 0;
    color: #909399;
    font-size: 14px;
}

.register-footer {
    text-align: center;
    color: #909399;
    font-size: 14px;
}

@media (max-width: 768px) {
    .register-card {
        width: 90%;
        padding: 24px;
    }

    .register-logo {
        font-size: 36px;
    }

    .register-header h2 {
        font-size: 20px;
    }
}
</style>
