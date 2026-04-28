<template>
    <div class="dashboard">
        <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6">
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
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6">
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
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon green"><Calendar /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.reservationCount }}</h3>
                            <p>预约总数</p>
                            <small>设备 {{ stats.equipmentReservationCount }} / 实验室 {{ stats.labReservationCount }}</small>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <el-icon class="stat-icon yellow"><Clock /></el-icon>
                        <div class="stat-info">
                            <h3>{{ stats.pendingReservationCount }}</h3>
                            <p>待审核预约</p>
                            <small>今日新增 {{ stats.todayReservationCount }}</small>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6" v-if="isAdmin || isTeacher">
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
            <el-col :xs="24" :sm="12" :md="isAdmin ? 6 : 8" :lg="isAdmin ? 4 : 6" v-if="isAdmin">

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

        <!-- 欢迎横幅 -->
        <el-card class="welcome-banner" style="margin-top: 20px;">
            <div class="welcome-content">
                <div class="welcome-left">
                    <h3>{{ greetingText }}，{{ userStore.user?.realName || '用户' }}！</h3>
                    <p>今天是 {{ currentDate }}，祝您工作顺利。</p>
                </div>
                <div class="welcome-right">
                    <span class="clock">{{ currentTime }}</span>
                </div>
            </div>
        </el-card>

        <!-- 公告 + 最近操作 -->
        <el-row :gutter="20" style="margin-top: 20px" v-if="isAdmin">
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>系统公告</span></div>
                    </template>
                    <el-timeline>
                        <el-timeline-item timestamp="系统维护" placement="top" type="primary">
                            系统运行正常，请按需使用各功能模块。
                        </el-timeline-item>
                        <el-timeline-item timestamp="温馨提示" placement="top" type="success">
                            请各位按时签到签退，保持良好考勤记录。
                        </el-timeline-item>
                        <el-timeline-item timestamp="使用须知" placement="top" type="warning">
                            预约设备前请确认设备可用状态，避免冲突。
                        </el-timeline-item>
                    </el-timeline>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>最近操作</span></div>
                    </template>
                    <el-table :data="recentLogs" size="small" max-height="220">
                        <el-table-column prop="userName" label="操作人" width="80" />
                        <el-table-column prop="module" label="模块" width="60">
                            <template #default="{ row }">
                                <el-tag size="small">{{ row.module }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="action" label="操作" />
                        <el-table-column prop="createTime" label="时间" width="140">
                            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>

        <!-- 图表区域 -->
        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>设备分类统计</span></div>
                    </template>
                    <div ref="categoryChartRef" class="chart-container"></div>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>设备状态分布</span></div>
                    </template>
                    <div ref="statusChartRef" class="chart-container"></div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>近7天预约趋势</span></div>
                    </template>
                    <div ref="reservationChartRef" class="chart-container"></div>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>近7天考勤统计</span></div>
                    </template>
                    <div ref="attendanceChartRef" class="chart-container"></div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px">
            <el-col :xs="24" :md="12">
                <el-card>
                    <template #header>
                        <div class="card-header"><span>预约状态统计</span></div>
                    </template>
                    <div ref="reservationStatusChartRef" class="chart-container"></div>
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
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
                    <!-- 今日考勤状态 -->
                    <div v-if="isAdmin || isTeacher" style="margin-top: 20px; border-top: 1px solid #ebeef5; padding-top: 16px;">
                        <h4 style="margin: 0 0 12px 0; color: #303133;">今日考勤状态</h4>
                        <div v-if="todayAttendance" class="attendance-status">
                            <el-tag :type="getStatusType(todayAttendance.status)">
                                {{ getStatusText(todayAttendance.status) }}
                            </el-tag>
                            <p>签到时间: {{ formatTime(todayAttendance.checkInTime) }}</p>
                            <p v-if="todayAttendance.checkOutTime">签退时间: {{ formatTime(todayAttendance.checkOutTime) }}</p>
                            <p v-if="todayAttendance.duration">在岗时长: {{ todayAttendance.duration }} 分钟</p>
                        </div>
                        <el-empty v-else description="今日未签到" />
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { statisticsApi, attendanceApi, logApi } from '@/api'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import * as echarts from 'echarts'

dayjs.locale('zh-cn')

const userStore = useUserStore()
const isAdmin = computed(() => userStore.user?.role === 'admin')
const isTeacher = computed(() => userStore.user?.role === 'teacher')

const stats = ref({
    equipmentCount: 0,
    laboratoryCount: 0,
    reservationCount: 0,
    equipmentReservationCount: 0,
    labReservationCount: 0,
    pendingReservationCount: 0,
    todayReservationCount: 0,
    attendanceCount: 0,
    userCount: 0
})

const todayAttendance = ref(null)
const recentLogs = ref([])
const currentTime = ref('')
const currentDate = ref('')
let clockTimer = null

const greetingText = computed(() => {
    const h = new Date().getHours()
    if (h < 6) return '凌晨好'
    if (h < 12) return '上午好'
    if (h < 14) return '中午好'
    if (h < 18) return '下午好'
    return '晚上好'
})

const updateClock = () => {
    const now = dayjs()
    currentTime.value = now.format('HH:mm:ss')
    currentDate.value = now.format('YYYY年MM月DD日 dddd')
}

const categoryChartRef = ref(null)
const statusChartRef = ref(null)
const reservationChartRef = ref(null)
const attendanceChartRef = ref(null)
const reservationStatusChartRef = ref(null)

let chartInstances = []

const statusTextMap = {
    available: '可用',
    in_use: '使用中',
    maintenance: '维护中',
    unavailable: '不可用'
}

const reservationStatusTextMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    cancelled: '已取消'
}

const initCharts = (data) => {
    chartInstances.forEach(c => c.dispose())
    chartInstances = []

    // 设备分类饼图
    if (categoryChartRef.value) {
        const chart = echarts.init(categoryChartRef.value)
        const categoryData = Object.entries(data.equipmentCategoryStats || {}).map(([name, value]) => ({ name, value }))
        chart.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
            legend: { bottom: 0 },
            color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#13c2c2', '#a855f7'],
            series: [{
                type: 'pie',
                radius: ['40%', '65%'],
                center: ['50%', '45%'],
                data: categoryData,
                label: { formatter: '{b}\n{d}%' },
                emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' } }
            }]
        })
        chartInstances.push(chart)
    }

    // 设备状态饼图
    if (statusChartRef.value) {
        const chart = echarts.init(statusChartRef.value)
        const statusData = Object.entries(data.equipmentStatusStats || {}).map(([key, value]) => ({
            name: statusTextMap[key] || key, value
        }))
        chart.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
            legend: { bottom: 0 },
            color: ['#67c23a', '#e6a23c', '#909399', '#f56c6c'],
            series: [{
                type: 'pie',
                radius: '60%',
                center: ['50%', '45%'],
                data: statusData,
                label: { formatter: '{b}\n{c}台' },
                emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' } }
            }]
        })
        chartInstances.push(chart)
    }

    // 预约趋势折线图
    if (reservationChartRef.value) {
        const chart = echarts.init(reservationChartRef.value)
        const trend = data.reservationTrend || []
        chart.setOption({
            tooltip: { trigger: 'axis' },
            legend: { data: ['设备预约', '实验室预约'], bottom: 0 },
            grid: { left: 50, right: 20, top: 20, bottom: 40 },
            xAxis: {
                type: 'category',
                data: trend.map(t => t.date.substring(5)),
                axisLabel: { fontSize: 12 }
            },
            yAxis: { type: 'value', minInterval: 1 },
            series: [{
                name: '设备预约',
                type: 'line',
                data: trend.map(t => t.eqCount),
                smooth: true,
                areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(64,158,255,0.3)' },
                    { offset: 1, color: 'rgba(64,158,255,0.05)' }
                ])},
                lineStyle: { color: '#409eff', width: 2 },
                itemStyle: { color: '#409eff' }
            }, {
                name: '实验室预约',
                type: 'line',
                data: trend.map(t => t.labCount),
                smooth: true,
                areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(103,126,234,0.3)' },
                    { offset: 1, color: 'rgba(103,126,234,0.05)' }
                ])},
                lineStyle: { color: '#667eea', width: 2 },
                itemStyle: { color: '#667eea' }
            }]
        })
        chartInstances.push(chart)
    }

    // 考勤统计柱状图
    if (attendanceChartRef.value) {
        const chart = echarts.init(attendanceChartRef.value)
        const trend = data.attendanceTrend || []
        chart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { left: 50, right: 20, top: 20, bottom: 30 },
            xAxis: {
                type: 'category',
                data: trend.map(t => t.date.substring(5)),
                axisLabel: { fontSize: 12 }
            },
            yAxis: { type: 'value', minInterval: 1 },
            series: [{
                type: 'bar',
                data: trend.map(t => t.count),
                barWidth: '40%',
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: '#67c23a' },
                        { offset: 1, color: '#b7eb8f' }
                    ]),
                    borderRadius: [4, 4, 0, 0]
                }
            }]
        })
        chartInstances.push(chart)
    }

    // 预约状态环形图
    if (reservationStatusChartRef.value) {
        const chart = echarts.init(reservationStatusChartRef.value)
        const statusData = Object.entries(data.reservationStatusStats || {}).map(([key, value]) => ({
            name: reservationStatusTextMap[key] || key, value
        }))
        chart.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
            legend: { bottom: 0 },
            color: ['#e6a23c', '#67c23a', '#f56c6c', '#909399'],
            series: [{
                type: 'pie',
                radius: ['40%', '65%'],
                center: ['50%', '45%'],
                data: statusData,
                label: { formatter: '{b}\n{d}%' },
                emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' } }
            }]
        })
        chartInstances.push(chart)
    }
}

const handleResize = () => {
    chartInstances.forEach(c => c.resize())
}

const loadStats = async () => {
    try {
        const data = await statisticsApi.getOverview()
        stats.value.equipmentCount = data.equipmentCount || 0
        stats.value.laboratoryCount = data.laboratoryCount || 0
        stats.value.reservationCount = data.reservationCount || 0
        stats.value.equipmentReservationCount = data.equipmentReservationCount || 0
        stats.value.labReservationCount = data.labReservationCount || 0
        stats.value.pendingReservationCount = data.pendingReservationCount || 0
        stats.value.todayReservationCount = data.todayReservationCount || 0
        stats.value.attendanceCount = data.attendanceCount || 0
        stats.value.userCount = data.userCount || 0

        await nextTick()
        initCharts(data)
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

const loadRecentLogs = async () => {
    if (isAdmin.value) {
        try {
            const data = await logApi.getList({})
            recentLogs.value = (data || []).slice(0, 10)
        } catch (e) {
            console.error(e)
        }
    }
}

onMounted(() => {
    loadStats()
    loadTodayAttendance()
    loadRecentLogs()
    updateClock()
    clockTimer = setInterval(updateClock, 1000)
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    chartInstances.forEach(c => c.dispose())
    if (clockTimer) clearInterval(clockTimer)
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

.stat-icon.yellow {
    background: #fffbe6;
    color: #faad14;
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

.stat-info small {
    color: #b0b0b0;
    font-size: 12px;
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

.chart-container {
    height: 300px;
    width: 100%;
}

.welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
}

.welcome-banner :deep(.el-card__body) {
    padding: 20px 24px;
}

.welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.welcome-left h3 {
    margin: 0 0 6px 0;
    font-size: 20px;
    color: #fff;
}

.welcome-left p {
    margin: 0;
    color: rgba(255,255,255,0.8);
    font-size: 14px;
}

.clock {
    font-size: 36px;
    font-weight: bold;
    color: #fff;
    font-family: 'Courier New', monospace;
    letter-spacing: 2px;
}
</style>
