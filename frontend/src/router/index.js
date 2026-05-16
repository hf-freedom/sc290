import { createRouter, createWebHistory } from 'vue-router'
import DashboardPage from '../views/DashboardPage.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: DashboardPage,
    meta: { title: '首页仪表盘' }
  },
  {
    path: '/account',
    name: 'Account',
    component: () => import('../views/AccountPage.vue'),
    meta: { title: '会员账户' }
  },
  {
    path: '/recharge',
    name: 'Recharge',
    component: () => import('../views/RechargePage.vue'),
    meta: { title: '充值' }
  },
  {
    path: '/consume',
    name: 'Consume',
    component: () => import('../views/ConsumePage.vue'),
    meta: { title: '消费' }
  },
  {
    path: '/refund',
    name: 'Refund',
    component: () => import('../views/RefundPage.vue'),
    meta: { title: '退款' }
  },
  {
    path: '/risk',
    name: 'Risk',
    component: () => import('../views/RiskPage.vue'),
    meta: { title: '风控管理' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 会员储值消费系统` : '会员储值消费系统'
  next()
})

export default router
