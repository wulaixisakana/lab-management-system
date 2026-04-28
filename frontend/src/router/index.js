import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: { title: '注册' }
    },
    {
        path: '/',
        component: () => import('@/layouts/MainLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/Dashboard.vue'),
                meta: { title: '首页' }
            },
            {
                path: 'laboratory',
                name: 'Laboratory',
                component: () => import('@/views/Laboratory.vue'),
                meta: { title: '实验室管理' }
            },
            {
                path: 'equipment',
                name: 'Equipment',
                component: () => import('@/views/Equipment.vue'),
                meta: { title: '设备管理' }
            },
            {
                path: 'reservation',
                name: 'Reservation',
                component: () => import('@/views/Reservation.vue'),
                meta: { title: '设备预约' }
            },
            {
                path: 'lab-reservation',
                name: 'LabReservation',
                component: () => import('@/views/LabReservation.vue'),
                meta: { title: '实验室预约' }
            },
            {
                path: 'attendance',
                name: 'Attendance',
                component: () => import('@/views/Attendance.vue'),
                meta: { title: '考勤管理' }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/Profile.vue'),
                meta: { title: '个人中心' }
            },
            {
                path: 'users',
                name: 'Users',
                component: () => import('@/views/Users.vue'),
                meta: { title: '用户管理', requiresAdmin: true }
            },
            {
                path: 'logs',
                name: 'OperationLog',
                component: () => import('@/views/OperationLog.vue'),
                meta: { title: '操作日志', requiresAdmin: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    if (to.path !== '/login' && to.path !== '/register' && !userStore.token) {
        next('/login')
    } else if (to.meta.requiresAdmin && userStore.user?.role !== 'admin') {
        next('/')
    } else {
        next()
    }
})

export default router
