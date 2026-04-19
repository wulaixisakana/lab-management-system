<template>
    <el-container class="main-container">
        <el-aside width="200px" class="aside">
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
                <el-menu-item index="/attendance">
                    <el-icon><Clock /></el-icon>
                    <span>考勤管理</span>
                </el-menu-item>
                <el-menu-item index="/users" v-if="userStore.user?.role === 'admin'">
                    <el-icon><User /></el-icon>
                    <span>用户管理</span>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <el-container>
            <el-header class="header">
                <div class="header-left">
                    <h2>{{ currentTitle }}</h2>
                </div>
                <div class="header-right">
                    <el-dropdown @command="handleCommand">
                        <span class="user-info">
                            <el-icon><UserFilled /></el-icon>
                            {{ userStore.user?.realName }}
                            <el-icon><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
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
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '实验室管理系统')

const handleCommand = async (command) => {
    if (command === 'logout') {
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
    background: #304156;
    color: #fff;
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
}

.menu {
    border: none;
    background: #304156;
}

.menu .el-menu-item {
    color: #bfcbd9;
}

.menu .el-menu-item:hover,
.menu .el-menu-item.is-active {
    background: #263445;
    color: #409eff;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    border-bottom: 1px solid #e4e7ed;
    padding: 0 20px;
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
    background: #f5f7fa;
    padding: 20px;
}
</style>
