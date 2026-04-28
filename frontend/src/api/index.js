import request from '@/utils/request'

export const authApi = {
    login: (data) => request.post('/user/login', data),
    register: (data) => request.post('/user/register', data),
    getInfo: () => request.get('/user/info')
}

export const equipmentApi = {
    getList: (params) => request.get('/equipment/list', { params }),
    getById: (id) => request.get(`/equipment/${id}`),
    save: (data) => request.post('/equipment/save', data),
    update: (data) => request.post('/equipment/update', data),
    delete: (id) => request.delete(`/equipment/${id}`)
}

export const laboratoryApi = {
    getList: (params) => request.get('/laboratory/list', { params }),
    getById: (id) => request.get(`/laboratory/${id}`),
    save: (data) => request.post('/laboratory/save', data),
    update: (data) => request.post('/laboratory/update', data),
    delete: (id) => request.delete(`/laboratory/${id}`)
}

export const reservationApi = {
    getList: (params) => request.get('/reservation/list', { params }),
    getMy: () => request.get('/reservation/my'),
    getById: (id) => request.get(`/reservation/${id}`),
    create: (data) => request.post('/reservation/create', data),
    approve: (id) => request.post(`/reservation/${id}/approve`),
    reject: (id, data) => request.post(`/reservation/${id}/reject`, data),
    cancel: (id) => request.post(`/reservation/${id}/cancel`),
    delete: (id) => request.delete(`/reservation/${id}`)
}

export const attendanceApi = {
    getList: (params) => request.get('/attendance/list', { params }),
    getMy: () => request.get('/attendance/my'),
    getToday: () => request.get('/attendance/today'),
    checkIn: () => request.post('/attendance/checkin'),
    checkOut: () => request.post('/attendance/checkout')
}

export const statisticsApi = {
    getOverview: () => request.get('/statistics/overview')
}

export const labReservationApi = {
    getList: (params) => request.get('/labReservation/list', { params }),
    getMy: () => request.get('/labReservation/my'),
    create: (data) => request.post('/labReservation/create', data),
    approve: (id) => request.post(`/labReservation/${id}/approve`),
    reject: (id, data) => request.post(`/labReservation/${id}/reject`, data),
    cancel: (id) => request.post(`/labReservation/${id}/cancel`),
    delete: (id) => request.delete(`/labReservation/${id}`)
}

export const logApi = {
    getList: (params) => request.get('/log/list', { params })
}

export const notificationApi = {
    getList: () => request.get('/notification/list'),
    markAsRead: (id) => request.post(`/notification/${id}/read`),
    markAllAsRead: () => request.post('/notification/readAll')
}

export const exportApi = {
    equipment: (params) => `/api/export/equipment?${new URLSearchParams(params).toString()}`,
    reservation: (params) => `/api/export/reservation?${new URLSearchParams(params).toString()}`,
    attendance: (params) => `/api/export/attendance?${new URLSearchParams(params).toString()}`,
    labReservation: (params) => `/api/export/labReservation?${new URLSearchParams(params).toString()}`,
    operationLog: (params) => `/api/export/operationLog?${new URLSearchParams(params).toString()}`
}

export const userApi = {
    getList: () => request.get('/user/list'),
    update: (data) => request.post('/user/update', data),
    delete: (id) => request.delete(`/user/${id}`),
    updateProfile: (data) => request.post('/user/profile', data),
    changePassword: (data) => request.post('/user/changePassword', data)
}
