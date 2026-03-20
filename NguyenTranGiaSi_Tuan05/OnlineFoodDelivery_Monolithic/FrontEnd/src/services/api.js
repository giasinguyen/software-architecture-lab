import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

export const foodApi = {
  getAll: () => api.get('/foods'),
  getAllAdmin: () => api.get('/foods/all'),
  create: (data) => api.post('/foods', data),
  toggle: (id) => api.patch(`/foods/${id}/toggle`),
}

export const orderApi = {
  place: (data) => api.post('/orders', data),
  getByUser: (userId) => api.get(`/orders/user/${userId}`),
  getAll: () => api.get('/orders'),
  getById: (id) => api.get(`/orders/${id}`),
  updateStatus: (id, status) => api.patch(`/orders/${id}/status`, { status }),
}

export const voucherApi = {
  validate: (code, orderTotal) =>
    api.get('/vouchers/validate', { params: { code, orderTotal } }),
}

export const notificationApi = {
  getByUser: (userId) => api.get(`/notifications/user/${userId}`),
  markAllRead: (userId) => api.patch(`/notifications/user/${userId}/read-all`),
}

export const recommendationApi = {
  getPopular: (limit = 6) => api.get('/recommendations/popular', { params: { limit } }),
  getPersonalized: (userId, limit = 6) =>
    api.get(`/recommendations/user/${userId}`, { params: { limit } }),
}

export const userApi = {
  getAll: () => api.get('/users'),
}

export default api
