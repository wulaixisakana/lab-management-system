<template>
    <div class="dashboard">
        <el-row :gutter="20">
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon blue"><Setting /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.equipmentCount }}</h3>
                            <p>设备总数</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon cyan"><OfficeBuilding /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.laboratoryCount }}</h3>
                            <p>实验室总数</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon green"><Calendar /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.reservationCount }}</h3>
                            <p>预约记录</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6" v-if="isAdmin || isTeacher">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon orange"><Clock /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.attendanceCount }}</h3>
                            <p>今日考勤</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="6" v-if="isAdmin">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon purple"><User /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.userCount }}</h3>
                            <p>用户总数</p>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>快捷操作</span>
                        </div>
                    </template>
                    <div class="quick-actions">
                        <el-button type="primary" @click="$router.push('/equipment')">
                            <el-icon><Setting /></el-icon> 设备管理
                        </el-button>
                        <el-button type="success" @click="$router.push('/reservation')">
                            <el-icon><Calendar /></el-icon> 设备预约
                        </el-button>
                        <el-button type="warning" @click="handleCheckIn" v-if="isAdmin || isTeacher">
                            <el-icon><Clock /></el-icon> 签到
                        </el-button>
                        <el-button type="info" @click="handleCheckOut" v-if="isAdmin || isTeacher">
                            <el-icon><Clock /></el-icon> 签退
                        </el-button>
                        <el-button type="info" @click="$router.push('/laboratory')" v-if="!isAdmin && !isTeacher">
                            <el-icon><OfficeBuilding /></el-icon> 实验室管理
                        </el-button>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12" v-if="isAdmin || isTeacher">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <span>今日考勤状态</span>
                        </div>
                    </template>
                    <div v-if="todayAttendance" class="attendance-status">
                        <el-tag :type="getStatusType(todayAttendance.status)">
                            {{ getStatusText(todayAttendance.status) }}
                        </el-tag>
                        <p>签到时间: {{ formatTime(todayAttendance.checkInTime) }}</p>
                        <p v-if="todayAttendance.checkOutTime">签退时间: {{ formatTime(todayAttendance.checkOutTime) }}</p>
                        <p v-if="todayAttendance.duration">在岗时长: {{ todayAttendance.duration }} 分钟</p>
                    </div>
                    <el-empty v-else description="今日未签到" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { equipmentApi, attendanceApi, userApi, reservationApi, laboratoryApi } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')
const isTeacher = computed(() => userStore.user?.role === 'teacher')

const stats = ref({
    equipmentCount: 0,
    laboratoryCount: 0,
    reservationCount: 0,
    attendanceCount: 0,
    userCount: 0
})

const todayAttendance = ref(null)

const loadStats = async () => {
    try {
        const [equipments, laboratories, reservations, attendances, users] = await Promise.all([
            equipmentApi.getList({}),
            laboratoryApi.getList({}),
            reservationApi.getList({}),
            attendanceApi.getList({}),
            userApi.getList()
        ])
        stats.value.equipmentCount = equipments?.length || 0
        stats.value.laboratoryCount = laboratories?.length || 0
        stats.value.reservationCount = reservations?.length || 0
        stats.value.attendanceCount = attendances?.length || 0
        stats.value.userCount = users?.length || 0
    } catch (error) {
        console.error(error)
    }
}

const loadTodayAttendance = async () => {
    try {
        const res = await attendanceApi.getToday()
        todayAttendance.value = res
    } catch (error) {
        todayAttendance.value = null
    }
}

const handleCheckIn = async () => {
    try {
        await attendanceApi.checkIn()
        ElMessage.success('签到成功')
        loadTodayAttendance()
        loadStats()
    } catch (error) {
        console.error(error)
    }
}

const handleCheckOut = async () => {
    try {
        await attendanceApi.checkOut()
        ElMessage.success('签退成功')
        loadTodayAttendance()
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

onMounted(() => {
    loadStats()
    loadTodayAttendance()
})
</script>

<style scoped>
.dashboard {
    padding: 0;
}

.stat-card {
    cursor: pointer;
    transition: all 0.3s;
}

.stat-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
}

.stat-icon {
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 30px;
    border-radius: 8px;
}

.stat-icon.blue {
    background: #ecf5ff;
    color: #409eff;
}

.stat-icon.green {
    background: #f0f9ff;
    color: #67c23a;
}

.stat-icon.orange {
    background: #fdf6ec;
    color: #e6a23c;
}

.stat-icon.purple {
    background: #f4e4f8;
    color: #a855f7;
}

.stat-icon.cyan {
    background: #e6f7ff;
    color: #13c2c2;
}

.stat-info h3 {
    margin: 0 0 5px 0;
    font-size: 28px;
    color: #303133;
}

.stat-info p {
    margin: 0;
    color: #909399;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.quick-actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
}

.quick-actions .el-button {
    height: 50px;
    font-size: 16px;
}

.attendance-status {
    text-align: center;
}

.attendance-status .el-tag {
    font-size: 16px;
    padding: 8px 16px;
}

.attendance-status p {
    margin: 10px 0 0 0;
    color: #606266;
}
</style>
