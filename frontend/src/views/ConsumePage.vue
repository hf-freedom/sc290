<template>
  <div class="consume-container">
    <div class="page-container">
      <div class="page-header">
        <h1 class="page-title">消费结账</h1>
        <el-tag type="info">
          账户余额：¥{{ accountStore.accountInfo.balance.toFixed(2) }} |
          赠送金：¥{{ accountStore.accountInfo.giftBalance.toFixed(2) }}
        </el-tag>
      </div>

      <el-row :gutter="20">
        <el-col :span="14">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>商品列表</span>
                <div class="header-actions">
                  <el-input
                    v-model="searchKeyword"
                    placeholder="搜索商品"
                    style="width: 200px;"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
            </template>
            <div class="products-grid">
              <el-row :gutter="15">
                <el-col
                  v-for="product in filteredProducts"
                  :key="product.id"
                  :span="8"
                >
                  <el-card
                    class="product-card"
                    shadow="hover"
                    @click="addToCart(product)"
                  >
                    <div class="product-image">
                      <el-icon :size="48"><Goods /></el-icon>
                    </div>
                    <div class="product-info">
                      <div class="product-name">{{ product.name }}</div>
                      <div class="product-price">
                        <span class="original" v-if="product.originalPrice">
                          ¥{{ product.originalPrice.toFixed(2) }}
                        </span>
                        <span class="current">¥{{ product.price.toFixed(2) }}</span>
                      </div>
                      <div class="product-stock">
                        库存：{{ product.stock }}
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>

        <el-col :span="10">
          <el-card class="cart-card">
            <template #header>
              <div class="card-header">
                <span>购物车</span>
                <el-badge :value="cartItems.length" :hidden="cartItems.length === 0">
                  <el-button
                    text
                    type="danger"
                    @click="clearCart"
                    :disabled="cartItems.length === 0"
                  >
                    清空
                  </el-button>
                </el-badge>
              </div>
            </template>

            <div class="cart-content" v-if="cartItems.length > 0">
              <el-table :data="cartItems" style="width: 100%" size="small">
                <el-table-column prop="name" label="商品" />
                <el-table-column prop="quantity" label="数量" width="80">
                  <template #default="{ row, $index }">
                    <el-input-number
                      v-model="row.quantity"
                      :min="1"
                      :max="row.stock"
                      size="small"
                      @change="updateCartItem($index)"
                    />
                  </template>
                </el-table-column>
                <el-table-column prop="price" label="单价" width="80">
                  <template #default="{ row }">
                    ¥{{ row.price.toFixed(2) }}
                  </template>
                </el-table-column>
                <el-table-column label="小计" width="90">
                  <template #default="{ row }">
                    <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="60">
                  <template #default="{ $index }">
                    <el-button
                      type="danger"
                      size="small"
                      text
                      @click="removeFromCart($index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>

              <el-divider />

              <div class="order-summary">
                <div class="summary-row">
                  <span>商品总数：</span>
                  <span>{{ totalQuantity }} 件</span>
                </div>
                <div class="summary-row">
                  <span>商品金额：</span>
                  <span>¥{{ subtotal.toFixed(2) }}</span>
                </div>
                <div class="summary-row discount" v-if="discountAmount > 0">
                  <span>会员折扣：</span>
                  <span>-¥{{ discountAmount.toFixed(2) }}</span>
                </div>
                <div class="summary-row total">
                  <span>应付金额：</span>
                  <span class="total-amount">¥{{ totalAmount.toFixed(2) }}</span>
                </div>
              </div>

              <el-divider />

              <div class="payment-section">
                <div class="section-title">支付方式</div>
                
                <el-alert
                  title="扣减优先级"
                  type="info"
                  :closable="false"
                  show-icon
                  style="margin-bottom: 15px;"
                >
                  <template #default>
                    <div class="deduction-priority">
                      <div class="priority-item">
                        <el-tag size="small" type="primary">1</el-tag>
                        <span>账户余额</span>
                        <span class="priority-amount">¥{{ accountStore.accountInfo.balance.toFixed(2) }}</span>
                      </div>
                      <div class="priority-arrow">↓</div>
                      <div class="priority-item">
                        <el-tag size="small" type="success">2</el-tag>
                        <span>赠送金</span>
                        <span class="priority-amount">¥{{ accountStore.accountInfo.giftBalance.toFixed(2) }}</span>
                      </div>
                      <div class="priority-arrow">↓</div>
                      <div class="priority-item">
                        <el-tag size="small" type="warning">3</el-tag>
                        <span>优惠券</span>
                        <span class="priority-amount">{{ availableCoupons.length }}张可用</span>
                      </div>
                    </div>
                  </template>
                </el-alert>
                
                <el-radio-group v-model="paymentMethod" class="payment-methods">
                  <el-radio label="balance">
                    <div class="payment-option">
                      <el-icon><Coin /></el-icon>
                      <span>账户余额</span>
                      <span class="payment-balance">¥{{ accountStore.accountInfo.balance.toFixed(2) }}</span>
                    </div>
                  </el-radio>
                  <el-radio label="gift">
                    <div class="payment-option">
                      <el-icon><Gift /></el-icon>
                      <span>赠送金</span>
                      <span class="payment-balance">¥{{ accountStore.accountInfo.giftBalance.toFixed(2) }}</span>
                    </div>
                  </el-radio>
                  <el-radio label="cash">
                    <div class="payment-option">
                      <el-icon><Money /></el-icon>
                      <span>现金支付</span>
                    </div>
                  </el-radio>
                  <el-radio label="combined">
                    <div class="payment-option">
                      <el-icon><Connection /></el-icon>
                      <span>组合支付</span>
                      <el-tag v-if="!canPayWithBalance" type="warning" size="small" style="margin-left: 10px;">
                        余额不足
                      </el-tag>
                    </div>
                  </el-radio>
                </el-radio-group>

                <div class="combined-payment" v-if="paymentMethod === 'combined'">
                  <el-alert
                    title="组合支付说明"
                    type="info"
                    :closable="false"
                    show-icon
                    style="margin-bottom: 15px;"
                  >
                    <template #default>
                      <div>使用余额+赠送金+现金组合支付，余额不足时可使用现金补足</div>
                    </template>
                  </el-alert>
                  
                  <el-form :model="combinedPayment" label-width="80px" size="small">
                    <el-form-item label="使用余额">
                      <el-input-number
                        v-model="combinedPayment.balanceAmount"
                        :min="0"
                        :max="accountStore.accountInfo.balance"
                        :step="10"
                      />
                      <span class="input-hint">可用: ¥{{ accountStore.accountInfo.balance.toFixed(2) }}</span>
                    </el-form-item>
                    <el-form-item label="使用赠送金">
                      <el-input-number
                        v-model="combinedPayment.giftAmount"
                        :min="0"
                        :max="accountStore.accountInfo.giftBalance"
                        :step="10"
                      />
                      <span class="input-hint">可用: ¥{{ accountStore.accountInfo.giftBalance.toFixed(2) }}</span>
                    </el-form-item>
                    <el-form-item label="现金补足">
                      <el-input-number
                        v-model="combinedPayment.cashAmount"
                        :min="0"
                        :step="10"
                      />
                      <span class="input-hint">需补足: ¥{{ Math.max(0, totalAmount - combinedPayment.balanceAmount - combinedPayment.giftAmount).toFixed(2) }}</span>
                    </el-form-item>
                  </el-form>
                  <div class="combined-summary">
                    合计：¥{{ (combinedPayment.balanceAmount + combinedPayment.giftAmount + combinedPayment.cashAmount).toFixed(2) }}
                    <span v-if="combinedPayment.balanceAmount + combinedPayment.giftAmount + combinedPayment.cashAmount >= totalAmount" class="enough">
                      (足够支付)
                    </span>
                    <span v-else class="not-enough">
                      (还需 ¥{{ Math.max(0, totalAmount - combinedPayment.balanceAmount - combinedPayment.giftAmount - combinedPayment.cashAmount).toFixed(2) }})
                    </span>
                  </div>
                </div>

                <el-alert
                  v-if="showInsufficientAlert"
                  title="余额不足提示"
                  type="warning"
                  :closable="false"
                  show-icon
                  style="margin-top: 15px;"
                >
                  <template #default>
                    <div class="insufficient-info">
                      <div>您的账户余额不足以完成支付</div>
                      <div>建议：选择"组合支付"使用余额+赠送金+现金补足</div>
                    </div>
                  </template>
                </el-alert>

                <el-button
                  type="primary"
                  size="large"
                  style="width: 100%; margin-top: 20px;"
                  @click="handleCheckout"
                  :disabled="!canCheckout"
                  :loading="loading"
                >
                  确认支付 ¥{{ totalAmount.toFixed(2) }}
                </el-button>
              </div>
            </div>

            <el-empty v-else description="购物车是空的" />
          </el-card>

          <el-card style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>可用优惠券</span>
              </div>
            </template>
            <div class="available-coupons">
              <el-tag
                v-for="coupon in availableCoupons"
                :key="coupon.id"
                class="coupon-tag"
                :type="selectedCoupon?.id === coupon.id ? 'primary' : 'info'"
                @click="toggleCoupon(coupon)"
              >
                ¥{{ coupon.amount }} {{ coupon.name }}
              </el-tag>
              <el-empty v-if="availableCoupons.length === 0" description="暂无可用优惠券" :image-size="60" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-dialog
        v-model="checkoutDialogVisible"
        title="支付确认"
        width="600px"
      >
        <div class="checkout-summary">
          <div class="summary-item">
            <span>商品数量：</span>
            <span>{{ totalQuantity }} 件</span>
          </div>
          <div class="summary-item">
            <span>商品金额：</span>
            <span>¥{{ subtotal.toFixed(2) }}</span>
          </div>
          <div class="summary-item" v-if="discountAmount > 0">
            <span>会员折扣：</span>
            <span>-¥{{ discountAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item" v-if="selectedCoupon">
            <span>优惠券：</span>
            <span>-¥{{ selectedCoupon.amount.toFixed(2) }}</span>
          </div>
          <el-divider />
          <div class="summary-item total">
            <span>应付金额：</span>
            <span class="amount">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          
          <el-divider content-position="left">支付拆分明细</el-divider>
          <el-alert
            title="支付来源分配"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 15px;"
          >
            <template #default>
              <div class="payment-split-detail">
                <div class="split-row" v-if="getPaymentSplit().balanceAmount > 0">
                  <span class="split-icon">①</span>
                  <span class="split-label">账户余额支付：</span>
                  <span class="split-value primary">¥{{ getPaymentSplit().balanceAmount.toFixed(2) }}</span>
                </div>
                <div class="split-row" v-if="getPaymentSplit().giftAmount > 0">
                  <span class="split-icon">②</span>
                  <span class="split-label">赠送金支付：</span>
                  <span class="split-value success">¥{{ getPaymentSplit().giftAmount.toFixed(2) }}</span>
                </div>
                <div class="split-row" v-if="getPaymentSplit().couponAmount > 0">
                  <span class="split-icon">③</span>
                  <span class="split-label">优惠券抵扣：</span>
                  <span class="split-value warning">¥{{ getPaymentSplit().couponAmount.toFixed(2) }}</span>
                </div>
                <div class="split-row" v-if="getPaymentSplit().cashAmount > 0">
                  <span class="split-icon">④</span>
                  <span class="split-label">现金补足支付：</span>
                  <span class="split-value danger">¥{{ getPaymentSplit().cashAmount.toFixed(2) }}</span>
                </div>
                <el-divider style="margin: 15px 0;" />
                <div class="split-total">
                  <span>支付总额：</span>
                  <span class="total-value">¥{{ totalAmount.toFixed(2) }}</span>
                </div>
                <div class="split-check">
                  <el-icon v-if="isPaymentEnough" color="#67c23a"><CircleCheckFilled /></el-icon>
                  <el-icon v-else color="#f56c6c"><CircleCloseFilled /></el-icon>
                  <span :class="isPaymentEnough ? 'check-success' : 'check-fail'">
                    {{ isPaymentEnough ? '✓ 支付金额充足' : '✗ 支付金额不足' }}
                  </span>
                </div>
              </div>
            </template>
          </el-alert>
        </div>
        <template #footer>
          <el-button @click="checkoutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCheckout" :loading="loading">
            确认支付
          </el-button>
        </template>
      </el-dialog>

      <el-dialog
        v-model="successDialogVisible"
        title="支付成功"
        width="500px"
        :close-on-click-modal="false"
      >
        <div class="success-content">
          <el-icon class="success-icon" :size="80"><CircleCheckFilled /></el-icon>
          <p class="success-message">支付成功！</p>
          <p class="success-amount">¥{{ lastPaidAmount.toFixed(2) }}</p>
          <p class="success-tip">您的订单已创建，感谢您的购买</p>
          
          <el-divider content-position="left" v-if="lastPaymentSplit">支付明细</el-divider>
          <div class="payment-split-summary" v-if="lastPaymentSplit">
            <div class="split-item" v-if="lastPaymentSplit.balanceAmount > 0">
              <span class="split-type">余额：</span>
              <span class="split-amount">-¥{{ lastPaymentSplit.balanceAmount.toFixed(2) }}</span>
            </div>
            <div class="split-item" v-if="lastPaymentSplit.giftAmount > 0">
              <span class="split-type">赠送金：</span>
              <span class="split-amount">-¥{{ lastPaymentSplit.giftAmount.toFixed(2) }}</span>
            </div>
            <div class="split-item" v-if="lastPaymentSplit.couponAmount > 0">
              <span class="split-type">优惠券：</span>
              <span class="split-amount">-¥{{ lastPaymentSplit.couponAmount.toFixed(2) }}</span>
            </div>
            <div class="split-item" v-if="lastPaymentSplit.cashAmount > 0">
              <span class="split-type">现金：</span>
              <span class="split-amount">-¥{{ lastPaymentSplit.cashAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <template #footer>
          <el-button type="primary" @click="successDialogVisible = false">完成</el-button>
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

const searchKeyword = ref('')
const cartItems = ref([])
const paymentMethod = ref('balance')
const selectedCoupon = ref(null)
const loading = ref(false)
const checkoutDialogVisible = ref(false)
const successDialogVisible = ref(false)
const lastPaidAmount = ref(0)
const lastPaymentSplit = ref(null)

const combinedPayment = ref({
  balanceAmount: 0,
  giftAmount: 0,
  cashAmount: 0
})

const products = ref([
  { id: 1, name: '咖啡', price: 28, originalPrice: 35, stock: 100 },
  { id: 2, name: '奶茶', price: 22, originalPrice: null, stock: 150 },
  { id: 3, name: '蛋糕', price: 38, originalPrice: 48, stock: 50 },
  { id: 4, name: '面包', price: 15, originalPrice: null, stock: 80 },
  { id: 5, name: '沙拉', price: 32, originalPrice: 40, stock: 40 },
  { id: 6, name: '三明治', price: 25, originalPrice: null, stock: 60 },
  { id: 7, name: '果汁', price: 18, originalPrice: 22, stock: 90 },
  { id: 8, name: '冰淇淋', price: 20, originalPrice: null, stock: 70 },
  { id: 9, name: '饼干', price: 12, originalPrice: 15, stock: 120 }
])

const filteredProducts = computed(() => {
  if (!searchKeyword.value) return products.value
  return products.value.filter(p =>
    p.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const subtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const discountAmount = computed(() => {
  const level = accountStore.accountInfo.level
  const discountMap = {
    '普通会员': 0.02,
    '白银会员': 0.05,
    '黄金会员': 0.08,
    '铂金会员': 0.12,
    '钻石会员': 0.15
  }
  const discountRate = discountMap[level] || 0
  return subtotal.value * discountRate
})

const couponDiscount = computed(() => {
  return selectedCoupon.value?.amount || 0
})

const totalAmount = computed(() => {
  return Math.max(0, subtotal.value - discountAmount.value - couponDiscount.value)
})

const totalQuantity = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const availableCoupons = computed(() => {
  return accountStore.coupons.filter(c => {
    return c.status === 'unused' && c.amount <= totalAmount.value
  })
})

const canCheckout = computed(() => {
  if (cartItems.value.length === 0) return false
  
  if (paymentMethod.value === 'balance') {
    return accountStore.accountInfo.balance >= totalAmount.value
  } else if (paymentMethod.value === 'gift') {
    return accountStore.accountInfo.giftBalance >= totalAmount.value
  } else if (paymentMethod.value === 'cash') {
    return true
  } else if (paymentMethod.value === 'combined') {
    return combinedPayment.value.balanceAmount + combinedPayment.value.giftAmount + combinedPayment.value.cashAmount >= totalAmount.value
  }
  return false
})

const canPayWithBalance = computed(() => {
  return accountStore.accountInfo.balance + accountStore.accountInfo.giftBalance >= totalAmount.value
})

const showInsufficientAlert = computed(() => {
  return paymentMethod.value !== 'cash' && 
         paymentMethod.value !== 'combined' && 
         !canPayWithBalance.value && 
         cartItems.value.length > 0
})

const isPaymentEnough = computed(() => {
  const split = getPaymentSplit()
  return split.balanceAmount + split.giftAmount + split.couponAmount + split.cashAmount >= totalAmount.value
})

const getPaymentSplit = () => {
  const amount = totalAmount.value
  const balance = accountStore.accountInfo.balance
  const giftBalance = accountStore.accountInfo.giftBalance
  const couponAmount = selectedCoupon.value?.amount || 0
  
  let balanceAmount = 0
  let giftAmount = 0
  let cashAmount = 0
  
  if (paymentMethod.value === 'balance') {
    balanceAmount = Math.min(amount, balance)
  } else if (paymentMethod.value === 'gift') {
    giftAmount = Math.min(amount, giftBalance)
  } else if (paymentMethod.value === 'cash') {
    cashAmount = amount
  } else if (paymentMethod.value === 'combined') {
    balanceAmount = Math.min(combinedPayment.value.balanceAmount, amount)
    const remainingAfterBalance = Math.max(0, amount - balanceAmount)
    giftAmount = Math.min(combinedPayment.value.giftAmount, remainingAfterBalance)
    const remainingAfterGift = Math.max(0, remainingAfterBalance - giftAmount)
    cashAmount = Math.min(combinedPayment.value.cashAmount, remainingAfterGift)
  }
  
  return {
    balanceAmount,
    giftAmount,
    couponAmount,
    cashAmount
  }
}

const addToCart = (product) => {
  const existingItem = cartItems.value.find(item => item.id === product.id)
  if (existingItem) {
    if (existingItem.quantity < existingItem.stock) {
      existingItem.quantity++
      ElMessage.success(`已添加 ${product.name} 到购物车`)
    } else {
      ElMessage.warning('库存不足')
    }
  } else {
    cartItems.value.push({
      ...product,
      quantity: 1
    })
    ElMessage.success(`已添加 ${product.name} 到购物车`)
  }
}

const removeFromCart = (index) => {
  const item = cartItems.value[index]
  cartItems.value.splice(index, 1)
  ElMessage.info(`已移除 ${item.name}`)
}

const updateCartItem = (index) => {
  // 数量更新自动处理
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    cartItems.value = []
    ElMessage.success('购物车已清空')
  } catch {
    // 取消操作
  }
}

const toggleCoupon = (coupon) => {
  if (selectedCoupon.value?.id === coupon.id) {
    selectedCoupon.value = null
  } else {
    selectedCoupon.value = coupon
  }
}

const getDeductionAmount = (type) => {
  const amount = totalAmount.value
  const balance = accountStore.accountInfo.balance
  const giftBalance = accountStore.accountInfo.giftBalance
  const couponAmount = selectedCoupon.value?.amount || 0
  
  if (type === 'balance') {
    return Math.min(amount, balance)
  } else if (type === 'gift') {
    const remainingAfterBalance = Math.max(0, amount - balance)
    return Math.min(remainingAfterBalance, giftBalance)
  } else if (type === 'coupon') {
    return couponAmount
  }
  return 0
}

const handleCheckout = async () => {
  if (!canCheckout.value) {
    if (showInsufficientAlert.value) {
      ElMessage.warning('余额不足，建议使用组合支付或现金支付')
    } else {
      ElMessage.error('无法完成支付，请检查支付方式')
    }
    return
  }
  checkoutDialogVisible.value = true
}

const confirmCheckout = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const paymentSplit = getPaymentSplit()
    
    let balanceUsed = paymentSplit.balanceAmount
    let giftUsed = paymentSplit.giftAmount
    let cashUsed = paymentSplit.cashAmount
    
    accountStore.updateAccount({
      balance: accountStore.accountInfo.balance - balanceUsed,
      giftBalance: accountStore.accountInfo.giftBalance - giftUsed
    })

    if (selectedCoupon.value) {
      const coupon = accountStore.coupons.find(c => c.id === selectedCoupon.value.id)
      if (coupon) {
        coupon.status = 'used'
      }
    }

    lastPaidAmount.value = totalAmount.value
    lastPaymentSplit.value = paymentSplit
    cartItems.value = []
    selectedCoupon.value = null
    combinedPayment.value = { balanceAmount: 0, giftAmount: 0, cashAmount: 0 }
    
    checkoutDialogVisible.value = false
    successDialogVisible.value = true
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.consume-container {
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

.products-grid {
  min-height: 400px;
}

.product-card {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: #fff;
  margin-bottom: 10px;
}

.product-info {
  text-align: center;
}

.product-name {
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.product-price {
  margin-bottom: 5px;
}

.product-price .original {
  text-decoration: line-through;
  color: #c0c4cc;
  font-size: 12px;
  margin-right: 5px;
}

.product-price .current {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
}

.product-stock {
  font-size: 12px;
  color: #909399;
}

.cart-card {
  position: sticky;
  top: 20px;
}

.subtotal {
  color: #f56c6c;
  font-weight: 600;
}

.order-summary {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #606266;
}

.summary-row.discount {
  color: #67c23a;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.total-amount {
  color: #f56c6c;
  font-size: 24px;
}

.payment-section {
  margin-top: 20px;
}

.section-title {
  font-weight: 600;
  margin-bottom: 15px;
  color: #303133;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.payment-balance {
  color: #909399;
  font-size: 13px;
}

.combined-payment {
  margin-top: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.combined-summary {
  margin-top: 10px;
  text-align: right;
  color: #606266;
}

.combined-summary .enough {
  color: #67c23a;
}

.combined-summary .not-enough {
  color: #f56c6c;
}

.deduction-priority {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 0;
}

.priority-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #606266;
}

.priority-amount {
  margin-left: auto;
  font-weight: 600;
  color: #303133;
}

.priority-arrow {
  text-align: center;
  color: #909399;
  font-size: 12px;
  line-height: 1;
}

.deduction-detail {
  padding: 10px 0;
}

.deduction-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
}

.deduction-step {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
  width: 20px;
}

.deduction-label {
  color: #606266;
  flex: 1;
}

.deduction-amount {
  color: #303133;
  font-weight: 600;
  font-size: 15px;
}

.available-coupons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.coupon-tag {
  cursor: pointer;
  padding: 8px 15px;
}

.checkout-summary {
  padding: 20px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  color: #606266;
}

.summary-item.total {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.summary-item .amount {
  color: #f56c6c;
  font-size: 24px;
}

.success-content {
  text-align: center;
  padding: 30px 0;
}

.success-icon {
  color: #67c23a;
  margin-bottom: 20px;
}

.success-message {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}

.success-amount {
  font-size: 36px;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 15px;
}

.success-tip {
  color: #909399;
  font-size: 14px;
}

.combined-summary .not-enough {
  color: #f56c6c;
}

.input-hint {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.insufficient-info {
  font-size: 13px;
  line-height: 1.6;
}

.payment-split-detail {
  padding: 10px 0;
}

.split-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  font-size: 14px;
}

.split-icon {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  width: 25px;
  text-align: center;
}

.split-label {
  color: #606266;
  flex: 1;
  font-weight: 500;
}

.split-value {
  font-weight: 600;
  font-size: 16px;
}

.split-value.primary {
  color: #409eff;
}

.split-value.success {
  color: #67c23a;
}

.split-value.warning {
  color: #e6a23c;
}

.split-value.danger {
  color: #f56c6c;
}

.split-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.total-value {
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
}

.split-check {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
  margin-top: 10px;
  font-size: 14px;
  font-weight: 500;
}

.check-success {
  color: #67c23a;
}

.check-fail {
  color: #f56c6c;
}

.payment-split-summary {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-top: 15px;
}

.split-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.split-type {
  color: #606266;
}

.split-amount {
  color: #303133;
  font-weight: 600;
}
</style>
