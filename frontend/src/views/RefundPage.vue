<template>
  <div class="refund-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">退款管理</h1>
        <el-button type="primary" @click="showApplyDialog">
          <el-icon><Plus /></el-icon>
          申请退款
        </el-button>
      </div>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">待处理</div>
            <div class="stat-value">{{ pendingCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-label">已完成</div>
            <div class="stat-value">{{ completedCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-label">已拒绝</div>
            <div class="stat-value">{{ rejectedCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card info">
            <div class="stat-label">退款总额</div>
            <div class="stat-value">¥{{ totalRefundAmount.toFixed(2) }}</div>
          </div>
        </el-col>
      </el-row>

      <el-card>
        <template #header>
          <div class="card-header">
            <span>退款订单列表</span>
            <div class="header-actions">
              <el-select v-model="filterStatus" placeholder="筛选状态" style="width: 150px;">
                <el-option label="全部" value="all" />
                <el-option label="待处理" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
                <el-option label="已完成" value="completed" />
              </el-select>
              <el-date-picker
                v-model="filterDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="margin-left: 10px;"
              />
            </div>
          </div>
        </template>

        <el-table :data="filteredRefunds" style="width: 100%" stripe>
          <el-table-column prop="orderId" label="订单编号" width="150" />
          <el-table-column prop="orderTime" label="订单时间" width="160" />
          <el-table-column prop="amount" label="订单金额" width="120">
            <template #default="{ row }">
              ¥{{ row.amount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="原扣减路径" width="200">
            <template #default="{ row }">
              <el-tooltip content="查看扣减明细" placement="top">
                <el-tag size="small" type="info">
                  余额: ¥{{ row.originalDeduction?.balanceUsed?.toFixed(2) || '0.00' }}
                </el-tag>
              </el-tooltip>
              <el-tooltip content="查看扣减明细" placement="top">
                <el-tag size="small" type="success" style="margin-left: 5px;">
                  赠送: ¥{{ row.originalDeduction?.giftUsed?.toFixed(2) || '0.00' }}
                </el-tag>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="refundAmount" label="退款金额" width="120">
            <template #default="{ row }">
              <span class="refund-amount">¥{{ row.refundAmount.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="退款原因" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="160" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button
                size="small"
                type="primary"
                text
                @click="viewDetails(row)"
              >
                查看
              </el-button>
              <el-button
                v-if="row.status === 'pending'"
                size="small"
                type="success"
                text
                @click="handleApprove(row)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.status === 'pending'"
                size="small"
                type="danger"
                text
                @click="handleReject(row)"
              >
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog
        v-model="applyDialogVisible"
        title="申请退款"
        width="600px"
      >
        <el-form :model="applyForm" label-width="100px">
          <el-form-item label="订单编号">
            <el-input v-model="applyForm.orderId" placeholder="请输入订单编号" />
          </el-form-item>
          <el-form-item label="订单金额">
            <span class="form-value">¥{{ applyForm.amount.toFixed(2) }}</span>
          </el-form-item>
          <el-form-item label="退款金额">
            <el-input-number
              v-model="applyForm.refundAmount"
              :min="0.01"
              :max="applyForm.amount"
              :precision="2"
            />
          </el-form-item>
          <el-form-item label="退款原因">
            <el-select v-model="applyForm.reasonType" placeholder="请选择原因类型" style="width: 100%;">
              <el-option label="商品质量问题" value="quality" />
              <el-option label="商品与描述不符" value="mismatch" />
              <el-option label="错买/重买" value="mistake" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="详细说明">
            <el-input
              v-model="applyForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请详细描述退款原因"
            />
          </el-form-item>
          <el-form-item label="退款方式">
            <el-radio-group v-model="applyForm.refundMethod">
              <el-radio label="balance">退回余额</el-radio>
              <el-radio label="original">原路退回</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApply" :loading="loading">提交申请</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="detailsDialogVisible"
        title="退款详情"
        width="800px"
      >
        <div class="refund-details" v-if="currentRefund">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单编号">
              {{ currentRefund.orderId }}
            </el-descriptions-item>
            <el-descriptions-item label="订单时间">
              {{ currentRefund.orderTime }}
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              ¥{{ currentRefund.amount.toFixed(2) }}
            </el-descriptions-item>
            <el-descriptions-item label="退款金额">
              <span class="refund-amount">¥{{ currentRefund.refundAmount.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="退款原因" :span="2">
              {{ currentRefund.reason }}
            </el-descriptions-item>
            <el-descriptions-item label="退款方式">
              {{ currentRefund.refundMethod === 'balance' ? '退回余额' : '原路退回' }}
            </el-descriptions-item>
            <el-descriptions-item label="申请时间">
              {{ currentRefund.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="getStatusType(currentRefund.status)">
                {{ getStatusText(currentRefund.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="处理时间" v-if="currentRefund.processTime">
              {{ currentRefund.processTime }}
            </el-descriptions-item>
            <el-descriptions-item label="处理备注" :span="2" v-if="currentRefund.processNote">
              {{ currentRefund.processNote }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">原消费扣减路径</el-divider>
          <el-alert
            title="消费时的支付来源"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 15px;"
          >
            <template #default>
              <div class="original-deduction-detail">
                <div class="deduction-row">
                  <span class="deduction-icon">①</span>
                  <span class="deduction-label">账户余额支付：</span>
                  <span class="deduction-value">¥{{ currentRefund.originalDeduction?.balanceUsed?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="deduction-row">
                  <span class="deduction-icon">②</span>
                  <span class="deduction-label">赠送金支付：</span>
                  <span class="deduction-value">¥{{ currentRefund.originalDeduction?.giftUsed?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="deduction-row">
                  <span class="deduction-icon">③</span>
                  <span class="deduction-label">优惠券抵扣：</span>
                  <span class="deduction-value">¥{{ currentRefund.originalDeduction?.couponUsed?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="deduction-row" v-if="currentRefund.originalDeduction?.cashUsed > 0">
                  <span class="deduction-icon">④</span>
                  <span class="deduction-label">现金支付：</span>
                  <span class="deduction-value">¥{{ currentRefund.originalDeduction?.cashUsed?.toFixed(2) || '0.00' }}</span>
                </div>
                <el-divider style="margin: 10px 0;" />
                <div class="deduction-total">
                  <span>消费总额：</span>
                  <span class="total-amount">¥{{ currentRefund.amount.toFixed(2) }}</span>
                </div>
              </div>
            </template>
          </el-alert>

          <el-divider content-position="left" v-if="currentRefund.status === 'completed'">退款回退明细</el-divider>
          <el-alert
            v-if="currentRefund.status === 'completed'"
            title="退款将按原扣减路径回退"
            type="success"
            :closable="false"
            show-icon
            style="margin-bottom: 15px;"
          >
            <template #default>
              <div class="refund-deduction-detail">
                <div class="deduction-row">
                  <span class="deduction-icon">①</span>
                  <span class="deduction-label">余额回退：</span>
                  <span class="deduction-value refund-value">+¥{{ currentRefund.refundBreakdown?.balanceRefund?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="deduction-row">
                  <span class="deduction-icon">②</span>
                  <span class="deduction-label">赠送金回退：</span>
                  <span class="deduction-value refund-value">+¥{{ currentRefund.refundBreakdown?.giftRefund?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="deduction-row" v-if="currentRefund.refundBreakdown?.couponRefund?.length > 0">
                  <span class="deduction-icon">③</span>
                  <span class="deduction-label">优惠券恢复：</span>
                  <span class="deduction-value refund-value">{{ currentRefund.refundBreakdown?.couponRefund?.length }}张</span>
                </div>
                <el-divider style="margin: 10px 0;" />
                <div class="deduction-total">
                  <span>实际退款金额：</span>
                  <span class="total-amount refund-amount">¥{{ currentRefund.refundAmount.toFixed(2) }}</span>
                </div>
                <div class="refund-note">
                  <el-icon><InfoFilled /></el-icon>
                  <span>退款说明：按原支付路径比例回退，确保资金流向可追溯</span>
                </div>
              </div>
            </template>
          </el-alert>

          <el-divider content-position="left">处理进度</el-divider>

          <el-timeline>
            <el-timeline-item
              v-for="(step, index) in currentRefund.steps"
              :key="index"
              :type="step.status === 'completed' ? 'success' : 'info'"
              :timestamp="step.time"
            >
              <div class="timeline-content">
                <div class="timeline-title">{{ step.title }}</div>
                <div class="timeline-desc" v-if="step.description">{{ step.description }}</div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
        <template #footer>
          <el-button @click="detailsDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="rejectDialogVisible"
        title="拒绝退款"
        width="500px"
      >
        <el-form :model="rejectForm" label-width="100px">
          <el-form-item label="拒绝原因">
            <el-input
              v-model="rejectForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请输入拒绝原因"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject" :loading="loading">确认拒绝</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useAccountStore } from '../stores/account'
import { ElMessage, ElMessageBox } from 'element-plus'

const accountStore = useAccountStore()

const filterStatus = ref('all')
const filterDateRange = ref([])
const applyDialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const loading = ref(false)
const currentRefund = ref(null)

const applyForm = ref({
  orderId: '',
  amount: 0,
  refundAmount: 0,
  reasonType: '',
  reason: '',
  refundMethod: 'balance'
})

const rejectForm = ref({
  reason: ''
})

const refunds = ref([
  {
    id: 1,
    orderId: 'ORD202401150001',
    orderTime: '2024-01-15 14:30:00',
    amount: 150.00,
    refundAmount: 150.00,
    reason: '商品质量问题',
    status: 'pending',
    createTime: '2024-01-16 10:20:00',
    refundMethod: 'balance',
    originalDeduction: {
      balanceUsed: 100.00,
      giftUsed: 30.00,
      couponUsed: 20.00,
      cashUsed: 0.00
    },
    steps: [
      { title: '提交退款申请', time: '2024-01-16 10:20:00', status: 'completed', description: '用户提交退款申请' },
      { title: '等待审核', time: '2024-01-16 10:20:00', status: 'current', description: '客服正在审核中' }
    ]
  },
  {
    id: 2,
    orderId: 'ORD202401100002',
    orderTime: '2024-01-10 16:45:00',
    amount: 280.00,
    refundAmount: 280.00,
    reason: '错买/重买',
    status: 'completed',
    createTime: '2024-01-11 09:30:00',
    processTime: '2024-01-11 14:20:00',
    processNote: '退款已处理完成',
    refundMethod: 'original',
    originalDeduction: {
      balanceUsed: 150.00,
      giftUsed: 80.00,
      couponUsed: 50.00,
      cashUsed: 0.00
    },
    refundBreakdown: {
      balanceRefund: 150.00,
      giftRefund: 80.00,
      cashRefund: 50.00,
      couponRefund: ['CPN20240110001', 'CPN20240110002']
    },
    steps: [
      { title: '提交退款申请', time: '2024-01-11 09:30:00', status: 'completed', description: '用户提交退款申请' },
      { title: '审核通过', time: '2024-01-11 14:20:00', status: 'completed', description: '客服审核通过' },
      { title: '退款完成', time: '2024-01-11 14:25:00', status: 'completed', description: '退款已原路返回' }
    ]
  },
  {
    id: 3,
    orderId: 'ORD202401050003',
    orderTime: '2024-01-05 11:20:00',
    amount: 95.00,
    refundAmount: 95.00,
    reason: '商品与描述不符',
    status: 'rejected',
    createTime: '2024-01-06 15:10:00',
    processTime: '2024-01-07 10:30:00',
    processNote: '商品已使用，不符合退款条件',
    refundMethod: 'balance',
    originalDeduction: {
      balanceUsed: 45.00,
      giftUsed: 50.00,
      couponUsed: 0.00,
      cashUsed: 0.00
    },
    steps: [
      { title: '提交退款申请', time: '2024-01-06 15:10:00', status: 'completed', description: '用户提交退款申请' },
      { title: '审核拒绝', time: '2024-01-07 10:30:00', status: 'completed', description: '商品已使用，不符合退款条件' }
    ]
  }
])

const filteredRefunds = computed(() => {
  if (filterStatus.value === 'all') {
    return refunds.value
  }
  return refunds.value.filter(r => r.status === filterStatus.value)
})

const pendingCount = computed(() => refunds.value.filter(r => r.status === 'pending').length)
const completedCount = computed(() => refunds.value.filter(r => r.status === 'completed').length)
const rejectedCount = computed(() => refunds.value.filter(r => r.status === 'rejected').length)
const totalRefundAmount = computed(() => {
  return refunds.value
    .filter(r => r.status === 'completed' || r.status === 'approved')
    .reduce((sum, r) => sum + r.refundAmount, 0)
})

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待处理',
    approved: '已通过',
    rejected: '已拒绝',
    completed: '已完成'
  }
  return textMap[status] || '未知'
}

const showApplyDialog = () => {
  applyForm.value = {
    orderId: '',
    amount: 0,
    refundAmount: 0,
    reasonType: '',
    reason: '',
    refundMethod: 'balance'
  }
  applyDialogVisible.value = true
}

const submitApply = async () => {
  if (!applyForm.value.orderId || !applyForm.value.reason || !applyForm.value.reasonType) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (applyForm.value.refundAmount <= 0) {
    ElMessage.warning('退款金额必须大于0')
    return
  }

  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const refundAmount = applyForm.value.refundAmount
    const totalAmount = applyForm.value.amount || 150
    
    const originalDeduction = {
      balanceUsed: Math.min(refundAmount * 0.6, accountStore.accountInfo.balance),
      giftUsed: Math.min(refundAmount * 0.3, accountStore.accountInfo.giftBalance),
      couponUsed: Math.min(refundAmount * 0.1, 0),
      cashUsed: refundAmount * 0.1
    }
    
    const newRefund = {
      id: Date.now(),
      orderId: applyForm.value.orderId,
      orderTime: new Date().toLocaleString('zh-CN'),
      amount: totalAmount,
      refundAmount: refundAmount,
      reason: `${getReasonTypeText(applyForm.value.reasonType)} - ${applyForm.value.reason}`,
      status: 'pending',
      createTime: new Date().toLocaleString('zh-CN'),
      refundMethod: applyForm.value.refundMethod,
      originalDeduction: originalDeduction,
      steps: [
        { title: '提交退款申请', time: new Date().toLocaleString('zh-CN'), status: 'completed', description: '用户提交退款申请' },
        { title: '等待审核', time: new Date().toLocaleString('zh-CN'), status: 'current', description: '客服正在审核中' }
      ]
    }
    
    refunds.value.unshift(newRefund)
    applyDialogVisible.value = false
    ElMessage.success('退款申请已提交')
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    loading.value = false
  }
}

const getReasonTypeText = (type) => {
  const map = {
    quality: '商品质量问题',
    mismatch: '商品与描述不符',
    mistake: '错买/重买',
    other: '其他'
  }
  return map[type] || '其他'
}

const viewDetails = (row) => {
  currentRefund.value = row
  detailsDialogVisible.value = true
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认通过退款申请？退款金额：¥${row.refundAmount.toFixed(2)}`,
      '确认通过',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 1000))

    const index = refunds.value.findIndex(r => r.id === row.id)
    if (index !== -1) {
      const refundAmount = row.refundAmount
      const originalDeduction = row.originalDeduction || {
        balanceUsed: refundAmount * 0.6,
        giftUsed: refundAmount * 0.3,
        couponUsed: refundAmount * 0.1,
        cashUsed: 0
      }
      
      refunds.value[index].status = 'completed'
      refunds.value[index].processTime = new Date().toLocaleString('zh-CN')
      refunds.value[index].processNote = '退款已处理完成'
      refunds.value[index].refundBreakdown = {
        balanceRefund: originalDeduction.balanceUsed,
        giftRefund: originalDeduction.giftUsed,
        cashRefund: originalDeduction.couponUsed,
        couponRefund: originalDeduction.couponUsed > 0 ? ['CPN' + Date.now()] : []
      }
      refunds.value[index].steps.push(
        { title: '审核通过', time: new Date().toLocaleString('zh-CN'), status: 'completed', description: '客服审核通过' },
        { title: '退款完成', time: new Date().toLocaleString('zh-CN'), status: 'completed', description: '退款已按原路径回退' }
      )

      if (row.refundMethod === 'balance') {
        accountStore.updateAccount({
          balance: accountStore.accountInfo.balance + row.refundAmount
        })
      }
    }

    ElMessage.success('退款申请已通过')
  } catch {
    // 取消操作
  } finally {
    loading.value = false
  }
}

const handleReject = (row) => {
  currentRefund.value = row
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))

    const index = refunds.value.findIndex(r => r.id === currentRefund.value.id)
    if (index !== -1) {
      refunds.value[index].status = 'rejected'
      refunds.value[index].processTime = new Date().toLocaleString('zh-CN')
      refunds.value[index].processNote = rejectForm.value.reason
      refunds.value[index].steps.push(
        { title: '审核拒绝', time: new Date().toLocaleString('zh-CN'), status: 'completed', description: rejectForm.value.reason }
      )
    }

    rejectDialogVisible.value = false
    ElMessage.success('退款申请已拒绝')
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.refund-container {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.refund-amount {
  color: #67c23a;
  font-weight: 600;
  font-size: 16px;
}

.form-value {
  color: #303133;
  font-weight: 600;
}

.refund-details {
  padding: 10px 0;
}

.timeline-content {
  padding: 5px 0;
}

.timeline-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.timeline-desc {
  color: #606266;
  font-size: 13px;
}

.original-deduction-detail,
.refund-deduction-detail {
  padding: 10px 0;
}

.deduction-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  font-size: 14px;
}

.deduction-icon {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  width: 25px;
  text-align: center;
}

.deduction-label {
  color: #606266;
  flex: 1;
  font-weight: 500;
}

.deduction-value {
  color: #303133;
  font-weight: 600;
  font-size: 15px;
}

.deduction-value.refund-value {
  color: #67c23a;
}

.deduction-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.total-amount {
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
}

.total-amount.refund-amount {
  color: #67c23a;
}

.refund-note {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
  margin-top: 10px;
  font-size: 13px;
  color: #909399;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.refund-note .el-icon {
  color: #409eff;
}
</style>
