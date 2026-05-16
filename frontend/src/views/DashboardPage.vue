<template>
  <div class="dashboard-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">系统概览</h1>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
      </div>

      <div class="card-grid">
        <div class="stat-card">
          <div class="stat-label">会员总数</div>
          <div class="stat-value">{{ statistics.totalMembers.toLocaleString() }}</div>
          <div class="stat-change">↑ {{ statistics.monthlyGrowth }}% 较上月</div>
        </div>
        <div class="stat-card success">
          <div class="stat-label">今日交易</div>
          <div class="stat-value">{{ statistics.todayTransactions }}</div>
          <div class="stat-change">笔</div>
        </div>
        <div class="stat-card warning">
          <div class="stat-label">账户总余额</div>
          <div class="stat-value">¥{{ formatAmount(statistics.totalBalance) }}</div>
          <div class="stat-change">元</div>
        </div>
        <div class="stat-card info">
          <div class="stat-label">风控账户</div>
          <div class="stat-value">{{ statistics.riskAccounts }}</div>
          <div class="stat-change">个</div>
        </div>
      </div>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="24">
          <div class="section-title">
            <el-icon><Lightning /></el-icon>
            快速操作
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col
          v-for="action in quickActions"
          :key="action.id"
          :xs="12"
          :sm="6"
        >
          <div
            class="action-card"
            @click="handleQuickAction(action)"
          >
            <div class="action-icon" :style="{ color: action.color }">
              <el-icon :size="40"><component :is="action.icon" /></el-icon>
            </div>
            <div class="action-name">{{ action.name }}</div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :xs="24" :lg="12">
          <div class="table-container">
            <div class="section-title">
              <el-icon><Clock /></el-icon>
              最近交易
            </div>
            <el-table :data="recentTransactions" style="width: 100%">
              <el-table-column prop="time" label="时间" width="160" />
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="getTransactionTypeTag(row.type)">
                    {{ getTransactionTypeText(row.type) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="120">
                <template #default="{ row }">
                  <span :class="row.amount > 0 ? 'amount-positive' : 'amount-negative'">
                    {{ row.amount > 0 ? '+' : '' }}{{ row.amount.toFixed(2) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" />
            </el-table>
          </div>
        </el-col>

        <el-col :xs="24" :lg="12">
          <div class="table-container">
            <div class="section-title">
              <el-icon><Bell /></el-icon>
              系统通知
            </div>
            <el-list>
              <el-list-item v-for="notification in notifications" :key="notification.id">
                <div class="notification-item">
                  <div class="notification-content">
                    <div class="notification-title">{{ notification.title }}</div>
                    <div class="notification-message">{{ notification.message }}</div>
                    <div class="notification-time">{{ notification.time }}</div>
                  </div>
                  <el-badge
                    v-if="!notification.read"
                    is-dot
                    class="notification-badge"
                  />
                </div>
              </el-list-item>
            </el-list>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <div class="table-container">
            <div class="section-title">
              <el-icon><TrendCharts /></el-icon>
              账户状态概览
            </div>
            <el-descriptions :column="3" border>
              <el-descriptions-item label="账户名称">
                {{ accountInfo.name }}
              </el-descriptions-item>
              <el-descriptions-item label="会员等级">
                <el-tag type="warning">{{ accountInfo.level }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="账户状态">
                <el-tag type="success">{{ getStatusText(accountInfo.status) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="账户余额">
                <span class="amount-positive">¥{{ accountInfo.balance.toFixed(2) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="赠送金余额">
                <span class="amount-positive">¥{{ accountInfo.giftBalance.toFixed(2) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="总积分">
                {{ accountInfo.points.toLocaleString() }}
              </el-descriptions-item>
              <el-descriptions-item label="入会时间">
                {{ accountInfo.memberSince }}
              </el-descriptions-item>
              <el-descriptions-item label="联系电话">
                {{ accountInfo.phone }}
              </el-descriptions-item>
              <el-descriptions-item label="总资产">
                <span class="amount-positive">¥{{ totalBalance.toFixed(2) }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore } from '../stores/account'
import { useAppStore } from '../stores/app'
import { ElMessage } from 'element-plus'

const router = useRouter()
const accountStore = useAccountStore()
const appStore = useAppStore()

const dateRange = ref([
  new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
  new Date()
])

const statistics = computed(() => appStore.statistics)
const quickActions = computed(() => appStore.quickActions)
const accountInfo = computed(() => accountStore.accountInfo)
const totalBalance = computed(() => accountStore.totalBalance)
const notifications = computed(() => accountStore.notifications)

const recentTransactions = computed(() => accountStore.transactions.slice(0, 5))

const formatAmount = (amount) => {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(2) + '万'
  }
  return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

const getTransactionTypeTag = (type) => {
  const typeMap = {
    recharge: 'success',
    consume: 'warning',
    refund: 'info',
    gift: 'primary'
  }
  return typeMap[type] || 'info'
}

const getTransactionTypeText = (type) => {
  const typeMap = {
    recharge: '充值',
    consume: '消费',
    refund: '退款',
    gift: '赠送'
  }
  return typeMap[type] || '其他'
}

const getStatusText = (status) => {
  const statusMap = {
    active: '正常',
    frozen: '冻结',
    closed: '已关闭'
  }
  return statusMap[status] || '未知'
}

const handleDateChange = (val) => {
  if (val) {
    ElMessage.success(`已筛选 ${val[0].toLocaleDateString()} 至 ${val[1].toLocaleDateString()} 的数据`)
  }
}

const handleQuickAction = (action) => {
  router.push(action.path)
}
</script>

<style scoped>
.dashboard-container {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  padding: 10px 0;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.notification-message {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.amount-positive {
  color: #67c23a;
  font-weight: 600;
}

.amount-negative {
  color: #f56c6c;
  font-weight: 600;
}

.action-name {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
</style>
