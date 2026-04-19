<template>
    <div class="users">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>用户管理</span>
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon> 新增用户
                    </el-button>
                </div>
            </template>

            <el-table :data="tableData" border>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="realName" label="真实姓名" />
                <el-table-column prop="phone" label="电话" />
                <el-table-column prop="email" label="邮箱" />
                <el-table-column prop="role" label="角色" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getRoleType(row.role)">{{ getRoleText(row.role) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                            {{ row.status === 'active' ? '正常' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 用户表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" :disabled="isEdit" />
                </el-form-item>
                <el-form-item v-if="!isEdit" label="密码" prop="password">
                    <el-input v-model="form.password" type="password" />
                </el-form-item>
                <el-form-item label="真实姓名" prop="realName">
                    <el-input v-model="form.realName" />
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="form.email" />
                </el-form-item>
                <el-form-item label="角色" prop="role">
                    <el-select v-model="form.role" placeholder="请选择角色">
                        <el-option label="管理员" value="admin" />
                        <el-option label="教师" value="teacher" />
                        <el-option label="学生" value="student" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status" placeholder="请选择状态">
                        <el-option label="正常" value="active" />
                        <el-option label="禁用" value="disabled" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const form = ref({
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    role: 'student',
    status: 'active'
})

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadList = async () => {
    try {
        tableData.value = await userApi.getList()
    } catch (error) {
        console.error(error)
    }
}

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增用户'
    form.value = {
        id: null,
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: '',
        role: 'student',
        status: 'active'
    }
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑用户'
    form.value = { ...row, password: '' }
    dialogVisible.value = true
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
        await userApi.delete(row.id)
        ElMessage.success('删除成功')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const handleSubmit = async () => {
    await formRef.value.validate()
    try {
        if (isEdit.value) {
            await userApi.update(form.value)
            ElMessage.success('更新成功')
        } else {
            await userApi.register(form.value)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const getRoleType = (role) => {
    const types = {
        admin: 'danger',
        teacher: 'warning',
        student: 'success'
    }
    return types[role] || 'info'
}

const getRoleText = (role) => {
    const texts = {
        admin: '管理员',
        teacher: '教师',
        student: '学生'
    }
    return texts[role] || role
}

onMounted(() => {
    loadList()
})
</script>

<style scoped>
.users {
    padding: 0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
