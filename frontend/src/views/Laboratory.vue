<template>
    <div class="laboratory">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>实验室列表</span>
                    <el-button v-if="isAdmin" type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon> 新增实验室
                    </el-button>
                </div>
            </template>

            <el-form :inline="true" :model="queryForm" class="search-form">
                <el-form-item label="实验室名称">
                    <el-input v-model="queryForm.name" placeholder="请输入实验室名称" clearable />
                </el-form-item>
                <el-form-item label="楼栋">
                    <el-select v-model="queryForm.building" placeholder="请选择楼栋" clearable>
                        <el-option label="全部" value="" />
                        <el-option label="实验楼A" value="实验楼A" />
                        <el-option label="实验楼B" value="实验楼B" />
                        <el-option label="实验楼C" value="实验楼C" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
                        <el-option label="全部" value="" />
                        <el-option label="可用" value="available" />
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
                <el-table-column prop="name" label="实验室名称" />
                <el-table-column prop="code" label="实验室编号" />
                <el-table-column prop="building" label="楼栋" />
                <el-table-column prop="floor" label="楼层" />
                <el-table-column prop="roomNumber" label="房间号" />
                <el-table-column prop="area" label="面积(m²)" width="100" />
                <el-table-column prop="capacity" label="容纳人数" width="100" />
                <el-table-column prop="supervisor" label="负责人" />
                <el-table-column prop="phone" label="联系电话" />
                <el-table-column prop="status" label="状态">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column v-if="isAdmin" label="操作" width="180" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 实验室表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="实验室名称" prop="name">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="实验室编号" prop="code">
                    <el-input v-model="form.code" />
                </el-form-item>
                <el-form-item label="楼栋" prop="building">
                    <el-select v-model="form.building" placeholder="请选择楼栋">
                        <el-option label="实验楼A" value="实验楼A" />
                        <el-option label="实验楼B" value="实验楼B" />
                        <el-option label="实验楼C" value="实验楼C" />
                    </el-select>
                </el-form-item>
                <el-form-item label="楼层">
                    <el-input v-model="form.floor" placeholder="如：1F、2F" />
                </el-form-item>
                <el-form-item label="房间号">
                    <el-input v-model="form.roomNumber" placeholder="如：101、201" />
                </el-form-item>
                <el-form-item label="面积">
                    <el-input-number v-model="form.area" :min="0" :precision="2" />
                    <span style="margin-left: 8px">平方米</span>
                </el-form-item>
                <el-form-item label="容纳人数">
                    <el-input-number v-model="form.capacity" :min="0" />
                </el-form-item>
                <el-form-item label="负责人">
                    <el-input v-model="form.supervisor" />
                </el-form-item>
                <el-form-item label="联系电话">
                    <el-input v-model="form.phone" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择状态">
                        <el-option label="可用" value="available" />
                        <el-option label="维护中" value="maintenance" />
                        <el-option label="不可用" value="unavailable" />
                    </el-select>
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
import { laboratoryApi } from '@/api'
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
    building: '',
    status: ''
})

const form = ref({
    id: null,
    name: '',
    code: '',
    building: '',
    floor: '',
    roomNumber: '',
    area: 0,
    capacity: 0,
    supervisor: '',
    phone: '',
    status: 'available',
    description: ''
})

const rules = {
    name: [{ required: true, message: '请输入实验室名称', trigger: 'blur' }],
    code: [{ required: true, message: '请输入实验室编号', trigger: 'blur' }],
    building: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const loadList = async () => {
    try {
        tableData.value = await laboratoryApi.getList(queryForm.value)
    } catch (error) {
        console.error(error)
    }
}

const resetQuery = () => {
    queryForm.value = {
        name: '',
        building: '',
        status: ''
    }
    loadList()
}

const handleAdd = () => {
    isEdit.value = false
    dialogTitle.value = '新增实验室'
    form.value = {
        id: null,
        name: '',
        code: '',
        building: '',
        floor: '',
        roomNumber: '',
        area: 0,
        capacity: 0,
        supervisor: '',
        phone: '',
        status: 'available',
        description: ''
    }
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    dialogTitle.value = '编辑实验室'
    form.value = { ...row }
    dialogVisible.value = true
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定要删除该实验室吗？', '提示', { type: 'warning' })
        await laboratoryApi.delete(row.id)
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
            await laboratoryApi.update(form.value)
            ElMessage.success('更新成功')
        } else {
            await laboratoryApi.save(form.value)
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
        maintenance: 'warning',
        unavailable: 'danger'
    }
    return types[status] || 'info'
}

const getStatusText = (status) => {
    const texts = {
        available: '可用',
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
.laboratory {
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
