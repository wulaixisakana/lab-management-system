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

request.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        if (userStore.token) {
            config.headers.Authorization = `Bearer ${userStore.token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
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
        ElMessage.error(error.message || 'Network error')
        return Promise.reject(error)
    }
)

export default request
