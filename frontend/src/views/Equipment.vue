<template>
    <div class="equipment">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>设备列表</span>
                    <div>
                        <el-button type="success" @click="handleExport">
                            <el-icon><Download /></el-icon> 导出Excel
                        </el-button>
                        <el-button v-if="isAdmin" type="primary" @click="handleAdd">
                            <el-icon><Plus /></el-icon> 新增设备
                        </el-button>
                    </div>
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

            <div v-if="isAdmin && selectedRows.length" style="margin-bottom:10px">
                <el-button type="danger" @click="handleBatchDelete">批量删除 ({{ selectedRows.length }})</el-button>
            </div>

            <el-table :data="pagedData" border @selection-change="handleSelectionChange">
                <el-table-column v-if="isAdmin" type="selection" width="45" />
                <el-table-column prop="name" label="设备名称" min-width="120" />
                <el-table-column prop="code" label="设备编号" min-width="100" />
                <el-table-column prop="category" label="分类" min-width="90" />
                <el-table-column prop="brand" label="品牌" min-width="80" />
                <el-table-column prop="location" label="位置" min-width="90" />
                <el-table-column prop="status" label="状态">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="price" label="价格" width="100">
                    <template #default="{ row }">¥{{ row.price }}</template>
                </el-table-column>
                <el-table-column v-if="isAdmin" label="操作" width="180">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                style="margin-top: 15px; justify-content: flex-end;"
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50]"
                :total="tableData.length"
                layout="total, sizes, prev, pager, next"
            />
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { equipmentApi, exportApi } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return tableData.value.slice(start, start + pageSize.value)
})
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

const handleExport = () => {
    window.open(exportApi.equipment(queryForm.value), '_blank')
}

const selectedRows = ref([])
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}
const handleBatchDelete = async () => {
    try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个设备吗？`, '批量删除', { type: 'warning' })
        for (const row of selectedRows.value) {
            await equipmentApi.delete(row.id)
        }
        ElMessage.success('批量删除成功')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

let searchTimer = null
const debouncedSearch = () => {
    clearTimeout(searchTimer)
    searchTimer = setTimeout(() => loadList(), 400)
}

onUnmounted(() => {
    clearTimeout(searchTimer)
})

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
