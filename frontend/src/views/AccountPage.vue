<template>
  <div class="account-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">会员账户</h1>
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="account-card">
            <template #header>
              <div class="card-header">
                <span>账户信息</span>
                <el-tag :type="accountInfo.status === 'active' ? 'success' : 'danger'">
                  {{ getStatusText(accountInfo.status) }}
                </el-tag>
              </div>
            </template>
            <div class="account-info">
              <div class="avatar-section">
                <el-avatar :size="80" :icon="UserFilled" />
                <h3>{{ accountInfo.name }}</h3>
                <el-tag type="warning" size="large">{{ accountInfo.level }}</el-tag>
              </div>
              <div class="info-list">
                <div class="info-row">
                  <span class="label">会员编号：</span>
                  <span class="value">{{ accountInfo.id }}</span>
                </div>
                <div class="info-row">
                  <span class="label">手机号码：</span>
                  <span class="value">{{ accountInfo.phone }}</span>
                </div>
                <div class="info-row">
                  <span class="label">入会时间：</span>
                  <span class="value">{{ accountInfo.memberSince }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-card class="balance-card" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>资产概览</span>
              </div>
            </template>
            <div class="balance-list">
              <div class="balance-item">
                <div class="balance-label">账户余额</div>
                <div class="balance-value primary">¥{{ accountInfo.balance.toFixed(2) }}</div>
              </div>
              <div class="balance-item">
                <div class="balance-label">赠送金余额</div>
                <div class="balance-value success">¥{{ accountInfo.giftBalance.toFixed(2) }}</div>
              </div>
              <div class="balance-item">
                <div class="balance-label">总积分</div>
                <div class="balance-value warning">{{ accountInfo.points.toLocaleString() }}</div>
              </div>
              <el-divider />
              <div class="balance-item total">
                <div class="balance-label">总资产</div>
                <div class="balance-value danger">¥{{ totalBalance.toFixed(2) }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="16">
          <el-tabs v-model="activeTab" type="border-card">
            <el-tab-pane label="优惠券" name="coupons">
              <div class="coupons-section">
                <div class="coupons-filter">
                  <el-radio-group v-model="couponFilter" size="default">
                    <el-radio-button label="all">全部</el-radio-button>
                    <el-radio-button label="unused">未使用</el-radio-button>
                    <el-radio-button label="used">已使用</el-radio-button>
                    <el-radio-button label="expired">已过期</el-radio-button>
                  </el-radio-group>
                </div>
                <el-row :gutter="15">
                  <el-col
                    v-for="coupon in filteredCoupons"
                    :key="coupon.id"
                    :span="12"
                  >
                    <el-card
                      class="coupon-card"
                      :class="{ 'coupon-used': coupon.status === 'used', 'coupon-expired': coupon.status === 'expired' }"
                      shadow="hover"
                    >
                      <div class="coupon-content">
                        <div class="coupon-left">
                          <div class="coupon-amount">
                            <span class="symbol">¥</span>
                            <span class="number">{{ coupon.amount }}</span>
                          </div>
                          <div class="coupon-type">{{ getCouponTypeText(coupon.type) }}</div>
                        </div>
                        <div class="coupon-right">
                          <div class="coupon-name">{{ coupon.name }}</div>
                          <div class="coupon-expire">
                            <el-icon><Clock /></el-icon>
                            {{ coupon.expireDate }} 到期
                          </div>
                          <el-tag
                            :type="getCouponStatusType(coupon.status)"
                            size="small"
                          >
                            {{ getCouponStatusText(coupon.status) }}
                          </el-tag>
                        </div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
                <el-empty v-if="filteredCoupons.length === 0" description="暂无优惠券" />
              </div>
            </el-tab-pane>

            <el-tab-pane label="交易记录" name="transactions">
              <div class="transactions-section">
                <div class="transactions-filter">
                  <el-select v-model="transactionFilter" placeholder="筛选交易类型" style="width: 150px;">
                    <el-option label="全部" value="all" />
                    <el-option label="充值" value="recharge" />
                    <el-option label="消费" value="consume" />
                    <el-option label="退款" value="refund" />
                  </el-select>
                  <el-date-picker
                    v-model="transactionDateRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    style="margin-left: 10px;"
                  />
                </div>
                <el-table :data="filteredTransactions" style="width: 100%; margin-top: 15px;" stripe>
                  <el-table-column prop="time" label="时间" width="160" />
                  <el-table-column prop="type" label="类型" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getTransactionTypeTag(row.type)">
                        {{ getTransactionTypeText(row.type) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="description" label="描述" />
                  <el-table-column prop="balance" label="余额" width="120">
                    <template #default="{ row }">
                      ¥{{ row.balance.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="giftBalance" label="赠送金" width="120">
                    <template #default="{ row }">
                      ¥{{ row.giftBalance.toFixed(2) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="amount" label="金额" width="120">
                    <template #default="{ row }">
                      <span :class="row.amount > 0 ? 'amount-positive' : 'amount-negative'">
                        {{ row.amount > 0 ? '+' : '' }}¥{{ row.amount.toFixed(2) }}
                      </span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>

            <el-tab-pane label="会员等级" name="levels">
              <div class="levels-section">
                <el-alert
                  title="会员等级说明"
                  type="info"
                  :closable="false"
                  style="margin-bottom: 20px;"
                >
                  会员等级根据累计充值金额划分，等级越高享受的折扣和权益越多。
                </el-alert>
                <el-timeline>
                  <el-timeline-item
                    v-for="(level, index) in memberLevels"
                    :key="index"
                    :icon="getLevelIcon(level.level)"
                    :color="level.color"
                    :type="isCurrentLevel(level.level) ? 'primary' : 'info'"
                  >
                    <el-card class="level-card">
                      <div class="level-header">
                        <h4>{{ level.level }}</h4>
                        <el-tag :color="level.color" type="info">
                          消费享受 {{ ((1 - level.discount) * 100).toFixed(0) }}% 优惠
                        </el-tag>
                      </div>
                      <div class="level-content">
                        <div class="level-item">
                          <span class="label">累计充值：</span>
                          <span class="value">¥{{ level.minAmount.toLocaleString() }}</span>
                        </div>
                        <div class="level-item">
                          <span class="label">折扣比例：</span>
                          <span class="value">{{ (level.discount * 100).toFixed(0) }}%</span>
                        </div>
                        <div class="level-item" v-if="isCurrentLevel(level.level)">
                          <span class="label">当前等级：</span>
                          <el-tag type="warning">✓</el-tag>
                        </div>
                      </div>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAccountStore } from '../stores/account'
import { useAppStore } from '../stores/app'
import { ElMessage } from 'element-plus'
import { UserFilled, Coin, Star, Medal } from '@element-plus/icons-vue'

const accountStore = useAccountStore()
const appStore = useAppStore()

const activeTab = ref('coupons')
const couponFilter = ref('all')
const transactionFilter = ref('all')
const transactionDateRange = ref([])

const accountInfo = computed(() => accountStore.accountInfo)
const totalBalance = computed(() => accountStore.totalBalance)
const coupons = computed(() => accountStore.coupons)
const transactions = computed(() => accountStore.transactions)
const memberLevels = computed(() => appStore.memberLevels)

const filteredCoupons = computed(() => {
  if (couponFilter.value === 'all') {
    return coupons.value
  }
  return coupons.value.filter(c => c.status === couponFilter.value)
})

const filteredTransactions = computed(() => {
  if (transactionFilter.value === 'all') {
    return transactions.value
  }
  return transactions.value.filter(t => t.type === transactionFilter.value)
})

const getStatusText = (status) => {
  const map = { active: '正常', frozen: '冻结', closed: '已关闭' }
  return map[status] || '未知'
}

const getCouponTypeText = (type) => {
  const map = { discount: '满减券', cash: '现金券', gift: '礼品券' }
  return map[type] || '优惠券'
}

const getCouponStatusText = (status) => {
  const map = { unused: '未使用', used: '已使用', expired: '已过期' }
  return map[status] || '未知'
}

const getCouponStatusType = (status) => {
  const map = { unused: 'success', used: 'info', expired: 'danger' }
  return map[status] || 'info'
}

const getTransactionTypeTag = (type) => {
  const map = { recharge: 'success', consume: 'warning', refund: 'info' }
  return map[type] || 'info'
}

const getTransactionTypeText = (type) => {
  const map = { recharge: '充值', consume: '消费', refund: '退款' }
  return map[type] || '其他'
}

const getLevelIcon = (level) => {
  if (level === '钻石会员') return Star
  if (level === '黄金会员') return Medal
  return Coin
}

const isCurrentLevel = (level) => {
  return level === accountInfo.value.level
}

const handleRefresh = () => {
  ElMessage.success('账户信息已刷新')
  accountStore.asyncFetchAccountInfo(accountInfo.value.id)
  accountStore.asyncFetchCoupons()
  accountStore.asyncFetchTransactions()
}
</script>

<style scoped>
.account-container {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.account-card .avatar-section {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.account-card .avatar-section h3 {
  margin: 15px 0 10px;
  font-size: 20px;
}

.account-card .info-list {
  padding-top: 15px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  color: #909399;
}

.value {
  color: #303133;
  font-weight: 500;
}

.balance-list .balance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.balance-item.total {
  padding-top: 15px;
}

.balance-label {
  color: #606266;
  font-size: 14px;
}

.balance-value {
  font-size: 20px;
  font-weight: bold;
}

.balance-value.primary { color: #409eff; }
.balance-value.success { color: #67c23a; }
.balance-value.warning { color: #e6a23c; }
.balance-value.danger { color: #f56c6c; font-size: 24px; }

.coupons-filter {
  margin-bottom: 20px;
}

.coupon-card {
  margin-bottom: 15px;
  border-left: 4px solid #409eff;
}

.coupon-card.coupon-used {
  border-left-color: #909399;
  opacity: 0.6;
}

.coupon-card.coupon-expired {
  border-left-color: #f56c6c;
  opacity: 0.6;
}

.coupon-content {
  display: flex;
  gap: 20px;
}

.coupon-left {
  flex: 0 0 100px;
  text-align: center;
  padding: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
}

.coupon-amount .symbol {
  font-size: 16px;
}

.coupon-amount .number {
  font-size: 32px;
  font-weight: bold;
}

.coupon-type {
  font-size: 12px;
  margin-top: 5px;
}

.coupon-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.coupon-name {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.coupon-expire {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
}

.transactions-filter {
  display: flex;
  gap: 10px;
}

.level-card {
  margin-bottom: 10px;
}

.level-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.level-header h4 {
  margin: 0;
  font-size: 16px;
}

.level-content {
  display: flex;
  gap: 20px;
}

.level-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.amount-positive {
  color: #67c23a;
  font-weight: 600;
}

.amount-negative {
  color: #f56c6c;
  font-weight: 600;
}
</style>
