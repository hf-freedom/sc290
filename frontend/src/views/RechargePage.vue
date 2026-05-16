<template>
  <div class="recharge-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">充值</h1>
        <el-tag type="success" size="large">
          当前余额：¥{{ accountStore.accountInfo.balance.toFixed(2) }}
        </el-tag>
      </div>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>选择充值金额</span>
              </div>
            </template>
            <div class="recharge-plans">
              <el-radio-group v-model="selectedPlan" @change="handlePlanChange">
                <el-radio-button
                  v-for="plan in rechargePlans"
                  :key="plan.id"
                  :value="plan.id"
                >
                  <div class="plan-content">
                    <div class="plan-amount">¥{{ plan.amount }}</div>
                    <div class="plan-gift" v-if="plan.gift > 0">
                      送 ¥{{ plan.gift }}
                    </div>
                    <div class="plan-info" v-if="plan.gift > 0">
                      <el-tooltip :content="`有效期${plan.giftDays}天，可用于${plan.scopeText}`" placement="top">
                        <el-icon><InfoFilled /></el-icon>
                        <span>{{ plan.giftDays }}天有效期</span>
                      </el-tooltip>
                    </div>
                    <el-tag v-if="plan.popular" type="danger" size="small" style="margin-top: 5px;">
                      推荐
                    </el-tag>
                  </div>
                </el-radio-button>
              </el-radio-group>
            </div>

            <el-divider>自定义金额</el-divider>

            <el-form :model="customForm" label-width="100px">
              <el-form-item label="充值金额">
                <el-input-number
                  v-model="customForm.amount"
                  :min="100"
                  :max="50000"
                  :step="100"
                  @change="handleCustomAmountChange"
                />
                <span class="form-tip">最低充值 ¥100，最高 ¥50,000</span>
              </el-form-item>

              <el-form-item label="支付方式">
                <el-radio-group v-model="customForm.paymentMethod">
                  <el-radio label="wechat">微信支付</el-radio>
                  <el-radio label="alipay">支付宝</el-radio>
                  <el-radio label="bankcard">银行卡</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-form>

            <div class="recharge-summary">
              <el-alert
                title="充值优惠详情"
                type="success"
                :closable="false"
                show-icon
              >
                <template #default>
                  <div v-if="selectedPlanData && selectedPlanData.gift > 0" class="gift-details">
                    <div class="gift-summary">
                      充值 <strong>¥{{ selectedPlanData.amount }}</strong>，
                      赠送 <strong>¥{{ selectedPlanData.gift }}</strong>
                    </div>
                    <el-divider style="margin: 10px 0;" />
                    <div class="gift-info-grid">
                      <div class="info-item">
                        <span class="info-label">有效期：</span>
                        <span class="info-value">{{ selectedPlanData.giftDays }}天</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">使用范围：</span>
                        <span class="info-value">{{ selectedPlanData.scopeText }}</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">生效时间：</span>
                        <span class="info-value">立即生效</span>
                      </div>
                      <div class="info-item">
                        <span class="info-label">失效时间：</span>
                        <span class="info-value">{{ calculateExpireDate(selectedPlanData.giftDays) }}</span>
                      </div>
                    </div>
                    <div class="gift-note">
                      <el-icon><Warning /></el-icon>
                      <span>赠送金仅限指定范围内使用，不可提现，过期自动失效</span>
                    </div>
                    <div class="gift-total">
                      <span>实际到账：</span>
                      <span class="total-balance">¥{{ (selectedPlanData.amount + selectedPlanData.gift).toFixed(2) }}</span>
                      <span class="balance-detail">(余额 ¥{{ selectedPlanData.amount.toFixed(2) }} + 赠送金 ¥{{ selectedPlanData.gift.toFixed(2) }})</span>
                    </div>
                  </div>
                  <div v-else class="no-gift">
                    充值 <strong>¥{{ customForm.amount }}</strong>，
                    当前会员等级暂无赠送优惠，
                    <el-link type="primary" @click="checkUpgrade">升级会员享更多优惠</el-link>
                  </div>
                </template>
              </el-alert>

              <div class="summary-amount">
                <span>支付金额：</span>
                <span class="amount">¥{{ totalPayAmount.toFixed(2) }}</span>
              </div>
            </div>

            <div class="recharge-actions">
              <el-button type="primary" size="large" @click="handleRecharge" :loading="loading">
                立即充值
              </el-button>
            </div>
          </el-card>

          <el-card style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>会员等级优惠</span>
              </div>
            </template>
            <el-table :data="memberLevels" style="width: 100%">
              <el-table-column prop="level" label="会员等级" width="150">
                <template #default="{ row }">
                  <el-tag :color="row.color">{{ row.level }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="minAmount" label="累计门槛" width="150">
                <template #default="{ row }">
                  ¥{{ row.minAmount.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column prop="discount" label="消费折扣">
                <template #default="{ row }">
                  <span class="discount-text">{{ (row.discount * 100).toFixed(0) }}折</span>
                </template>
              </el-table-column>
              <el-table-column label="专属权益">
                <template #default="{ row }">
                  <el-tag
                    v-if="row.level === accountStore.accountInfo.level"
                    type="warning"
                  >
                    当前等级
                  </el-tag>
                  <span v-else-if="row.minAmount <= accountStore.totalBalance" class="can-upgrade">
                    可升级
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>充值记录</span>
                <el-button text type="primary" @click="loadRechargeRecords">
                  查看全部
                </el-button>
              </div>
            </template>
            <div class="recharge-records">
              <el-timeline>
                <el-timeline-item
                  v-for="record in rechargeRecords"
                  :key="record.id"
                  :type="record.status === 'success' ? 'success' : 'warning'"
                  :timestamp="record.time"
                  placement="top"
                >
                  <el-card shadow="hover" class="recharge-record-card">
                    <div class="record-content">
                      <div class="record-header">
                        <span class="record-amount">+¥{{ record.amount.toFixed(2) }}</span>
                        <el-tag
                          :type="record.status === 'success' ? 'success' : 'warning'"
                          size="small"
                        >
                          {{ record.status === 'success' ? '成功' : '处理中' }}
                        </el-tag>
                      </div>
                      <div class="record-detail" v-if="record.gift > 0">
                        赠送：¥{{ record.gift.toFixed(2) }}
                        <el-tooltip
                          :content="`有效期至${record.expireTime}，${record.scopeText}`"
                          placement="top"
                        >
                          <el-icon class="gift-info-icon"><InfoFilled /></el-icon>
                        </el-tooltip>
                      </div>
                      <div class="record-gift-info" v-if="record.gift > 0 && record.status === 'success'">
                        <el-tag size="small" type="info">
                          {{ record.giftDays }}天有效期
                        </el-tag>
                        <el-tag size="small" type="success">
                          {{ record.scopeText }}
                        </el-tag>
                      </div>
                      <div class="record-method">
                        {{ getPaymentMethodText(record.method) }}
                      </div>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-if="rechargeRecords.length === 0" description="暂无充值记录" />
            </div>
          </el-card>

          <el-card style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>充值须知</span>
              </div>
            </template>
            <div class="notice-content">
              <div class="notice-item">
                <el-icon><Clock /></el-icon>
                <span>充值金额即时到账</span>
              </div>
              <div class="notice-item">
                <el-icon><Ticket /></el-icon>
                <span>赠送金不可提现，可用于消费</span>
              </div>
              <div class="notice-item">
                <el-icon><Document /></el-icon>
                <span>充值后可开具发票</span>
              </div>
              <div class="notice-item">
                <el-icon><Warning /></el-icon>
                <span>如有疑问请联系客服</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-dialog
        v-model="paymentDialogVisible"
        title="确认支付"
        width="500px"
        :close-on-click-modal="false"
      >
        <div class="payment-content">
          <div class="payment-amount">
            <span>支付金额</span>
            <span class="amount">¥{{ totalPayAmount.toFixed(2) }}</span>
          </div>
          <div class="payment-details">
            <div class="detail-row" v-if="selectedPlanData && selectedPlanData.gift > 0">
              <span class="detail-label">充值金额：</span>
              <span class="detail-value">¥{{ selectedPlanData.amount.toFixed(2) }}</span>
            </div>
            <div class="detail-row gift-row" v-if="selectedPlanData && selectedPlanData.gift > 0">
              <span class="detail-label">赠送金额：</span>
              <span class="detail-value gift-value">+¥{{ selectedPlanData.gift.toFixed(2) }}</span>
            </div>
            <div class="gift-info-section" v-if="selectedPlanData && selectedPlanData.gift > 0">
              <el-divider content-position="left">赠送金说明</el-divider>
              <div class="gift-info-item">
                <el-icon><Clock /></el-icon>
                <span>有效期：<strong>{{ selectedPlanData.giftDays }}天</strong></span>
              </div>
              <div class="gift-info-item">
                <el-icon><Shop /></el-icon>
                <span>使用范围：<strong>{{ selectedPlanData.scopeText }}</strong></span>
              </div>
              <div class="gift-info-item">
                <el-icon><Calendar /></el-icon>
                <span>生效时间：<strong>立即生效</strong></span>
              </div>
              <div class="gift-info-item">
                <el-icon><Timer /></el-icon>
                <span>失效时间：<strong>{{ calculateExpireDate(selectedPlanData.giftDays) }}</strong></span>
              </div>
              <el-alert
                title="温馨提示"
                type="warning"
                :closable="false"
                show-icon
                style="margin-top: 15px;"
              >
                <template #default>
                  <div class="gift-warn-text">
                    赠送金仅限指定范围内使用，不可提现，过期自动失效
                  </div>
                </template>
              </el-alert>
            </div>
          </div>
          <div class="payment-qrcode">
            <div class="qrcode-placeholder">
              <el-icon :size="100"><Picture /></el-icon>
              <p>扫码支付</p>
            </div>
          </div>
          <div class="payment-method">
            使用 {{ getPaymentMethodText(customForm.paymentMethod) }} 扫码支付
          </div>
        </div>
        <template #footer>
          <el-button @click="paymentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment">支付完成</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAccountStore } from '../stores/account'
import { useAppStore } from '../stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'

const accountStore = useAccountStore()
const appStore = useAppStore()

const selectedPlan = ref(2)
const loading = ref(false)
const paymentDialogVisible = ref(false)

const customForm = ref({
  amount: 1000,
  paymentMethod: 'wechat'
})

const rechargePlans = computed(() => appStore.rechargePlans)
const memberLevels = computed(() => appStore.memberLevels)

const selectedPlanData = computed(() => {
  return rechargePlans.value.find(p => p.id === selectedPlan.value)
})

const totalPayAmount = computed(() => {
  if (selectedPlanData.value) {
    return selectedPlanData.value.amount
  }
  return customForm.value.amount
})

const rechargeRecords = ref([
  {
    id: 1,
    amount: 1000,
    gift: 100,
    giftDays: 30,
    scope: 'ALL',
    scopeText: '全部商品',
    expireTime: '2024-02-14 23:59:59',
    time: '2024-01-15 14:30',
    status: 'success',
    method: 'wechat'
  },
  {
    id: 2,
    amount: 500,
    gift: 50,
    giftDays: 30,
    scope: 'ALL',
    scopeText: '全部商品',
    expireTime: '2024-02-09 23:59:59',
    time: '2024-01-10 10:20',
    status: 'success',
    method: 'alipay'
  },
  {
    id: 3,
    amount: 2000,
    gift: 250,
    giftDays: 60,
    scope: 'PARTIAL',
    scopeText: '指定品类',
    expireTime: '2024-03-05 23:59:59',
    time: '2024-01-05 16:45',
    status: 'success',
    method: 'bankcard'
  }
])

const handlePlanChange = () => {
  if (selectedPlanData.value) {
    customForm.value.amount = selectedPlanData.value.amount
  }
}

const handleCustomAmountChange = () => {
  selectedPlan.value = null
}

const handleRecharge = async () => {
  try {
    await ElMessageBox.confirm(
      `确认充值 ¥${totalPayAmount.value}？`,
      '确认充值',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    paymentDialogVisible.value = true
  } catch {
    // 用户取消
  }
}

const confirmPayment = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const gift = selectedPlanData.value?.gift || 0
    const giftDays = selectedPlanData.value?.giftDays || 30
    const scope = selectedPlanData.value?.scope || 'ALL'
    const scopeText = selectedPlanData.value?.scopeText || '全部商品'
    
    const expireDate = new Date()
    expireDate.setDate(expireDate.getDate() + giftDays)
    
    const newRecord = {
      id: Date.now(),
      amount: totalPayAmount.value,
      gift: gift,
      giftDays: giftDays,
      scope: scope,
      scopeText: scopeText,
      expireTime: expireDate.toLocaleDateString('zh-CN') + ' 23:59:59',
      time: new Date().toLocaleString('zh-CN'),
      status: 'success',
      method: customForm.value.paymentMethod
    }
    rechargeRecords.value.unshift(newRecord)

    accountStore.updateAccount({
      balance: accountStore.accountInfo.balance + totalPayAmount.value + gift,
      giftBalance: accountStore.accountInfo.giftBalance + gift
    })

    paymentDialogVisible.value = false
    ElMessage.success('充值成功！')
  } catch (error) {
    ElMessage.error('充值失败，请重试')
  } finally {
    loading.value = false
  }
}

const loadRechargeRecords = () => {
  ElMessage.info('加载全部充值记录')
}

const getPaymentMethodText = (method) => {
  const map = {
    wechat: '微信支付',
    alipay: '支付宝',
    bankcard: '银行卡'
  }
  return map[method] || '未知'
}

const calculateExpireDate = (days) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toLocaleDateString('zh-CN') + ' 23:59:59'
}

const checkUpgrade = () => {
  ElMessage.info('继续充值达到升级门槛即可提升会员等级')
}
</script>

<style scoped>
.recharge-container {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header {
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recharge-plans {
  padding: 20px 0;
}

.plan-content {
  padding: 15px 30px;
  text-align: center;
}

.plan-amount {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.plan-gift {
  font-size: 14px;
  color: #67c23a;
  margin-top: 5px;
}

.form-tip {
  margin-left: 15px;
  color: #909399;
  font-size: 13px;
}

.recharge-summary {
  margin: 30px 0;
}

.summary-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  font-size: 18px;
}

.summary-amount .amount {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.recharge-actions {
  text-align: center;
  margin-top: 30px;
}

.discount-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 16px;
}

.can-upgrade {
  color: #67c23a;
  font-size: 13px;
}

.record-content {
  padding: 5px 0;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.record-amount {
  font-size: 18px;
  font-weight: bold;
  color: #67c23a;
}

.record-detail {
  font-size: 13px;
  color: #909399;
  margin-bottom: 3px;
}

.record-method {
  font-size: 12px;
  color: #c0c4cc;
}

.notice-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
}

.payment-content {
  text-align: center;
}

.payment-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 30px;
}

.payment-amount .amount {
  font-size: 32px;
  font-weight: bold;
  color: #f56c6c;
}

.payment-qrcode {
  margin-bottom: 20px;
}

.qrcode-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #f5f7fa;
  border-radius: 8px;
  color: #909399;
}

.payment-method {
  color: #606266;
  font-size: 14px;
}

.plan-info {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  cursor: pointer;
}

.plan-info:hover {
  color: #409eff;
}

.gift-details {
  padding: 5px 0;
}

.gift-summary {
  font-size: 15px;
  margin-bottom: 5px;
}

.gift-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  padding: 10px 0;
  background: #f5f7fa;
  border-radius: 4px;
  margin: 10px 0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
}

.info-label {
  color: #909399;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.gift-note {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #fdf6ec;
  border-radius: 4px;
  font-size: 13px;
  color: #e6a23c;
  margin: 10px 0;
}

.gift-note .el-icon {
  flex-shrink: 0;
}

.gift-total {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 10px;
  font-size: 16px;
  font-weight: 600;
}

.total-balance {
  color: #67c23a;
  font-size: 20px;
}

.balance-detail {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
}

.no-gift {
  text-align: center;
  padding: 10px;
}

.recharge-record-card {
  border-left: 3px solid #67c23a;
}

.record-gift-info {
  display: flex;
  gap: 5px;
  margin-top: 5px;
}

.gift-info-icon {
  margin-left: 5px;
  cursor: pointer;
  color: #409eff;
}

.payment-details {
  margin: 20px 0;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 15px;
  border-bottom: 1px solid #ebeef5;
}

.detail-label {
  color: #606266;
}

.detail-value {
  color: #303133;
  font-weight: 600;
}

.detail-row.gift-row {
  border-bottom: none;
  padding-bottom: 0;
}

.detail-value.gift-value {
  color: #67c23a;
  font-size: 17px;
}

.gift-info-section {
  margin-top: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.gift-info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
}

.gift-info-item .el-icon {
  color: #409eff;
}

.gift-warn-text {
  font-size: 13px;
  line-height: 1.5;
}
</style>
