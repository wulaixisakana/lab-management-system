<template>
    <div class="lab-reservation">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>实验室预约</span>
                    <div>
                        <el-button type="success" @click="handleExport">
                            <el-icon><Download /></el-icon> 导出Excel
                        </el-button>
                        <el-radio-group v-model="activeTab" @change="loadList" style="margin-right: 15px;">
                            <el-radio-button label="list" v-if="isAdmin || isTeacher">全部预约</el-radio-button>
                            <el-radio-button label="my">我的预约</el-radio-button>
                        </el-radio-group>
                    </div>
                </div>
            </template>

            <el-form :inline="true" :model="queryForm" v-if="activeTab === 'list'" class="query-form">
                <el-form-item label="实验室">
                    <el-input v-model="queryForm.laboratoryName" placeholder="请输入实验室名称" clearable />
                </el-form-item>
                <el-form-item label="预约人">
                    <el-input v-model="queryForm.userName" placeholder="请输入预约人" clearable />
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="queryForm.status" placeholder="全部" clearable>
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
                <el-table-column prop="laboratoryName" label="实验室" min-width="120" />
                <el-table-column prop="userName" label="预约人" min-width="80" />
                <el-table-column prop="startTime" label="开始时间" width="170">
                    <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="170">
                    <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
                </el-table-column>
                <el-table-column prop="purpose" label="使用目的" show-overflow-tooltip />
                <el-table-column prop="participantCount" label="人数" width="70" />
                <el-table-column prop="status" label="状态" width="90">
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
                        <el-button v-if="canApprove(row) && activeTab === 'list'" type="success" size="small" @click="handleApprove(row)">通过</el-button>
                        <el-button v-if="canApprove(row) && activeTab === 'list'" type="warning" size="small" @click="handleReject(row)">拒绝</el-button>
                        <el-button v-if="canCancel(row)" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
                        <el-button v-if="isAdmin && activeTab === 'list'" type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        <el-dialog v-model="dialogVisible" title="新增实验室预约" width="500px">
            <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                <el-form-item label="实验室" prop="laboratoryId">
                    <el-select v-model="form.laboratoryId" placeholder="请选择实验室">
                        <el-option v-for="lab in labList" :key="lab.id" :label="`${lab.name}（${lab.building} ${lab.roomNumber}）`" :value="lab.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="开始时间" prop="startTime">
                    <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
                </el-form-item>
                <el-form-item label="结束时间" prop="endTime">
                    <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
                </el-form-item>
                <el-form-item label="参与人数" prop="participantCount">
                    <el-input-number v-model="form.participantCount" :min="1" :max="200" />
                </el-form-item>
                <el-form-item label="使用目的" prop="purpose">
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { labReservationApi, laboratoryApi, exportApi } from '@/api'
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
const labList = ref([])

const queryForm = ref({
    laboratoryName: '',
    userName: '',
    status: ''
})

const form = ref({
    laboratoryId: null,
    startTime: '',
    endTime: '',
    participantCount: 1,
    purpose: ''
})

const rules = {
    laboratoryId: [{ required: true, message: '请选择实验室', trigger: 'change' }],
    startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
    endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
    purpose: [{ required: true, message: '请输入使用目的', trigger: 'blur' }]
}

const formatTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'

const getStatusType = (status) => {
    const map = { pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info' }
    return map[status] || 'info'
}

const getStatusText = (status) => {
    const map = { pending: '待审核', approved: '已通过', rejected: '已拒绝', cancelled: '已取消' }
    return map[status] || status
}

const canApprove = (row) => (isAdmin.value || isTeacher.value) && row.status === 'pending'

const canCancel = (row) => row.userId === userStore.user?.id && row.status === 'pending'

const loadList = async () => {
    try {
        if (activeTab.value === 'my') {
            tableData.value = await labReservationApi.getMy()
        } else {
            tableData.value = await labReservationApi.getList(queryForm.value)
        }
        currentPage.value = 1
    } catch (error) {
        console.error(error)
    }
}

const resetQuery = () => {
    queryForm.value = { laboratoryName: '', userName: '', status: '' }
    loadList()
}

const loadLabList = async () => {
    try {
        labList.value = await laboratoryApi.getList({})
    } catch (error) {
        console.error(error)
    }
}

const handleCreate = () => {
    form.value = { laboratoryId: null, startTime: '', endTime: '', participantCount: 1, purpose: '' }
    dialogVisible.value = true
}

const handleSubmit = async () => {
    await formRef.value.validate()
    try {
        const data = {
            ...form.value,
            startTime: dayjs(form.value.startTime).format('YYYY-MM-DD HH:mm:ss'),
            endTime: dayjs(form.value.endTime).format('YYYY-MM-DD HH:mm:ss')
        }
        await labReservationApi.create(data)
        ElMessage.success('预约成功，请等待审批')
        dialogVisible.value = false
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const handleApprove = async (row) => {
    await ElMessageBox.confirm('确认通过该预约？', '提示')
    try {
        await labReservationApi.approve(row.id)
        ElMessage.success('已通过')
        loadList()
    } catch (error) {
        console.error(error)
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
        await labReservationApi.reject(row.id, { reason: value || '' })
        ElMessage.success('已拒绝')
        loadList()
    } catch (error) {
        if (error !== 'cancel') console.error(error)
    }
}

const handleCancel = async (row) => {
    await ElMessageBox.confirm('确认取消该预约？', '提示')
    try {
        await labReservationApi.cancel(row.id)
        ElMessage.success('已取消')
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const handleDelete = async (row) => {
    await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' })
    try {
        await labReservationApi.delete(row.id)
        ElMessage.success('删除成功')
        loadList()
    } catch (error) {
        console.error(error)
    }
}

const handleExport = () => {
    window.open(exportApi.labReservation(queryForm.value), '_blank')
}

const selectedRows = ref([])
const handleSelectionChange = (rows) => {
    selectedRows.value = rows
}
const handleBatchDelete = async () => {
    try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条预约记录吗？`, '批量删除', { type: 'warning' })
        for (const row of selectedRows.value) {
            await labReservationApi.delete(row.id)
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
    if (!isAdmin.value && !isTeacher.value) {
        activeTab.value = 'my'
    }
    loadList()
    loadLabList()
})
</script>

<style scoped>
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
}

.query-form {
    margin-bottom: 15px;
}
</style>
