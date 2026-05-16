import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isLoading = ref(false)
  const systemConfig = ref({
    companyName: '会员储值消费系统',
    version: '1.0.0',
    supportPhone: '400-888-8888',
    enableRiskControl: true,
    enableAutoRefund: false,
    minRechargeAmount: 100,
    maxRechargeAmount: 50000
  })

  const statistics = ref({
    totalMembers: 12580,
    todayTransactions: 356,
    totalBalance: 8560000.00,
    riskAccounts: 23,
    activeMembers: 9825,
    monthlyGrowth: 12.5
  })

  const rechargePlans = ref([
    {
      id: 1,
      amount: 500,
      gift: 50,
      bonus: 0.10,
      popular: false,
      giftDays: 30,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      id: 2,
      amount: 1000,
      gift: 100,
      bonus: 0.10,
      popular: true,
      giftDays: 30,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      id: 3,
      amount: 2000,
      gift: 250,
      bonus: 0.125,
      popular: false,
      giftDays: 60,
      scope: 'PARTIAL',
      scopeText: '指定品类'
    },
    {
      id: 4,
      amount: 5000,
      gift: 750,
      bonus: 0.15,
      popular: true,
      giftDays: 90,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      id: 5,
      amount: 10000,
      gift: 2000,
      bonus: 0.20,
      popular: false,
      giftDays: 180,
      scope: 'ALL',
      scopeText: '全部商品'
    }
  ])

  const memberLevels = ref([
    {
      level: '普通会员',
      icon: 'User',
      minAmount: 0,
      discount: 0.98,
      color: '#909399',
      giftDays: 30,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      level: '白银会员',
      icon: 'Coin',
      minAmount: 1000,
      discount: 0.95,
      color: '#C0C4CC',
      giftDays: 60,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      level: '黄金会员',
      icon: 'Medal',
      minAmount: 5000,
      discount: 0.92,
      color: '#E6A23C',
      giftDays: 90,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      level: '铂金会员',
      icon: 'Star',
      minAmount: 20000,
      discount: 0.88,
      color: '#909399',
      giftDays: 120,
      scope: 'ALL',
      scopeText: '全部商品'
    },
    {
      level: '钻石会员',
      icon: 'gem',
      minAmount: 50000,
      discount: 0.85,
      color: '#409EFF',
      giftDays: 180,
      scope: 'ALL',
      scopeText: '全部商品'
    }
  ])

  const quickActions = ref([
    { id: 1, name: '快速充值', icon: 'Coin', path: '/recharge', color: '#409EFF' },
    { id: 2, name: '消费结账', icon: 'ShoppingCart', path: '/consume', color: '#67C23A' },
    { id: 3, name: '查看账户', icon: 'User', path: '/account', color: '#E6A23C' },
    { id: 4, name: '申请退款', icon: 'RefreshLeft', path: '/refund', color: '#F56C6C' }
  ])

  const setLoading = (status) => {
    isLoading.value = status
  }

  const updateStatistics = (data) => {
    statistics.value = { ...statistics.value, ...data }
  }

  return {
    isLoading,
    systemConfig,
    statistics,
    rechargePlans,
    memberLevels,
    quickActions,
    setLoading,
    updateStatistics
  }
})
