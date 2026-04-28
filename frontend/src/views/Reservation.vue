<template>
    <div class="reservation">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>设备预约</span>
                    <div>
                        <el-button type="success" @click="handleExport">
                            <el-icon><Download /></el-icon> 导出Excel
                        </el-button>
                        <el-button @click="activeTab = 'list'">预约列表</el-button>
                        <el-button type="primary" @click="activeTab = 'my'">我的预约</el-button>
                    </div>
                </div>
            </template>

            <!-- 筛选表单 -->
            <el-form v-if="activeTab === 'list'" :inline="true" :model="queryForm" class="search-form">
                <el-form-item label="设备名称">
                    <el-input v-model="queryForm.equipmentName" placeholder="请输入设备名称" clearable />
                </el-form-item>
                <el-form-item label="用户姓名">
                    <el-input v-model="queryForm.userName" placeholder="请输入用户姓名" clearable />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
                        <el-option label="全部" value="" />
                        <el-option label="待审核" value="pending" />
                        <el-option label="已通过" value="approved" />
                        <el-option label="已拒绝" value="rejected" />
                        <el-option label="已取消" value="cancelled" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList">查询</el-button>
                    <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <div style="display:flex;gap:8px;flex-wrap:wrap;align-items:center">
                <el-button v-if="activeTab === 'my'" type="primary" @click="handleCreate">
                    <el-icon><Plus /></el-icon> 新增预约
                </el-button>
                <el-button v-if="isAdmin && activeTab === 'list' && selectedRows.length" type="danger" @click="handleBatchDelete">
                    批量删除 ({{ selectedRows.length }})
                </el-button>
            </div>

            <el-table :data="pagedData" border style="margin-top: 15px" @selection-change="handleSelectionChange">
                <el-table-column v-if="isAdmin && activeTab === 'list'" type="selection" width="45" />
                <el-table-column prop="equipmentName" label="设备名称" min-width="120" />
                <el-table-column prop="userName" label="预约人" min-width="80" />
                <el-table-column prop="startTime" label="开始时间" width="180">
                    <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="180">
                    <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
                </el-table-column>
                <el-table-column prop="purpose" label="使用目的" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="rejectReason" label="拒绝理由" min-width="120">
                    <template #default="{ row }">
                        <span v-if="row.rejectReason" style="color:#f56c6c">{{ row.rejectReason }}</span>
                        <span v-else style="color:#c0c4cc">-</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                        <el-button v-if="canApprove(row) && activeTab === 'list'" type="success" size="small" @click="handleApprove(row)">
                            通过
                        </el-button>
                        <el-button v-if="canApprove(row) && activeTab === 'list'" type="warning" size="small" @click="handleReject(row)">
                            拒绝
                        </el-button>
                        <el-button v-if="canCancel(row)" type="danger" size="small" @click="handleCancel(row)">
                            取消
                        </el-button>
                        <el-button v-if="isAdmin && activeTab === 'list'" type="danger" size="small" @click="handleDelete(row)">
                            删除
                        </el-button>
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

        <!-- 新增预约对话框 -->
        <el-dialog v-model="dialogVisible" title="新增预约" width="500px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="设备" prop="equipmentId">
                    <el-select v-model="form.equipmentId" placeholder="请选择设备" @change="handleEquipmentChange">
                        <el-option v-for="eq in equipmentList" :key="eq.id" :label="eq.name" :value="eq.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="开始时间" prop="startTime">
                    <el-date-picker v-model="form.startTime" type="datetime" placeholder="请选择开始时间" style="width: 100%" />
                </el-form-item>
                <el-form-item label="结束时间" prop="endTime">
                    <el-date-picker v-model="form.endTime" type="datetime" placeholder="请选择结束时间" style="width: 100%" />
                </el-form-item>
                <el-form-item label="使用目的">
                    <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请输入使用目的" />
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
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reservationApi, equipmentApi, exportApi } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')
const isTeacher = computed(() => userStore.user?.role === 'teacher')

const activeTab = ref('list')
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return tableData.value.slice(start, start + pageSize.value)
})
const dialogVisible = ref(false)
const formRef = ref()
const equipmentList = ref([])

const queryForm = ref({
    equipmentName: '',
    userName: '',
    status: ''
})

const form = ref({
    equipmentId: null,
    startTime: '',
    endTime: '',
    purpose: ''
})

const rules = {
    equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
    startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
    endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const loadList = async () => {
    try {
        if (activeTab.value === 'my') {
            tableData.value = await reservationApi.getMy()
        } else {
            tableData.value = await reservationApi.getList(queryForm.value)
        }
    } catch (error) {
        console.error(error)
    }
}

const loadEquipments = async () => {
    try {
        equipmentList.value = await equipmentApi.getList({ status: 'available' })
    } catch (error) {
        console.error(error)
    }
}

const resetQuery = () => {
    queryForm.value = {
        equipmentName: '',
        userName: '',
        status: ''
    }
    loadList()
}

const handleCreate = () => {
    form.value = {
        equipmentId: null,
        startTime: '',
        endTime: '',
        purpose: ''
    }
    loadEquipments()
    dialogVisible.value = true
}

const handleEquipmentChange = () => {
    // 可以在这里添加设备变化时的逻辑
}

const handleSubmit = async () => {
    await formRef.value.validate()
    try {
        const data = {
            ...form.value,
            startTime: dayjs(form.value.startTime).format('YYYY-MM-DD HH:mm:ss'),
            endTime: dayjs(form.value.endTime).format('YYYY-MM-DD HH:mm:ss')
        }
        await reservationApi.create(data)
        ElMessage.success('预约成功，等待审核')
        dialogVisible.value = false
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const handleApprove = async (row) => {
    try {
        await ElMessageBox.confirm('确定通过该预约吗？', '提示', { type: 'warning' })
        await reservationApi.approve(row.id)
        ElMessage.success('已通过')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const handleReject = async (row) => {
    try {
        const { value } = await ElMessageBox.prompt('请输入拒绝理由（可选）', '拒绝预约', {
            confirmButtonText: '确认拒绝',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入拒绝理由',
            type: 'warning'
        })
        await reservationApi.reject(row.id, { reason: value || '' })
        ElMessage.success('已拒绝')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const handleCancel = async (row) => {
    try {
        await ElMessageBox.confirm('确定取消该预约吗？', '提示', { type: 'warning' })
        await reservationApi.cancel(row.id)
        ElMessage.success('已取消')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm('确定删除该预约记录吗？', '提示', { type: 'warning' })
        await reservationApi.delete(row.id)
        ElMessage.success('删除成功')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const canApprove = (row) => {
    return (isAdmin.value || isTeacher.value) && row.status === 'pending'
}

const canCancel = (row) => {
    return activeTab.value === 'my' && (row.status === 'pending' || row.status === 'approved')
}

const formatTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const getStatusType = (status) => {
    const types = {
        pending: 'warning',
        approved: 'success',
        rejected: 'danger',
        cancelled: 'info'
    }
    return types[status] || 'info'
}

const getStatusText = (status) => {
    const texts = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已拒绝',
        cancelled: '已取消'
    }
    return texts[status] || status
}

const selectedRows = ref([])
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}
const handleBatchDelete = async () => {
    try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条预约记录吗？`, '批量删除', { type: 'warning' })
        for (const row of selectedRows.value) {
            await reservationApi.delete(row.id)
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

watch(activeTab, () => {
    loadList()
})

onUnmounted(() => {
    clearTimeout(searchTimer)
})

const handleExport = () => {
    window.open(exportApi.reservation(queryForm.value), '_blank')
}

onMounted(() => {
    loadList()
})
</script>

<style scoped>
.reservation {
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
