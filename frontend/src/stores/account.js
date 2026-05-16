import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { accountApi, dashboardApi } from '../api'

export const useAccountStore = defineStore('account', () => {
  const accountInfo = ref({
    id: 1,
    name: '张三',
    phone: '13800138000',
    balance: 5000.00,
    giftBalance: 500.00,
    points: 12500,
    level: '黄金会员',
    levelIcon: 'Gold',
    memberSince: '2023-06-15',
    status: 'active'
  })

  const coupons = ref([
    { id: 1, name: '新人优惠券', amount: 50, type: 'discount', status: 'unused', expireDate: '2024-12-31' },
    { id: 2, name: '充值返利券', amount: 100, type: 'cash', status: 'unused', expireDate: '2024-11-30' },
    { id: 3, name: '满减券', amount: 20, type: 'discount', status: 'used', expireDate: '2024-10-15' }
  ])

  const notifications = ref([
    { id: 1, title: '充值返利活动', message: '充值满500送50，多充多送！', time: '2024-01-15 10:30', read: false },
    { id: 2, title: '新会员权益', message: '黄金会员新增专属折扣权益', time: '2024-01-14 15:20', read: false },
    { id: 3, title: '积分到期提醒', message: '您有500积分即将过期', time: '2024-01-13 09:00', read: true }
  ])

  const transactions = ref([
    { id: 1, type: 'recharge', amount: 1000, balance: 5000, giftBalance: 500, time: '2024-01-15 14:20', description: '充值' },
    { id: 2, type: 'consume', amount: -150, balance: 4500, giftBalance: 450, time: '2024-01-15 15:30', description: '购买商品' },
    { id: 3, type: 'refund', amount: 200, balance: 4700, giftBalance: 470, time: '2024-01-14 10:15', description: '订单退款' }
  ])

  const totalBalance = computed(() => accountInfo.value.balance + accountInfo.value.giftBalance)

  const asyncFetchAccountInfo = async (id) => {
    try {
      const data = await accountApi.getAccountInfo(id)
      accountInfo.value = data
    } catch (error) {
      console.error('获取账户信息失败', error)
    }
  }

  const asyncFetchCoupons = async () => {
    try {
      const data = await accountApi.getAccountDetails(accountInfo.value.id)
      coupons.value = data.coupons || []
    } catch (error) {
      console.error('获取优惠券失败', error)
    }
  }

  const asyncFetchTransactions = async (params) => {
    try {
      const data = await accountApi.getAccountTransactions(accountInfo.value.id, params)
      transactions.value = data.list || []
    } catch (error) {
      console.error('获取交易记录失败', error)
    }
  }

  const updateAccount = (data) => {
    accountInfo.value = { ...accountInfo.value, ...data }
  }

  const addNotification = (notification) => {
    notifications.value.unshift({
      id: Date.now(),
      ...notification,
      time: new Date().toLocaleString('zh-CN'),
      read: false
    })
  }

  return {
    accountInfo,
    coupons,
    notifications,
    transactions,
    totalBalance,
    asyncFetchAccountInfo,
    asyncFetchCoupons,
    asyncFetchTransactions,
    updateAccount,
    addNotification
  }
})
