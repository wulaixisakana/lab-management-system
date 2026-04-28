<template>
    <div class="operation-log">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>操作日志</span>
                    <el-button type="success" @click="handleExport">
                        <el-icon><Download /></el-icon> 导出Excel
                    </el-button>
                </div>
            </template>

            <el-form :inline="true" :model="queryForm" class="query-form">
                <el-form-item label="操作人">
                    <el-input v-model="queryForm.userName" placeholder="请输入操作人" clearable />
                </el-form-item>
                <el-form-item label="模块">
                    <el-select v-model="queryForm.module" placeholder="全部" clearable>
                        <el-option label="用户" value="用户" />
                        <el-option label="设备" value="设备" />
                        <el-option label="预约" value="预约" />
                        <el-option label="考勤" value="考勤" />
                        <el-option label="实验室" value="实验室" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList">查询</el-button>
                    <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="pagedData" border>
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="userName" label="操作人" width="120" />
                <el-table-column prop="module" label="模块" width="100">
                    <template #default="{ row }">
                        <el-tag size="small" :type="moduleTagType(row.module)">{{ row.module }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="action" label="操作" width="120" />
                <el-table-column prop="detail" label="详情" show-overflow-tooltip />
                <el-table-column prop="ip" label="IP地址" width="140" />
                <el-table-column prop="createTime" label="操作时间" width="180">
                    <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
                </el-table-column>
            </el-table>
            <el-pagination
                style="margin-top: 15px; justify-content: flex-end;"
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[20, 50, 100]"
                :total="tableData.length"
                layout="total, sizes, prev, pager, next"
            />
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { logApi, exportApi } from '@/api'
import dayjs from 'dayjs'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const pagedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return tableData.value.slice(start, start + pageSize.value)
})

const queryForm = ref({
    userName: '',
    module: ''
})

const formatTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'

const moduleTagType = (module) => {
    const map = { '用户': '', '设备': 'success', '预约': 'warning', '考勤': 'info', '实验室': 'danger' }
    return map[module] || 'info'
}

const loadList = async () => {
    try {
        tableData.value = await logApi.getList(queryForm.value)
        currentPage.value = 1
    } catch (error) {
        console.error(error)
    }
}

const resetQuery = () => {
    queryForm.value = { userName: '', module: '' }
    loadList()
}

const handleExport = () => {
    window.open(exportApi.operationLog(queryForm.value), '_blank')
}

onMounted(() => loadList())
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
