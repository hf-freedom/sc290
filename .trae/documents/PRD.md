# 会员储值消费系统 PRD文档

## 1. 项目概述

### 1.1 项目名称
会员储值消费系统 (Member Value Storage System)

### 1.2 项目目标
构建一个完整的会员储值消费管理系统，支持储值充值、赠送金管理、消费扣减、退款处理、风控管理和会员等级体系，所有数据存储在本地内存中。

### 1.3 核心功能
- 储值账户管理（余额、赠送金、优惠券）
- 多级会员体系与差异化充值赠送
- 智能消费扣减策略（余额→赠送金→优惠券）
- 完整退款回退机制
- 赠送金有效期和使用范围控制
- 异常充值退款风控检测
- 组合支付与拆分记录
- 定时任务处理（过期赠送金、风险统计）

## 2. 功能需求详述

### 2.1 会员等级体系

#### 2.1.1 会员等级定义
| 等级 | 名称 | 充值赠送比例 | 赠送金有效期 |
|------|------|-------------|------------|
| LV1 | 普通会员 | 0% | 30天 |
| LV2 | 银卡会员 | 5% | 60天 |
| LV3 | 金卡会员 | 10% | 90天 |
| LV4 | 钻石会员 | 15% | 180天 |

#### 2.1.2 会员等级晋升
- 根据累计充值金额自动晋升
- LV1: 0-999元
- LV2: 1000-4999元
- LV3: 5000-19999元
- LV4: 20000元以上

### 2.2 储值账户管理

#### 2.2.1 账户结构
每个会员账户包含：
- **用户ID**: 唯一标识
- **会员等级**: LV1-LV4
- **账户余额**: 实际充值金额，可提现
- **赠送金余额**: 充值赠送金额，有有效期，不可提现
- **优惠券列表**: 代金券、折扣券等
- **累计充值**: 历史充值总额
- **累计消费**: 历史消费总额
- **创建时间**: 账户创建时间
- **最后活跃时间**: 最后一次交易时间

#### 2.2.2 充值功能
- 支持多种充值金额
- 充值时根据会员等级计算赠送金
- 记录充值流水
- 检测异常充值行为（风控）

**充值金额档位**:
- 100元
- 500元
- 1000元
- 2000元
- 5000元
- 自定义金额

#### 2.2.3 消费扣减策略
消费时按照以下优先级扣减：
1. **账户余额**: 优先使用真实余额
2. **赠送金**: 余额不足时使用赠送金
3. **优惠券**: 最后使用优惠券

**扣减规则**:
- 赠送金仅限指定范围使用（全部商品/指定品类）
- 优惠券有最低消费门槛
- 赠送金过期前7天系统提醒

#### 2.2.4 退款处理
退款时必须按原扣减路径反向回退：
- 如果消费使用了余额+赠送金+优惠券
- 退款时按相同比例分配退回
- 赠送金退回后有效期不变
- 优惠券状态恢复为未使用

### 2.3 赠送金管理

#### 2.3.1 有效期管理
- 赠送金有明确的有效期
- 有效期从充值时间开始计算
- 过期前7天发送提醒
- 过期后自动失效（定时任务）

#### 2.3.2 使用范围限制
- **全部商品**: 可用于所有消费
- **指定品类**: 只能在特定商品品类使用
- **限时活动**: 只能在特定活动时间使用

### 2.4 风控管理

#### 2.4.1 风控规则
以下行为触发风控：
1. **24小时内充值次数 > 5次**: 高频充值
2. **充值后退款间隔 < 1小时**: 快速退款
3. **30天内退款次数 > 3次**: 频繁退款
4. **单笔充值金额 > 5000元**: 大额充值
5. **账户余额长期为0**: 僵尸账户

#### 2.4.2 风控措施
- 进入风控的账户标记为"风险账户"
- 需要人工审核才能继续操作
- 记录风控日志
- 提供风控解除接口（需管理员权限）

### 2.5 组合支付

#### 2.5.1 支付场景
当账户余额不足以完成支付时：
- 支持余额 + 现金组合支付
- 自动计算最优支付比例
- 记录完整拆分明细

#### 2.5.2 拆分明细
每笔组合支付记录：
- 支付方式（余额/赠送金/现金/优惠券）
- 各方式支付金额
- 支付时间
- 订单金额
- 账户ID

### 2.6 定时任务

#### 2.6.1 赠送金过期检查
- 每小时执行一次
- 检查所有赠送金的有效期
- 将过期赠送金状态改为"已过期"
- 记录过期日志

#### 2.6.2 账户风险统计
- 每小时执行一次
- 扫描所有账户的充值退款行为
- 标记符合风控规则的账户
- 生成风险报告

#### 2.6.3 会员等级更新
- 每小时执行一次
- 根据累计充值金额更新会员等级
- 发送等级变更通知

## 3. 前端功能界面

### 3.1 会员账户页面
- 展示账户余额、赠送金、优惠券
- 会员等级展示
- 充值入口
- 消费记录入口
- 退款入口

### 3.2 充值页面
- 充值金额选择
- 会员等级优惠展示
- 充值记录列表
- 充值明细

### 3.3 消费页面
- 商品列表
- 购物车
- 支付方式选择
- 组合支付配置
- 支付成功展示

### 3.4 退款页面
- 退款订单列表
- 退款详情
- 退款状态追踪
- 退款规则说明

### 3.5 风控管理页面
- 风控账户列表
- 风控详情
- 风控解除操作
- 风控记录

### 3.6 管理后台
- 会员管理
- 订单管理
- 定时任务监控
- 系统统计

## 4. 后端API接口

### 4.1 账户接口
- `POST /api/account/create` - 创建账户
- `GET /api/account/{userId}` - 获取账户信息
- `GET /api/account/{userId}/balance` - 获取账户余额
- `PUT /api/account/{userId}/level` - 更新会员等级

### 4.2 充值接口
- `POST /api/recharge` - 充值
- `GET /api/recharge/{userId}/list` - 充值记录列表
- `GET /api/recharge/{orderId}` - 充值详情

### 4.3 消费接口
- `POST /api/consume` - 消费
- `GET /api/consume/{userId}/list` - 消费记录列表
- `POST /api/consume/combine` - 组合支付

### 4.4 退款接口
- `POST /api/refund` - 退款
- `GET /api/refund/{userId}/list` - 退款记录列表
- `GET /api/refund/{orderId}` - 退款详情

### 4.5 风控接口
- `GET /api/risk/list` - 风控账户列表
- `GET /api/risk/{userId}` - 账户风险详情
- `PUT /api/risk/{userId}/release` - 解除风控
- `GET /api/risk/stats` - 风险统计

### 4.6 定时任务接口
- `POST /api/task/expire-gift` - 手动触发过期检查
- `POST /api/task/risk-check` - 手动触发风控检查
- `POST /api/task/level-update` - 手动触发等级更新
- `GET /api/task/stats` - 定时任务统计

## 5. 数据模型

### 5.1 账户模型 (Account)
```
- userId: String (用户ID)
- level: Integer (会员等级 1-4)
- balance: BigDecimal (账户余额)
- giftBalance: BigDecimal (赠送金余额)
- totalRecharge: BigDecimal (累计充值)
- totalConsume: BigDecimal (累计消费)
- riskLevel: Integer (风控等级 0-正常 1-关注 2-限制)
- riskReason: String (风控原因)
- riskTime: LocalDateTime (风控时间)
- coupons: List<Coupon> (优惠券列表)
- gifts: List<Gift> (赠送金记录列表)
- createTime: LocalDateTime
- updateTime: LocalDateTime
- lastActiveTime: LocalDateTime
```

### 5.2 充值记录模型 (RechargeRecord)
```
- orderId: String (订单ID)
- userId: String (用户ID)
- amount: BigDecimal (充值金额)
- giftAmount: BigDecimal (赠送金额)
- payMethod: String (支付方式)
- status: String (状态: SUCCESS/FAILED/PENDING)
- createTime: LocalDateTime
```

### 5.3 消费记录模型 (ConsumeRecord)
```
- orderId: String (订单ID)
- userId: String (用户ID)
- amount: BigDecimal (消费金额)
- balanceUsed: BigDecimal (余额使用)
- giftUsed: BigDecimal (赠送金使用)
- cashUsed: BigDecimal (现金使用)
- couponUsed: BigDecimal (优惠券使用)
- couponIds: List<String> (使用的优惠券ID)
- items: List<OrderItem> (消费明细)
- status: String (状态)
- createTime: LocalDateTime
```

### 5.4 退款记录模型 (RefundRecord)
```
- refundId: String (退款ID)
- orderId: String (原订单ID)
- userId: String (用户ID)
- refundAmount: BigDecimal (退款金额)
- balanceRefund: BigDecimal (余额退回)
- giftRefund: BigDecimal (赠送金退回)
- cashRefund: BigDecimal (现金退回)
- couponRefund: List<String> (退回的优惠券ID)
- reason: String (退款原因)
- status: String (状态)
- createTime: LocalDateTime
```

### 5.5 赠送金模型 (Gift)
```
- giftId: String (赠送金ID)
- userId: String (用户ID)
- amount: BigDecimal (赠送金额)
- remainAmount: BigDecimal (剩余金额)
- expireTime: LocalDateTime (过期时间)
- scope: String (使用范围: ALL/PARTIAL)
- categories: List<String> (可用品类)
- status: String (状态: ACTIVE/USED/EXPIRED)
- createTime: LocalDateTime
```

### 5.6 优惠券模型 (Coupon)
```
- couponId: String (优惠券ID)
- userId: String (用户ID)
- type: String (类型: CASH/DISCOUNT)
- amount: BigDecimal (面额或折扣)
- minConsume: BigDecimal (最低消费)
- expireTime: LocalDateTime (过期时间)
- status: String (状态: UNUSED/USED/EXPIRED)
- createTime: LocalDateTime
```

### 5.7 组合支付明细模型 (PaymentSplit)
```
- splitId: String (拆分ID)
- orderId: String (订单ID)
- balanceAmount: BigDecimal (余额支付)
- giftAmount: BigDecimal (赠送金支付)
- cashAmount: BigDecimal (现金支付)
- couponAmount: BigDecimal (优惠券支付)
- createTime: LocalDateTime
```

## 6. 非功能性需求

### 6.1 性能需求
- API响应时间 < 200ms
- 支持100个并发用户
- 数据存储在本地内存，使用HashMap管理

### 6.2 安全需求
- 所有接口需要进行参数校验
- 敏感操作需要记录日志
- 风控账户需要额外验证

### 6.3 可用性需求
- 前端需要友好的错误提示
- 后端需要完善的异常处理
- 提供系统健康检查接口

## 7. 技术栈

### 7.1 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **UI库**: Element Plus
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **端口**: 3003

### 7.2 后端
- **框架**: SpringBoot 2.7.x
- **Java版本**: Java 8
- **构建工具**: Maven
- **端口**: 8003
- **数据存储**: 本地内存 (ConcurrentHashMap)

## 8. 项目结构

### 8.1 后端结构
```
backend/
├── src/main/java/com/member/storage/
│   ├── StorageApplication.java
│   ├── config/
│   │   └── WebConfig.java
│   ├── controller/
│   │   ├── AccountController.java
│   │   ├── RechargeController.java
│   │   ├── ConsumeController.java
│   │   ├── RefundController.java
│   │   ├── RiskController.java
│   │   └── TaskController.java
│   ├── service/
│   │   ├── AccountService.java
│   │   ├── RechargeService.java
│   │   ├── ConsumeService.java
│   │   ├── RefundService.java
│   │   ├── RiskService.java
│   │   ├── GiftService.java
│   │   └── TaskService.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── RechargeRecord.java
│   │   ├── ConsumeRecord.java
│   │   ├── RefundRecord.java
│   │   ├── Gift.java
│   │   ├── Coupon.java
│   │   └── PaymentSplit.java
│   ├── repository/
│   │   └── MemoryStore.java
│   ├── dto/
│   │   └── (各种请求响应DTO)
│   ├── enums/
│   │   ├── MemberLevel.java
│   │   ├── OrderStatus.java
│   │   └── RiskLevel.java
│   └── util/
│       └── (工具类)
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### 8.2 前端结构
```
frontend/
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── views/
│   │   ├── AccountPage.vue
│   │   ├── RechargePage.vue
│   │   ├── ConsumePage.vue
│   │   ├── RefundPage.vue
│   │   ├── RiskPage.vue
│   │   └── DashboardPage.vue
│   ├── components/
│   │   └── (可复用组件)
│   ├── stores/
│   │   ├── account.js
│   │   └── app.js
│   ├── api/
│   │   └── index.js
│   ├── router/
│   │   └── index.js
│   └── assets/
│       └── styles/
├── index.html
├── vite.config.js
└── package.json
```

## 9. 用户操作流程

### 9.1 充值流程
1. 用户选择充值金额
2. 系统根据会员等级计算赠送金
3. 用户确认支付
4. 系统生成充值订单
5. 更新账户余额和赠送金
6. 记录充值流水
7. 检查风控规则
8. 返回充值结果

### 9.2 消费流程
1. 用户选择商品
2. 系统计算订单金额
3. 用户选择支付方式
4. 系统按优先级扣减（余额→赠送金→优惠券）
5. 如需组合支付，记录拆分明细
6. 生成消费订单
7. 更新账户余额
8. 返回消费结果

### 9.3 退款流程
1. 用户发起退款申请
2. 系统验证订单状态
3. 系统按原扣减路径计算退款分配
4. 执行退款操作
5. 恢复账户余额和赠送金
6. 恢复优惠券状态
7. 记录退款流水
8. 返回退款结果

## 10. 验收标准

### 10.1 功能验收
- [ ] 成功创建会员账户
- [ ] 完成充值并获得赠送金
- [ ] 成功消费并正确扣减
- [ ] 成功退款并正确回退
- [ ] 赠送金过期正确处理
- [ ] 风控规则正确触发
- [ ] 组合支付正确计算
- [ ] 会员等级正确更新

### 10.2 界面验收
- [ ] 所有页面正常访问
- [ ] 数据展示正确
- [ ] 操作流程顺畅
- [ ] 错误提示友好
- [ ] 响应式布局良好

### 10.3 性能验收
- [ ] API响应时间正常
- [ ] 页面加载速度良好
- [ ] 无明显卡顿
