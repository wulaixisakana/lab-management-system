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
    reject: (id) => request.post(`/reservation/${id}/reject`),
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

export const userApi = {
    getList: () => request.get('/user/list'),
    update: (data) => request.post('/user/update', data),
    delete: (id) => request.delete(`/user/${id}`)
}
