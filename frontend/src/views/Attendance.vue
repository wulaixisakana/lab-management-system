<template>
    <div class="attendance">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>考勤管理</span>
                    <div>
                        <el-button @click="activeTab = 'list'">考勤记录</el-button>
                        <el-button type="primary" @click="activeTab = 'my'">我的考勤</el-button>
                    </div>
                </div>
            </template>

            <!-- 筛选表单 -->
            <el-form v-if="activeTab === 'list'" :inline="true" :model="queryForm" class="search-form">
                <el-form-item label="用户姓名">
                    <el-input v-model="queryForm.userName" placeholder="请输入用户姓名" clearable />
                </el-form-item>
                <el-form-item label="日期范围">
                    <el-date-picker v-model="dateRange" type="daterange" placeholder="选择日期" @change="handleDateChange" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList">查询</el-button>
                    <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 快速签到签退 -->
            <div v-if="activeTab === 'my'" class="quick-actions">
                <el-button type="success" size="large" @click="handleCheckIn" :disabled="todayCheckedIn && !todayCheckedOut">
                    <el-icon><Clock /></el-icon> 签到
                </el-button>
                <el-button type="warning" size="large" @click="handleCheckOut" :disabled="!todayCheckedIn || todayCheckedOut">
                    <el-icon><Clock /></el-icon> 签退
                </el-button>
            </div>

            <el-table :data="tableData" border style="margin-top: 15px">
                <el-table-column prop="userName" label="姓名" />
                <el-table-column prop="checkInTime" label="签到时间" width="180">
                    <template #default="{ row }">{{ formatTime(row.checkInTime) }}</template>
                </el-table-column>
                <el-table-column prop="checkOutTime" label="签退时间" width="180">
                    <template #default="{ row }">{{ row.checkOutTime ? formatTime(row.checkOutTime) : '-' }}</template>
                </el-table-column>
                <el-table-column prop="duration" label="在岗时长" width="120">
                    <template #default="{ row }">{{ row.duration ? row.duration + ' 分钟' : '-' }}</template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { attendanceApi } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')

const activeTab = ref('list')
const tableData = ref([])
const dateRange = ref([])
const todayCheckedIn = ref(false)
const todayCheckedOut = ref(false)

const queryForm = ref({
    userName: '',
    startDate: '',
    endDate: ''
})

const loadList = async () => {
    try {
        if (activeTab.value === 'my') {
            tableData.value = await attendanceApi.getMy()
        } else {
            tableData.value = await attendanceApi.getList(queryForm.value)
        }
    } catch (error) {
        console.error(error)
    }
}

const loadTodayStatus = async () => {
    try {
        const res = await attendanceApi.getToday()
        todayCheckedIn.value = !!res
        todayCheckedOut.value = res?.checkOutTime != null
    } catch (error) {
        todayCheckedIn.value = false
        todayCheckedOut.value = false
    }
}

const handleDateChange = () => {
    if (dateRange.value && dateRange.value.length === 2) {
        queryForm.value.startDate = dayjs(dateRange.value[0]).format('YYYY-MM-DD')
        queryForm.value.endDate = dayjs(dateRange.value[1]).format('YYYY-MM-DD')
    } else {
        queryForm.value.startDate = ''
        queryForm.value.endDate = ''
    }
}

const resetQuery = () => {
    queryForm.value = {
        userName: '',
        startDate: '',
        endDate: ''
    }
    dateRange.value = []
    loadList()
}

const handleCheckIn = async () => {
    try {
        await attendanceApi.checkIn()
        ElMessage.success('签到成功')
        loadList()
        loadTodayStatus()
    } catch (error) {
        console.error(error)
    }
}

const handleCheckOut = async () => {
    try {
        await attendanceApi.checkOut()
        ElMessage.success('签退成功')
        loadList()
        loadTodayStatus()
    } catch (error) {
        console.error(error)
    }
}

const formatTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusType = (status) => {
    const types = {
        present: 'success',
        late: 'warning',
        early_leave: 'warning',
        absent: 'danger'
    }
    return types[status] || 'info'
}

const getStatusText = (status) => {
    const texts = {
        present: '正常',
        late: '迟到',
        early_leave: '早退',
        absent: '缺勤'
    }
    return texts[status] || status
}

watch(activeTab, () => {
    loadList()
    if (activeTab.value === 'my') {
        loadTodayStatus()
    }
})

onMounted(() => {
    loadList()
})
</script>

<style scoped>
.attendance {
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

.quick-actions {
    margin-bottom: 15px;
}

.quick-actions .el-button {
    margin-right: 10px;
}
</style>
