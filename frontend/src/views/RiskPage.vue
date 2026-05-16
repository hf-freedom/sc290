<template>
  <div class="risk-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">风控管理</h1>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加风控账户
        </el-button>
      </div>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">风控账户</div>
            <div class="stat-value">{{ riskAccounts.length }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-label">已冻结</div>
            <div class="stat-value">{{ frozenCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-label">已解除</div>
            <div class="stat-value">{{ resolvedCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card info">
            <div class="stat-label">高风险</div>
            <div class="stat-value">{{ highRiskCount }}</div>
          </div>
        </el-col>
      </el-row>

      <el-card>
        <template #header>
          <div class="card-header">
            <span>风控账户列表</span>
            <div class="header-actions">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索账户/手机号"
                style="width: 200px;"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select v-model="filterRiskLevel" placeholder="风险等级" style="width: 120px; margin-left: 10px;">
                <el-option label="全部" value="all" />
                <el-option label="高风险" value="high" />
                <el-option label="中风险" value="medium" />
                <el-option label="低风险" value="low" />
              </el-select>
              <el-select v-model="filterStatus" placeholder="账户状态" style="width: 120px; margin-left: 10px;">
                <el-option label="全部" value="all" />
                <el-option label="冻结" value="frozen" />
                <el-option label="正常" value="normal" />
              </el-select>
            </div>
          </div>
        </template>

        <el-table :data="filteredRiskAccounts" style="width: 100%" stripe>
          <el-table-column prop="accountId" label="账户ID" width="100" />
          <el-table-column prop="name" label="账户名称" width="120" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="balance" label="账户余额" width="120">
            <template #default="{ row }">
              ¥{{ row.balance.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="riskLevel" label="风险等级" width="100">
            <template #default="{ row }">
              <el-tag :type="getRiskLevelType(row.riskLevel)">
                {{ getRiskLevelText(row.riskLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="riskReason" label="风控原因" />
          <el-table-column prop="status" label="账户状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'frozen' ? 'danger' : 'success'">
                {{ row.status === 'frozen' ? '已冻结' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="添加时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                size="small"
                type="primary"
                text
                @click="viewDetails(row)"
              >
                详情
              </el-button>
              <el-button
                v-if="row.status !== 'frozen'"
                size="small"
                type="warning"
                text
                @click="handleFreeze(row)"
              >
                冻结
              </el-button>
              <el-button
                v-if="row.status === 'frozen'"
                size="small"
                type="success"
                text
                @click="handleUnfreeze(row)"
              >
                解冻
              </el-button>
              <el-button
                size="small"
                type="danger"
                text
                @click="handleRemove(row)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-dialog
        v-model="addDialogVisible"
        title="添加风控账户"
        width="600px"
      >
        <el-form :model="addForm" label-width="100px" :rules="addFormRules" ref="addFormRef">
          <el-form-item label="账户ID" prop="accountId">
            <el-input v-model="addForm.accountId" placeholder="请输入账户ID" />
          </el-form-item>
          <el-form-item label="账户名称" prop="name">
            <el-input v-model="addForm.name" placeholder="请输入账户名称" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="addForm.phone" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="风险等级" prop="riskLevel">
            <el-select v-model="addForm.riskLevel" placeholder="请选择风险等级" style="width: 100%;">
              <el-option label="高风险" value="high" />
              <el-option label="中风险" value="medium" />
              <el-option label="低风险" value="low" />
            </el-select>
          </el-form-item>
          <el-form-item label="账户余额">
            <el-input-number
              v-model="addForm.balance"
              :min="0"
              :precision="2"
            />
          </el-form-item>
          <el-form-item label="风控原因" prop="riskReason">
            <el-input
              v-model="addForm.riskReason"
              type="textarea"
              :rows="3"
              placeholder="请描述风控原因"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdd" :loading="loading">确认添加</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="detailsDialogVisible"
        title="风控详情"
        width="800px"
      >
        <div class="risk-details" v-if="currentRiskAccount">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="账户ID">
              {{ currentRiskAccount.accountId }}
            </el-descriptions-item>
            <el-descriptions-item label="账户名称">
              {{ currentRiskAccount.name }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号码">
              {{ currentRiskAccount.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="账户余额">
              ¥{{ currentRiskAccount.balance.toFixed(2) }}
            </el-descriptions-item>
            <el-descriptions-item label="风险等级">
              <el-tag :type="getRiskLevelType(currentRiskAccount.riskLevel)">
                {{ getRiskLevelText(currentRiskAccount.riskLevel) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="账户状态">
              <el-tag :type="currentRiskAccount.status === 'frozen' ? 'danger' : 'success'">
                {{ currentRiskAccount.status === 'frozen' ? '已冻结' : '正常' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="风控原因" :span="2">
              {{ currentRiskAccount.riskReason }}
            </el-descriptions-item>
            <el-descriptions-item label="添加时间">
              {{ currentRiskAccount.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="添加人">
              {{ currentRiskAccount.createdBy }}
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">风控记录</el-divider>

          <el-table :data="currentRiskAccount.records" style="width: 100%" size="small">
            <el-table-column prop="time" label="时间" width="160" />
            <el-table-column prop="operator" label="操作人" width="120" />
            <el-table-column prop="action" label="操作" width="120">
              <template #default="{ row }">
                <el-tag :type="getActionType(row.action)" size="small">
                  {{ row.action }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="原因/备注" />
          </el-table>

          <div class="add-record" style="margin-top: 20px;">
            <el-button type="primary" @click="showAddRecordDialog">
              <el-icon><Plus /></el-icon>
              添加记录
            </el-button>
          </div>
        </div>
        <template #footer>
          <el-button @click="detailsDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="addRecordDialogVisible"
        title="添加风控记录"
        width="500px"
      >
        <el-form :model="recordForm" label-width="100px">
          <el-form-item label="操作类型">
            <el-select v-model="recordForm.action" placeholder="请选择操作类型" style="width: 100%;">
              <el-option label="审查" value="审查" />
              <el-option label="跟进" value="跟进" />
              <el-option label="预警" value="预警" />
              <el-option label="冻结" value="冻结" />
              <el-option label="解冻" value="解冻" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作人">
            <el-input v-model="recordForm.operator" placeholder="请输入操作人" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="recordForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请输入备注信息"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="addRecordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddRecord" :loading="loading">确认</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchKeyword = ref('')
const filterRiskLevel = ref('all')
const filterStatus = ref('all')
const addDialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const addRecordDialogVisible = ref(false)
const loading = ref(false)
const currentRiskAccount = ref(null)
const addFormRef = ref(null)

const addForm = ref({
  accountId: '',
  name: '',
  phone: '',
  riskLevel: 'medium',
  balance: 0,
  riskReason: ''
})

const recordForm = ref({
  action: '',
  operator: '',
  reason: ''
})

const addFormRules = {
  accountId: [{ required: true, message: '请输入账户ID', trigger: 'blur' }],
  name: [{ required: true, message: '请输入账户名称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }],
  riskLevel: [{ required: true, message: '请选择风险等级', trigger: 'change' }],
  riskReason: [{ required: true, message: '请输入风控原因', trigger: 'blur' }]
}

const riskAccounts = ref([
  {
    id: 1,
    accountId: 'A10001',
    name: '李明',
    phone: '13900139001',
    balance: 8500.00,
    riskLevel: 'high',
    riskReason: '异常大额充值，疑似套现',
    status: 'frozen',
    createTime: '2024-01-15 10:30:00',
    createdBy: '系统管理员',
    records: [
      { time: '2024-01-15 10:30:00', operator: '系统管理员', action: '添加风控', reason: '异常大额充值，疑似套现' },
      { time: '2024-01-15 10:35:00', operator: '系统管理员', action: '冻结', reason: '账户已冻结，等待调查' },
      { time: '2024-01-16 09:20:00', operator: '风控专员', action: '跟进', reason: '正在核实交易记录' }
    ]
  },
  {
    id: 2,
    accountId: 'A10002',
    name: '王芳',
    phone: '13800138002',
    balance: 3200.00,
    riskLevel: 'medium',
    riskReason: '短时间内多次退款',
    status: 'normal',
    createTime: '2024-01-12 14:20:00',
    createdBy: '客服专员',
    records: [
      { time: '2024-01-12 14:20:00', operator: '客服专员', action: '添加风控', reason: '短时间内多次退款' }
    ]
  },
  {
    id: 3,
    accountId: 'A10003',
    name: '张伟',
    phone: '13700137003',
    balance: 5600.00,
    riskLevel: 'low',
    riskReason: '新账户，存在异常登录',
    status: 'normal',
    createTime: '2024-01-10 16:45:00',
    createdBy: '系统',
    records: [
      { time: '2024-01-10 16:45:00', operator: '系统', action: '预警', reason: '检测到异常登录地点' },
      { time: '2024-01-11 08:30:00', operator: '风控专员', action: '审查', reason: '已确认账户安全' }
    ]
  }
])

const filteredRiskAccounts = computed(() => {
  return riskAccounts.value.filter(account => {
    const matchKeyword = !searchKeyword.value ||
      account.name.includes(searchKeyword.value) ||
      account.phone.includes(searchKeyword.value) ||
      account.accountId.includes(searchKeyword.value)
    
    const matchRiskLevel = filterRiskLevel.value === 'all' ||
      account.riskLevel === filterRiskLevel.value
    
    const matchStatus = filterStatus.value === 'all' ||
      account.status === filterStatus.value
    
    return matchKeyword && matchRiskLevel && matchStatus
  })
})

const frozenCount = computed(() => riskAccounts.value.filter(a => a.status === 'frozen').length)
const resolvedCount = computed(() => riskAccounts.value.filter(a => a.riskLevel === 'low').length)
const highRiskCount = computed(() => riskAccounts.value.filter(a => a.riskLevel === 'high').length)

const getRiskLevelType = (level) => {
  const typeMap = { high: 'danger', medium: 'warning', low: 'success' }
  return typeMap[level] || 'info'
}

const getRiskLevelText = (level) => {
  const textMap = { high: '高风险', medium: '中风险', low: '低风险' }
  return textMap[level] || '未知'
}

const getActionType = (action) => {
  const typeMap = {
    '添加风控': 'warning',
    '冻结': 'danger',
    '解冻': 'success',
    '预警': 'info',
    '审查': 'primary',
    '跟进': 'primary'
  }
  return typeMap[action] || 'info'
}

const showAddDialog = () => {
  addForm.value = {
    accountId: '',
    name: '',
    phone: '',
    riskLevel: 'medium',
    balance: 0,
    riskReason: ''
  }
  addDialogVisible.value = true
}

const submitAdd = async () => {
  try {
    await addFormRef.value.validate()
    
    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newAccount = {
      id: Date.now(),
      ...addForm.value,
      status: 'normal',
      createTime: new Date().toLocaleString('zh-CN'),
      createdBy: '系统管理员',
      records: [
        {
          time: new Date().toLocaleString('zh-CN'),
          operator: '系统管理员',
          action: '添加风控',
          reason: addForm.value.riskReason
        }
      ]
    }
    
    riskAccounts.value.unshift(newAccount)
    addDialogVisible.value = false
    ElMessage.success('风控账户添加成功')
  } catch (error) {
    // 验证失败
  } finally {
    loading.value = false
  }
}

const viewDetails = (row) => {
  currentRiskAccount.value = row
  detailsDialogVisible.value = true
}

const handleFreeze = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认冻结账户 ${row.name}？`,
      '确认冻结',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 1000))

    const index = riskAccounts.value.findIndex(a => a.id === row.id)
    if (index !== -1) {
      riskAccounts.value[index].status = 'frozen'
      riskAccounts.value[index].records.push({
        time: new Date().toLocaleString('zh-CN'),
        operator: '系统管理员',
        action: '冻结',
        reason: '手动冻结账户'
      })
    }

    ElMessage.success('账户已冻结')
  } catch {
    // 取消操作
  } finally {
    loading.value = false
  }
}

const handleUnfreeze = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认解冻账户 ${row.name}？`,
      '确认解冻',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 1000))

    const index = riskAccounts.value.findIndex(a => a.id === row.id)
    if (index !== -1) {
      riskAccounts.value[index].status = 'normal'
      riskAccounts.value[index].riskLevel = 'low'
      riskAccounts.value[index].records.push({
        time: new Date().toLocaleString('zh-CN'),
        operator: '系统管理员',
        action: '解冻',
        reason: '风险已解除，账户解冻'
      })
    }

    ElMessage.success('账户已解冻')
  } catch {
    // 取消操作
  } finally {
    loading.value = false
  }
}

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认从风控列表中移除账户 ${row.name}？`,
      '确认移除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const index = riskAccounts.value.findIndex(a => a.id === row.id)
    if (index !== -1) {
      riskAccounts.value.splice(index, 1)
    }

    ElMessage.success('账户已从风控列表移除')
  } catch {
    // 取消操作
  }
}

const showAddRecordDialog = () => {
  recordForm.value = {
    action: '',
    operator: '',
    reason: ''
  }
  addRecordDialogVisible.value = true
}

const submitAddRecord = async () => {
  if (!recordForm.value.action || !recordForm.value.reason) {
    ElMessage.warning('请填写完整信息')
    return
  }

  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))

    if (currentRiskAccount.value) {
      currentRiskAccount.value.records.push({
        time: new Date().toLocaleString('zh-CN'),
        operator: recordForm.value.operator || '系统管理员',
        action: recordForm.value.action,
        reason: recordForm.value.reason
      })
    }

    addRecordDialogVisible.value = false
    ElMessage.success('风控记录添加成功')
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.risk-container {
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

.risk-details {
  padding: 10px 0;
}

.add-record {
  text-align: right;
}
</style>
