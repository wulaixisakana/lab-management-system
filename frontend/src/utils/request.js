import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// Use relative path for API proxy (works on Cloudflare Pages and local dev)
const baseURL = '/api'

const request = axios.create({
    baseURL: baseURL,
    timeout: 10000
})

const pendingRequests = new Set()

request.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        if (config.method === 'post' || config.method === 'delete') {
            const key = `${config.method}:${config.url}:${JSON.stringify(config.data || '')}`
            if (pendingRequests.has(key)) {
                return Promise.reject(new Error('请勿重复提交'))
            }
            pendingRequests.add(key)
            config._dedupKey = key
            setTimeout(() => pendingRequests.delete(key), 2000)
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        if (response.config._dedupKey) {
            pendingRequests.delete(response.config._dedupKey)
        }
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || 'Request failed')
            if (res.code === 401) {
                const userStore = useUserStore()
                userStore.logout()
                router.push('/login')
            }
            return Promise.reject(new Error(res.message || 'Request failed'))
        }
        return res.data
    },
    error => {
        if (error.config && error.config._dedupKey) {
            pendingRequests.delete(error.config._dedupKey)
        }
        const status = error.response?.status
        if (status === 401 || status === 403) {
            ElMessage.error(status === 401 ? '登录已过期，请重新登录' : '无权限执行此操作')
            const userStore = useUserStore()
            userStore.logout()
            router.push('/login')
        } else {
            ElMessage.error(error.response?.data?.message || error.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

export default request
