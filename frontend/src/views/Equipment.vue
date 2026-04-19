<template>
    <div class="equipment">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>设备列表</span>
                    <el-button v-if="isAdmin" type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon> 新增设备
                    </el-button>
                </div>
            </template>

            <el-form :inline="true" :model="queryForm" class="search-form">
                <el-form-item label="设备名称">
                    <el-input v-model="queryForm.name" placeholder="请输入设备名称" clearable />
                </el-form-item>
                <el-form-item label="设备分类">
                    <el-select v-model="queryForm.category" placeholder="请选择分类" clearable>
                        <el-option label="全部" value="" />
                        <el-option label="精密仪器" value="精密仪器" />
                        <el-option label="分析仪器" value="分析仪器" />
                        <el-option label="分离设备" value="分离设备" />
                        <el-option label="培养设备" value="培养设备" />
                        <el-option label="储存设备" value="储存设备" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
                        <el-option label="全部" value="" />
                        <el-option label="可用" value="available" />
                        <el-option label="使用中" value="in_use" />
                        <el-option label="维护中" value="maintenance" />
                        <el-option label="不可用" value="unavailable" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList">查询</el-button>
                    <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="tableData" border>
                <el-table-column prop="name" label="设备名称" />
                <el-table-column prop="code" label="设备编号" />
                <el-table-column prop="category" label="分类" />
                <el-table-column prop="brand" label="品牌" />
                <el-table-column prop="location" label="位置" />
                <el-table-column prop="status" label="状态">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="price" label="价格" width="100">
                    <template #default="{ row }">¥{{ row.price }}</template>
                </el-table-column>
                <el-table-column v-if="isAdmin" label="操作" width="180" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 设备表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="设备名称" prop="name">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="设备编号" prop="code">
                    <el-input v-model="form.code" />
                </el-form-item>
                <el-form-item label="设备分类" prop="category">
                    <el-select v-model="form.category" placeholder="请选择分类">
                        <el-option label="精密仪器" value="精密仪器" />
                        <el-option label="分析仪器" value="分析仪器" />
                        <el-option label="分离设备" value="分离设备" />
                        <el-option label="培养设备" value="培养设备" />
                        <el-option label="储存设备" value="储存设备" />
                        <el-option label="计量仪器" value="计量仪器" />
                        <el-option label="检测仪器" value="检测仪器" />
                    </el-select>
                </el-form-item>
                <el-form-item label="品牌">
                    <el-input v-model="form.brand" />
                </el-form-item>
                <el-form-item label="型号">
                    <el-input v-model="form.model" />
                </el-form-item>
                <el-form-item label="存放位置" prop="location">
                    <el-input v-model="form.location" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择状态">
                        <el-option label="可用" value="available" />
                        <el-option label="使用中" value="in_use" />
                        <el-option label="维护中" value="maintenance" />
                        <el-option label="不可用" value="unavailable" />
                    </el-select>
                </el-form-item>
                <el-form-item label="价格">
                    <el-input-number v-model="form.price" :min="0" :precision="2" />
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { equipmentApi } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const isEdit = ref(false)

const queryForm = ref({
    name: '',
    category: '',
    status: ''
})

const form = ref({
    id: null,
    name: '',
    code: '',
    category: '',
    brand: '',
    model: '',
    location: '',
    status: 'available',
    price: 0,
    description: ''
})

const rules = {
    name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
    category: [{ required: true, message: '请选择设备分类', trigger: 'change' }],
    location: [{ required: true, message: '请输入存放位置', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadList = async () => {
    try {
        tableData.value = await equipmentApi.getList(queryForm.value)
    } catch (error) {
        console.error(error)
    }
}

const resetQuery = () => {
    queryForm.value = {
        name: '',
        category: '',
        status: ''
    }
    loadList()
}

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增设备'
    form.value = {
        id: null,
        name: '',
        code: '',
        category: '',
        brand: '',
        model: '',
        location: '',
        status: 'available',
        price: 0,
        description: ''
    }
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑设备'
    form.value = { ...row }
    dialogVisible.value = true
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定要删除该设备吗？', '提示', { type: 'warning' })
        await equipmentApi.delete(row.id)
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
            await equipmentApi.update(form.value)
            ElMessage.success('更新成功')
        } else {
            await equipmentApi.save(form.value)
            ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const getStatusType = (status) => {
    const types = {
        available: 'success',
        in_use: 'warning',
        maintenance: 'info',
        unavailable: 'danger'
    }
    return types[status] || 'info'
}

const getStatusText = (status) => {
    const texts = {
        available: '可用',
        in_use: '使用中',
        maintenance: '维护中',
        unavailable: '不可用'
    }
    return texts[status] || status
}

onMounted(() => {
    loadList()
})
</script>

<style scoped>
.equipment {
    padding: 0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-form {
    margin-bottom: 15px;
}
</style>
