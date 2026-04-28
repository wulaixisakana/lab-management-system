<template>
    <el-container class="main-container">
        <div class="mobile-overlay" v-if="sidebarOpen" @click="sidebarOpen = false"></div>
        <el-aside :width="sidebarOpen ? '200px' : '0px'" class="aside" :class="{ open: sidebarOpen }">
            <div class="logo">
                <el-icon><Microscope /></el-icon>
                <span>实验室管理</span>
            </div>
            <el-menu :default-active="activeMenu" router class="menu">
                <el-menu-item index="/dashboard">
                    <el-icon><House /></el-icon>
                    <span>首页</span>
                </el-menu-item>
                <el-menu-item index="/laboratory">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>实验室管理</span>
                </el-menu-item>
                <el-menu-item index="/equipment">
                    <el-icon><Setting /></el-icon>
                    <span>设备管理</span>
                </el-menu-item>
                <el-menu-item index="/reservation">
                    <el-icon><Calendar /></el-icon>
                    <span>设备预约</span>
                </el-menu-item>
                <el-menu-item index="/lab-reservation">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>实验室预约</span>
                </el-menu-item>
                <el-menu-item index="/attendance">
                    <el-icon><Clock /></el-icon>
                    <span>考勤管理</span>
                </el-menu-item>
                <el-menu-item index="/users" v-if="userStore.user?.role === 'admin'">
                    <el-icon><User /></el-icon>
                    <span>用户管理</span>
                </el-menu-item>
                <el-menu-item index="/logs" v-if="userStore.user?.role === 'admin'">
                    <el-icon><Document /></el-icon>
                    <span>操作日志</span>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <el-container>
            <el-header class="header">
                <div class="header-left">
                    <el-icon class="hamburger" @click="sidebarOpen = !sidebarOpen"><Fold /></el-icon>
                    <h2>{{ currentTitle }}</h2>
                </div>
                <div class="header-right">
                    <el-popover placement="bottom" :width="360" trigger="click" @show="loadNotifications">
                        <template #reference>
                            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
                                <el-icon class="notification-icon"><Bell /></el-icon>
                            </el-badge>
                        </template>
                        <div class="notification-header">
                            <span>消息通知</span>
                            <el-button link type="primary" size="small" @click="handleReadAll" v-if="unreadCount > 0">全部已读</el-button>
                        </div>
                        <el-scrollbar max-height="300px">
                            <div v-if="notifications.length === 0" style="text-align:center;padding:20px;color:#909399;">暂无消息</div>
                            <div v-for="item in notifications" :key="item.id"
                                class="notification-item" :class="{ unread: !item.isRead }"
                                @click="handleReadOne(item)">
                                <div class="notification-title">{{ item.title }}</div>
                                <div class="notification-content">{{ item.content }}</div>
                                <div class="notification-time">{{ formatTime(item.createTime) }}</div>
                            </div>
                        </el-scrollbar>
                    </el-popover>
                    <el-dropdown @command="handleCommand">
                        <span class="user-info">
                            <el-icon><UserFilled /></el-icon>
                            {{ userStore.user?.realName }}
                            <el-icon><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
            </el-header>

            <el-main class="main">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { notificationApi } from '@/api'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '实验室管理系统')

const sidebarOpen = ref(window.innerWidth > 768)

watch(() => route.path, () => {
    if (window.innerWidth <= 768) sidebarOpen.value = false
})

const notifications = ref([])
const unreadCount = ref(0)
let pollTimer = null

const formatTime = (time) => dayjs(time).format('MM-DD HH:mm')

const loadNotifications = async () => {
    try {
        const data = await notificationApi.getList()
        notifications.value = data.notifications || []
        unreadCount.value = data.unreadCount || 0
    } catch (e) {
        console.error(e)
    }
}

const handleReadOne = async (item) => {
    if (!item.isRead) {
        await notificationApi.markAsRead(item.id)
        item.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
    }
}

const handleReadAll = async () => {
    await notificationApi.markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
}

onMounted(() => {
    loadNotifications()
    pollTimer = setInterval(loadNotifications, 30000)
})

onBeforeUnmount(() => {
    if (pollTimer) clearInterval(pollTimer)
})

const handleCommand = async (command) => {
    if (command === 'profile') {
        router.push('/profile')
    } else if (command === 'logout') {
        try {
            await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
            userStore.logout()
            router.push('/login')
        } catch {
            // 取消操作
        }
    }
}
</script>

<style scoped>
.main-container {
    height: 100vh;
}

.aside {
    background: linear-gradient(180deg, #1d2b3a 0%, #2c3e50 100%);
    color: #fff;
    box-shadow: 2px 0 8px rgba(0,0,0,0.15);
}

.logo {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 60px;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    gap: 8px;
    background: rgba(0,0,0,0.15);
    letter-spacing: 2px;
}

.menu {
    border: none;
    background: transparent;
}

.menu .el-menu-item {
    color: #bfcbd9;
    margin: 4px 8px;
    border-radius: 6px;
    transition: all 0.3s;
}

.menu .el-menu-item:hover {
    background: rgba(255,255,255,0.08);
    color: #fff;
}

.menu .el-menu-item.is-active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-bottom: 1px solid #ebeef5;
    padding: 0 24px;
    box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.header-left h2 {
    margin: 0;
    font-size: 18px;
    color: #303133;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    color: #606266;
}

.main {
    background: #f0f2f5;
    padding: 20px;
}

.notification-badge {
    margin-right: 20px;
    cursor: pointer;
}

.notification-icon {
    font-size: 20px;
    color: #606266;
}

.notification-icon:hover {
    color: #409eff;
}

.notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 10px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 8px;
    font-weight: bold;
}

.notification-item {
    padding: 10px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    border-radius: 4px;
}

.notification-item:hover {
    background: #f5f7fa;
}

.notification-item.unread {
    background: #ecf5ff;
}

.notification-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
}

.notification-content {
    font-size: 12px;
    color: #606266;
    margin-top: 4px;
}

.notification-time {
    font-size: 11px;
    color: #909399;
    margin-top: 4px;
}

.hamburger {
    font-size: 22px;
    cursor: pointer;
    color: #606266;
    margin-right: 12px;
    display: none;
}

@media (max-width: 768px) {
    .hamburger {
        display: block;
    }

    .aside {
        position: fixed;
        left: -200px;
        top: 0;
        bottom: 0;
        z-index: 1000;
        transition: left 0.3s;
        width: 200px !important;
    }

    .aside.open {
        left: 0;
    }

    .mobile-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0,0,0,0.4);
        z-index: 999;
    }

    .header {
        padding: 0 12px;
    }

    .header-left h2 {
        font-size: 15px;
    }

    .main {
        padding: 12px;
    }

    .user-info span {
        display: none;
    }
}
</style>
