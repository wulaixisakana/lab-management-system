<template>
    <div class="profile">
        <el-row :gutter="20">
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>个人信息</span>
                        </div>
                    </template>
                    <el-form :model="profileForm" :rules="profileRules" ref="profileRef" label-width="80px">
                        <el-form-item label="用户名">
                            <el-input v-model="profileForm.username" disabled />
                        </el-form-item>
                        <el-form-item label="角色">
                            <el-tag :type="roleTagType">{{ roleText }}</el-tag>
                        </el-form-item>
                        <el-form-item label="姓名" prop="realName">
                            <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                        </el-form-item>
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>修改密码</span>
                        </div>
                    </template>
                    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordRef" label-width="100px">
                        <el-form-item label="当前密码" prop="oldPassword">
                            <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" />
                        </el-form-item>
                        <el-form-item label="确认密码" prop="confirmPassword">
                            <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleChangePassword" :loading="changingPwd">修改密码</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
                <el-card style="margin-top: 20px;">
                    <template #header>
                        <div class="card-header">
                            <span>账号信息</span>
                        </div>
                    </template>
                    <el-descriptions :column="1" border>
                        <el-descriptions-item label="账号状态">
                            <el-tag :type="profileForm.status === 'active' ? 'success' : 'danger'">
                                {{ profileForm.status === 'active' ? '正常' : '已禁用' }}
                            </el-tag>
                        </el-descriptions-item>
                        <el-descriptions-item label="注册时间">
                            {{ profileForm.createTime || '-' }}
                        </el-descriptions-item>
                    </el-descriptions>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi, userApi } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()

const profileRef = ref()
const passwordRef = ref()
const saving = ref(false)
const changingPwd = ref(false)

const profileForm = ref({
    username: '',
    realName: '',
    phone: '',
    email: '',
    role: '',
    status: '',
    createTime: ''
})

const passwordForm = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const roleText = computed(() => {
    const map = { admin: '管理员', teacher: '教师', student: '学生' }
    return map[profileForm.value.role] || profileForm.value.role
})

const roleTagType = computed(() => {
    const map = { admin: 'danger', teacher: 'warning', student: '' }
    return map[profileForm.value.role] || 'info'
})

const profileRules = {
    realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
    email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const validateConfirmPassword = (rule, value, callback) => {
    if (value !== passwordForm.value.newPassword) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const passwordRules = {
    oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ]
}

const loadProfile = async () => {
    try {
        const user = await authApi.getInfo()
        profileForm.value.username = user.username
        profileForm.value.realName = user.realName || ''
        profileForm.value.phone = user.phone || ''
        profileForm.value.email = user.email || ''
        profileForm.value.role = user.role
        profileForm.value.status = user.status
        profileForm.value.createTime = user.createTime ? dayjs(user.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'
    } catch (error) {
        console.error(error)
    }
}

const handleSaveProfile = async () => {
    try {
        await profileRef.value.validate()
        saving.value = true
        const updatedUser = await userApi.updateProfile({
            realName: profileForm.value.realName,
            phone: profileForm.value.phone,
            email: profileForm.value.email
        })
        userStore.setUser(updatedUser)
        ElMessage.success('个人信息更新成功')
    } catch (error) {
        if (error !== false) console.error(error)
    } finally {
        saving.value = false
    }
}

const handleChangePassword = async () => {
    try {
        await passwordRef.value.validate()
        changingPwd.value = true
        await userApi.changePassword({
            oldPassword: passwordForm.value.oldPassword,
            newPassword: passwordForm.value.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
        setTimeout(() => {
            userStore.logout()
        }, 1500)
    } catch (error) {
        if (error !== false) console.error(error)
    } finally {
        changingPwd.value = false
    }
}

onMounted(() => {
    loadProfile()
})
</script>

<style scoped>
.profile {
    padding: 0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
}
</style>
