# 会员储值消费系统 - 前端

## 项目介绍

这是一个基于 Vue 3 + Vite 的会员储值消费系统前端项目，提供完整的会员账户管理、充值、消费、退款和风控管理功能。

## 技术栈

- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 新一代前端构建工具
- **Element Plus** - 基于 Vue 3 的组件库
- **Pinia** - Vue 3 状态管理库
- **Axios** - HTTP 请求库
- **Vue Router** - Vue 3 官方路由管理

## 功能模块

### 1. DashboardPage - 首页仪表盘
- 系统概览统计（会员总数、交易数、余额等）
- 快速操作入口
- 最近交易记录
- 系统通知
- 账户状态概览

### 2. AccountPage - 会员账户
- 账户基本信息展示
- 资产概览（余额、赠送金、积分）
- 优惠券管理
- 交易记录查询
- 会员等级权益展示

### 3. RechargePage - 充值
- 充值金额选择
- 自定义充值金额
- 多种支付方式（微信、支付宝、银行卡）
- 充值优惠活动
- 充值记录查询
- 会员等级折扣说明

### 4. ConsumePage - 消费
- 商品列表展示
- 购物车管理
- 多种支付方式（余额、赠送金、组合支付）
- 会员折扣自动计算
- 优惠券使用
- 订单支付

### 5. RefundPage - 退款
- 退款订单列表
- 退款申请
- 退款审核（通过/拒绝）
- 退款进度跟踪
- 退款统计

### 6. RiskPage - 风控管理
- 风控账户列表
- 风控详情查看
- 账户冻结/解冻
- 风控记录管理
- 风险等级分类

## 项目结构

```
frontend/
├── package.json          # 项目配置
├── vite.config.js        # Vite 配置
├── index.html            # HTML 入口
├── src/
│   ├── main.js           # 应用入口
│   ├── App.vue           # 根组件
│   ├── api/
│   │   └── index.js      # API 接口配置
│   ├── router/
│   │   └── index.js      # 路由配置
│   ├── stores/
│   │   ├── account.js    # 账户状态管理
│   │   └── app.js        # 应用状态管理
│   ├── views/
│   │   ├── DashboardPage.vue   # 首页仪表盘
│   │   ├── AccountPage.vue     # 会员账户
│   │   ├── RechargePage.vue    # 充值
│   │   ├── ConsumePage.vue     # 消费
│   │   ├── RefundPage.vue      # 退款
│   │   └── RiskPage.vue        # 风控管理
│   └── assets/
│       └── styles/
│           └── main.css  # 全局样式
```

## 安装和运行

### 安装依赖

```bash
cd frontend
npm install
```

### 开发模式运行

```bash
npm run dev
```

项目将在 http://localhost:3003 启动。

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 配置说明

### API 代理配置

项目配置了 API 代理，将 `/api` 请求代理到 `http://localhost:8003`。

修改 `vite.config.js` 中的 `server.proxy` 可调整代理配置。

### 端口配置

默认端口为 3003，可在 `vite.config.js` 的 `server.port` 中修改。

## 功能特点

1. **响应式设计** - 适配桌面和移动设备
2. **状态管理** - 使用 Pinia 进行全局状态管理
3. **路由管理** - 完整的路由配置和导航
4. **表单验证** - 完整的表单验证逻辑
5. **交互反馈** - 友好的消息提示和对话框
6. **数据可视化** - 统计卡片和图表展示
7. **权限管理** - 基于角色的功能展示

## 注意事项

1. 首次运行需要安装所有依赖
2. 确保后端服务运行在 http://localhost:8003
3. 浏览器需要支持 ES6+ 和 CSS3

## 许可证

MIT License
