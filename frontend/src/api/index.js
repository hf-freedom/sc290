import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
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
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export const accountApi = {
  getAccountInfo: (id) => request.get(`/accounts/${id}`),
  getAccountList: (params) => request.get('/accounts', { params }),
  updateAccount: (id, data) => request.put(`/accounts/${id}`, data),
  getAccountDetails: (id) => request.get(`/accounts/${id}/details`),
  getAccountTransactions: (id, params) => request.get(`/accounts/${id}/transactions`, { params })
}

export const rechargeApi = {
  getRechargePlans: () => request.get('/recharge/plans'),
  createRecharge: (data) => request.post('/recharge', data),
  getRechargeRecords: (params) => request.get('/recharge/records', { params }),
  getRechargeDetails: (id) => request.get(`/recharge/${id}`),
  cancelRecharge: (id) => request.delete(`/recharge/${id}`)
}

export const consumeApi = {
  getProducts: (params) => request.get('/products', { params }),
  getProductDetails: (id) => request.get(`/products/${id}`),
  createOrder: (data) => request.post('/orders', data),
  getOrders: (params) => request.get('/orders', { params }),
  getOrderDetails: (id) => request.get(`/orders/${id}`),
  payOrder: (id, data) => request.post(`/orders/${id}/pay`, data),
  cancelOrder: (id) => request.delete(`/orders/${id}`)
}

export const refundApi = {
  getRefundOrders: (params) => request.get('/refunds', { params }),
  getRefundDetails: (id) => request.get(`/refunds/${id}`),
  applyRefund: (data) => request.post('/refunds/apply', data),
  approveRefund: (id, data) => request.put(`/refunds/${id}/approve`, data),
  rejectRefund: (id, data) => request.put(`/refunds/${id}/reject`, data)
}

export const riskApi = {
  getRiskAccounts: (params) => request.get('/risk/accounts', { params }),
  getRiskDetails: (id) => request.get(`/risk/${id}`),
  freezeAccount: (id, data) => request.post(`/risk/${id}/freeze`, data),
  unfreezeAccount: (id) => request.post(`/risk/${id}/unfreeze`),
  addRiskRecord: (id, data) => request.post(`/risk/${id}/records`, data),
  getRiskRecords: (id, params) => request.get(`/risk/${id}/records`, { params })
}

export const dashboardApi = {
  getStatistics: () => request.get('/dashboard/statistics'),
  getQuickActions: () => request.get('/dashboard/quick-actions'),
  getRecentTransactions: (params) => request.get('/dashboard/recent-transactions', { params }),
  getSystemStatus: () => request.get('/dashboard/system-status')
}

export default request
