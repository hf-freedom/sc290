# 会员储值消费系统 技术架构文档

## 1. 系统架构概述

### 1.1 整体架构
采用前后端分离架构：
- **前端**: Vue 3 单页应用 (SPA)
- **后端**: SpringBoot RESTful API
- **数据存储**: 本地内存 (ConcurrentHashMap)
- **通信**: HTTP JSON

### 1.2 技术选型
| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 前端框架 | Vue | 3.x | 渐进式JavaScript框架 |
| 前端构建 | Vite | 4.x | 下一代前端构建工具 |
| UI组件库 | Element Plus | 2.x | Vue 3 UI组件库 |
| 状态管理 | Pinia | 2.x | Vue状态管理库 |
| HTTP客户端 | Axios | 1.x | Promise HTTP客户端 |
| 后端框架 | SpringBoot | 2.7.x | Java企业级框架 |
| Java版本 | JDK | 8 | 稳定版本 |
| 构建工具 | Maven | 3.x | 项目管理和构建 |
| 数据存储 | ConcurrentHashMap | - | 线程安全的内存存储 |

## 2. 项目初始化

### 2.1 后端项目初始化

#### 2.1.1 Maven pom.xml 配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.member</groupId>
    <artifactId>storage</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Member Storage System</name>
    <description>会员储值消费系统</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- SpringBoot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- SpringBoot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 2.1.2 application.yml 配置
```yaml
server:
  port: 8003

spring:
  application:
    name: member-storage-system
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

logging:
  level:
    root: INFO
    com.member.storage: DEBUG
```

### 2.2 前端项目初始化

#### 2.2.1 package.json 配置
```json
{
  "name": "member-storage-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "3.3.4",
    "vue-router": "4.2.4",
    "pinia": "2.1.6",
    "axios": "1.5.0",
    "element-plus": "2.3.12"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "4.3.4",
    "vite": "4.4.9"
  }
}
```

#### 2.2.2 vite.config.js 配置
```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3003,
    proxy: {
      '/api': {
        target: 'http://localhost:8003',
        changeOrigin: true
      }
    }
  }
})
```

## 3. 后端详细设计

### 3.1 核心类结构

#### 3.1.1 启动类 (StorageApplication.java)
```java
package com.member.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
    }
}
```

#### 3.1.2 Web配置类 (WebConfig.java)
```java
package com.member.storage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3003")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 3.2 枚举类设计

#### 3.2.1 会员等级枚举 (MemberLevel.java)
```java
package com.member.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberLevel {
    LV1(1, "普通会员", 0, 30),
    LV2(2, "银卡会员", 0.05, 60),
    LV3(3, "金卡会员", 0.10, 90),
    LV4(4, "钻石会员", 0.15, 180);

    private final int level;
    private final String name;
    private final double giftRate;
    private final int giftDays;
}
```

#### 3.2.2 订单状态枚举 (OrderStatus.java)
```java
package com.member.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING("待支付"),
    SUCCESS("成功"),
    FAILED("失败"),
    REFUNDED("已退款"),
    CANCELLED("已取消");

    private final String description;
}
```

#### 3.2.3 风控等级枚举 (RiskLevel.java)
```java
package com.member.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RiskLevel {
    NORMAL(0, "正常"),
    ATTENTION(1, "关注"),
    RESTRICTED(2, "限制");

    private final int level;
    private final String description;
}
```

### 3.3 数据模型设计

#### 3.3.1 账户模型 (Account.java)
```java
package com.member.storage.model;

import com.member.storage.enums.MemberLevel;
import com.member.storage.enums.RiskLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String userId;
    private MemberLevel level;
    private BigDecimal balance;
    private BigDecimal giftBalance;
    private BigDecimal totalRecharge;
    private BigDecimal totalConsume;
    private RiskLevel riskLevel;
    private String riskReason;
    private LocalDateTime riskTime;
    private List<Gift> gifts;
    private List<Coupon> coupons;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastActiveTime;
}
```

#### 3.3.2 充值记录模型 (RechargeRecord.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RechargeRecord {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal giftAmount;
    private String payMethod;
    private String status;
    private LocalDateTime createTime;
}
```

#### 3.3.3 消费记录模型 (ConsumeRecord.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsumeRecord {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal balanceUsed;
    private BigDecimal giftUsed;
    private BigDecimal cashUsed;
    private BigDecimal couponUsed;
    private List<String> couponIds;
    private List<OrderItem> items;
    private String status;
    private LocalDateTime createTime;
}
```

#### 3.3.4 退款记录模型 (RefundRecord.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RefundRecord {
    private String refundId;
    private String orderId;
    private String userId;
    private BigDecimal refundAmount;
    private BigDecimal balanceRefund;
    private BigDecimal giftRefund;
    private BigDecimal cashRefund;
    private List<String> couponRefund;
    private String reason;
    private String status;
    private LocalDateTime createTime;
}
```

#### 3.3.5 赠送金模型 (Gift.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Gift {
    private String giftId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal remainAmount;
    private LocalDateTime expireTime;
    private String scope;
    private List<String> categories;
    private String status;
    private LocalDateTime createTime;
}
```

#### 3.3.6 优惠券模型 (Coupon.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private String couponId;
    private String userId;
    private String type;
    private BigDecimal amount;
    private BigDecimal minConsume;
    private LocalDateTime expireTime;
    private String status;
    private LocalDateTime createTime;
}
```

#### 3.3.7 组合支付明细模型 (PaymentSplit.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentSplit {
    private String splitId;
    private String orderId;
    private BigDecimal balanceAmount;
    private BigDecimal giftAmount;
    private BigDecimal cashAmount;
    private BigDecimal couponAmount;
    private LocalDateTime createTime;
}
```

#### 3.3.8 订单项模型 (OrderItem.java)
```java
package com.member.storage.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private String itemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String category;
}
```

### 3.4 数据存储设计

#### 3.4.1 内存存储类 (MemoryStore.java)
```java
package com.member.storage.repository;

import com.member.storage.model.*;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Component
public class MemoryStore {
    public final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, RechargeRecord> recharges = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, ConsumeRecord> consumes = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, RefundRecord> refunds = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, PaymentSplit> splits = new ConcurrentHashMap<>();
    
    public List<RechargeRecord> getRechargeList(String userId) {
        List<RechargeRecord> list = new CopyOnWriteArrayList<>();
        recharges.forEach((id, record) -> {
            if (record.getUserId().equals(userId)) {
                list.add(record);
            }
        });
        return list;
    }
    
    public List<ConsumeRecord> getConsumeList(String userId) {
        List<ConsumeRecord> list = new CopyOnWriteArrayList<>();
        consumes.forEach((id, record) -> {
            if (record.getUserId().equals(userId)) {
                list.add(record);
            }
        });
        return list;
    }
    
    public List<RefundRecord> getRefundList(String userId) {
        List<RefundRecord> list = new CopyOnWriteArrayList<>();
        refunds.forEach((id, record) -> {
            if (record.getUserId().equals(userId)) {
                list.add(record);
            }
        });
        return list;
    }
}
```

### 3.5 核心业务服务设计

#### 3.5.1 账户服务 (AccountService.java)
主要功能：
- 创建账户
- 查询账户信息
- 更新会员等级
- 管理赠送金和优惠券

#### 3.5.2 充值服务 (RechargeService.java)
主要功能：
- 执行充值
- 计算赠送金
- 更新账户余额
- 检查风控规则

#### 3.5.3 消费服务 (ConsumeService.java)
主要功能：
- 执行消费扣减
- 实现优先级扣减策略
- 处理组合支付
- 记录拆分明细

#### 3.5.4 退款服务 (RefundService.java)
主要功能：
- 执行退款
- 按原路径回退
- 恢复优惠券状态
- 记录退款流水

#### 3.5.5 风控服务 (RiskService.java)
主要功能：
- 检测风控规则
- 标记风险账户
- 提供风控统计
- 管理风控解除

#### 3.5.6 赠送金服务 (GiftService.java)
主要功能：
- 管理赠送金生命周期
- 检查有效期
- 验证使用范围
- 处理过期赠送金

#### 3.5.7 定时任务服务 (TaskService.java)
主要功能：
- 处理赠送金过期
- 执行风控检查
- 更新会员等级
- 生成统计报告

### 3.6 Controller层设计

#### 3.6.1 账户控制器 (AccountController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.AccountService;
import com.member.storage.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@RequestParam String userId) {
        Account account = accountService.createAccount(userId);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<Account> getAccount(@PathVariable String userId) {
        Account account = accountService.getAccount(userId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Object> getBalance(@PathVariable String userId) {
        Account account = accountService.getAccount(userId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Object() {
            public BigDecimal balance = account.getBalance();
            public BigDecimal giftBalance = account.getGiftBalance();
        });
    }
}
```

#### 3.6.2 充值控制器 (RechargeController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.RechargeService;
import com.member.storage.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recharge")
public class RechargeController {
    
    @Autowired
    private RechargeService rechargeService;
    
    @PostMapping
    public ResponseEntity<RechargeRecord> recharge(@RequestBody RechargeRequest request) {
        RechargeRecord record = rechargeService.recharge(
            request.getUserId(),
            request.getAmount(),
            request.getPayMethod()
        );
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/{userId}/list")
    public ResponseEntity<List<RechargeRecord>> getRechargeList(@PathVariable String userId) {
        List<RechargeRecord> list = rechargeService.getRechargeList(userId);
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<RechargeRecord> getRechargeDetail(@PathVariable String orderId) {
        RechargeRecord record = rechargeService.getRecharge(orderId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
}
```

#### 3.6.3 消费控制器 (ConsumeController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.ConsumeService;
import com.member.storage.model.ConsumeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/consume")
public class ConsumeController {
    
    @Autowired
    private ConsumeService consumeService;
    
    @PostMapping
    public ResponseEntity<ConsumeRecord> consume(@RequestBody ConsumeRequest request) {
        ConsumeRecord record = consumeService.consume(
            request.getUserId(),
            request.getItems(),
            request.getCouponIds()
        );
        return ResponseEntity.ok(record);
    }
    
    @PostMapping("/combine")
    public ResponseEntity<ConsumeRecord> combinePay(@RequestBody CombinePayRequest request) {
        ConsumeRecord record = consumeService.combinePay(
            request.getUserId(),
            request.getItems(),
            request.getBalanceAmount(),
            request.getCashAmount()
        );
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/{userId}/list")
    public ResponseEntity<List<ConsumeRecord>> getConsumeList(@PathVariable String userId) {
        List<ConsumeRecord> list = consumeService.getConsumeList(userId);
        return ResponseEntity.ok(list);
    }
}
```

#### 3.6.4 退款控制器 (RefundController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.RefundService;
import com.member.storage.model.RefundRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/refund")
public class RefundController {
    
    @Autowired
    private RefundService refundService;
    
    @PostMapping
    public ResponseEntity<RefundRecord> refund(@RequestBody RefundRequest request) {
        RefundRecord record = refundService.refund(
            request.getOrderId(),
            request.getReason()
        );
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/{userId}/list")
    public ResponseEntity<List<RefundRecord>> getRefundList(@PathVariable String userId) {
        List<RefundRecord> list = refundService.getRefundList(userId);
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<RefundRecord> getRefundDetail(@PathVariable String orderId) {
        RefundRecord record = refundService.getRefund(orderId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
}
```

#### 3.6.5 风控控制器 (RiskController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
    
    @Autowired
    private RiskService riskService;
    
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getRiskList() {
        List<Map<String, Object>> list = riskService.getRiskList();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getRiskDetail(@PathVariable String userId) {
        Map<String, Object> detail = riskService.getRiskDetail(userId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }
    
    @PutMapping("/{userId}/release")
    public ResponseEntity<Object> releaseRisk(@PathVariable String userId) {
        riskService.releaseRisk(userId);
        return ResponseEntity.ok().body("{\"message\": \"风控已解除\"}");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRiskStats() {
        Map<String, Object> stats = riskService.getRiskStats();
        return ResponseEntity.ok(stats);
    }
}
```

#### 3.6.6 定时任务控制器 (TaskController.java)
```java
package com.member.storage.controller;

import com.member.storage.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @PostMapping("/expire-gift")
    public ResponseEntity<Map<String, Object>> expireGift() {
        int count = taskService.processExpiredGifts();
        return ResponseEntity.ok().body(Map.of("processed", count));
    }
    
    @PostMapping("/risk-check")
    public ResponseEntity<Map<String, Object>> riskCheck() {
        int count = taskService.checkRisks();
        return ResponseEntity.ok().body(Map.of("checked", count));
    }
    
    @PostMapping("/level-update")
    public ResponseEntity<Map<String, Object>> levelUpdate() {
        int count = taskService.updateMemberLevels();
        return ResponseEntity.ok().body(Map.of("updated", count));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTaskStats() {
        Map<String, Object> stats = taskService.getTaskStats();
        return ResponseEntity.ok(stats);
    }
}
```

### 3.7 定时任务实现

#### 3.7.1 定时任务配置
```java
package com.member.storage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ScheduledConfig {
    // 定时任务在 TaskService 中实现
}
```

#### 3.7.2 定时任务服务 (TaskService.java)
```java
package com.member.storage.service;

import com.member.storage.repository.MemoryStore;
import com.member.storage.model.Account;
import com.member.storage.model.Gift;
import com.member.storage.enums.MemberLevel;
import com.member.storage.enums.RiskLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {
    
    @Autowired
    private MemoryStore store;
    
    private final AtomicInteger expireGiftCount = new AtomicInteger(0);
    private final AtomicInteger riskCheckCount = new AtomicInteger(0);
    private final AtomicInteger levelUpdateCount = new AtomicInteger(0);
    private LocalDateTime lastExpireGiftTime;
    private LocalDateTime lastRiskCheckTime;
    private LocalDateTime lastLevelUpdateTime;
    
    @Scheduled(fixedRate = 3600000) // 每小时执行
    public void scheduledTasks() {
        processExpiredGifts();
        checkRisks();
        updateMemberLevels();
    }
    
    public int processExpiredGifts() {
        int count = 0;
        LocalDateTime now = LocalDateTime.now();
        store.accounts.forEach((userId, account) -> {
            if (account.getGifts() != null) {
                for (Gift gift : account.getGifts()) {
                    if ("ACTIVE".equals(gift.getStatus()) && 
                        gift.getExpireTime().isBefore(now)) {
                        gift.setStatus("EXPIRED");
                        account.setGiftBalance(
                            account.getGiftBalance().subtract(gift.getRemainAmount())
                        );
                        count++;
                    }
                }
            }
        });
        expireGiftCount.addAndGet(count);
        lastExpireGiftTime = LocalDateTime.now();
        return count;
    }
    
    public int checkRisks() {
        int count = 0;
        store.accounts.forEach((userId, account) -> {
            boolean risk = checkAccountRisk(account);
            if (risk && account.getRiskLevel() == RiskLevel.NORMAL) {
                account.setRiskLevel(RiskLevel.ATTENTION);
                count++;
            }
        });
        riskCheckCount.addAndGet(count);
        lastRiskCheckTime = LocalDateTime.now();
        return count;
    }
    
    public int updateMemberLevels() {
        int count = 0;
        store.accounts.forEach((userId, account) -> {
            MemberLevel newLevel = calculateMemberLevel(account.getTotalRecharge());
            if (newLevel.getLevel() > account.getLevel().getLevel()) {
                account.setLevel(newLevel);
                count++;
            }
        });
        levelUpdateCount.addAndGet(count);
        lastLevelUpdateTime = LocalDateTime.now();
        return count;
    }
    
    private boolean checkAccountRisk(Account account) {
        // 实现风控检查逻辑
        return false;
    }
    
    private MemberLevel calculateMemberLevel(BigDecimal totalRecharge) {
        if (totalRecharge.compareTo(new BigDecimal("20000")) >= 0) {
            return MemberLevel.LV4;
        } else if (totalRecharge.compareTo(new BigDecimal("5000")) >= 0) {
            return MemberLevel.LV3;
        } else if (totalRecharge.compareTo(new BigDecimal("1000")) >= 0) {
            return MemberLevel.LV2;
        }
        return MemberLevel.LV1;
    }
    
    public Map<String, Object> getTaskStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("expireGiftCount", expireGiftCount.get());
        stats.put("riskCheckCount", riskCheckCount.get());
        stats.put("levelUpdateCount", levelUpdateCount.get());
        stats.put("lastExpireGiftTime", lastExpireGiftTime);
        stats.put("lastRiskCheckTime", lastRiskCheckTime);
        stats.put("lastLevelUpdateTime", lastLevelUpdateTime);
        return stats;
    }
}
```

## 4. 前端详细设计

### 4.1 项目入口文件

#### 4.1.1 main.js
```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
```

#### 4.1.2 App.vue
```vue
<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
#app {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

body {
  margin: 0;
  padding: 0;
  background: #f5f7fa;
}

* {
  box-sizing: border-box;
}
</style>
```

#### 4.1.3 index.html
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>会员储值消费系统</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

### 4.2 路由设计

#### 4.2.1 router/index.js
```javascript
import { createRouter, createWebHistory } from 'vue-router'
import DashboardPage from '../views/DashboardPage.vue'
import AccountPage from '../views/AccountPage.vue'
import RechargePage from '../views/RechargePage.vue'
import ConsumePage from '../views/ConsumePage.vue'
import RefundPage from '../views/RefundPage.vue'
import RiskPage from '../views/RiskPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardPage
  },
  {
    path: '/account',
    name: 'Account',
    component: AccountPage
  },
  {
    path: '/recharge',
    name: 'Recharge',
    component: RechargePage
  },
  {
    path: '/consume',
    name: 'Consume',
    component: ConsumePage
  },
  {
    path: '/refund',
    name: 'Refund',
    component: RefundPage
  },
  {
    path: '/risk',
    name: 'Risk',
    component: RiskPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

### 4.3 API封装

#### 4.3.1 api/index.js
```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 账户接口
export const accountAPI = {
  create: (userId) => api.post('/account/create', null, { params: { userId } }),
  get: (userId) => api.get(`/account/${userId}`),
  getBalance: (userId) => api.get(`/account/${userId}/balance`)
}

// 充值接口
export const rechargeAPI = {
  recharge: (data) => api.post('/recharge', data),
  list: (userId) => api.get(`/recharge/${userId}/list`),
  detail: (orderId) => api.get(`/recharge/${orderId}`)
}

// 消费接口
export const consumeAPI = {
  consume: (data) => api.post('/consume', data),
  combinePay: (data) => api.post('/consume/combine', data),
  list: (userId) => api.get(`/consume/${userId}/list`)
}

// 退款接口
export const refundAPI = {
  refund: (data) => api.post('/refund', data),
  list: (userId) => api.get(`/refund/${userId}/list`),
  detail: (orderId) => api.get(`/refund/${orderId}`)
}

// 风控接口
export const riskAPI = {
  list: () => api.get('/risk/list'),
  detail: (userId) => api.get(`/risk/${userId}`),
  release: (userId) => api.put(`/risk/${userId}/release`),
  stats: () => api.get('/risk/stats')
}

// 定时任务接口
export const taskAPI = {
  expireGift: () => api.post('/task/expire-gift'),
  riskCheck: () => api.post('/task/risk-check'),
  levelUpdate: () => api.post('/task/level-update'),
  stats: () => api.get('/task/stats')
}

export default api
```

### 4.4 状态管理

#### 4.4.1 stores/account.js
```javascript
import { defineStore } from 'pinia'
import { accountAPI } from '../api'

export const useAccountStore = defineStore('account', {
  state: () => ({
    currentAccount: null,
    loading: false
  }),
  
  actions: {
    async createAccount(userId) {
      this.loading = true
      try {
        const response = await accountAPI.create(userId)
        this.currentAccount = response.data
        return response.data
      } catch (error) {
        console.error('创建账户失败', error)
        throw error
      } finally {
        this.loading = false
      }
    },
    
    async fetchAccount(userId) {
      this.loading = true
      try {
        const response = await accountAPI.get(userId)
        this.currentAccount = response.data
        return response.data
      } catch (error) {
        console.error('获取账户失败', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})
```

#### 4.4.2 stores/app.js
```javascript
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    userId: 'user_' + Date.now(),
    sidebar: true,
    theme: 'light'
  }),
  
  actions: {
    setUserId(userId) {
      this.userId = userId
    }
  }
})
```

### 4.5 页面组件设计

#### 4.5.1 DashboardPage.vue - 首页仪表盘
主要功能：
- 系统概览统计
- 快速操作入口
- 最近交易记录
- 账户状态展示

#### 4.5.2 AccountPage.vue - 会员账户页面
主要功能：
- 账户信息展示
- 余额和赠送金显示
- 优惠券列表
- 会员等级展示
- 账户详情编辑

#### 4.5.3 RechargePage.vue - 充值页面
主要功能：
- 充值金额选择
- 会员等级优惠展示
- 充值表单
- 充值记录列表
- 充值明细查询

#### 4.5.4 ConsumePage.vue - 消费页面
主要功能：
- 商品列表展示
- 购物车管理
- 支付方式选择
- 组合支付配置
- 消费确认和支付

#### 4.5.5 RefundPage.vue - 退款页面
主要功能：
- 退款订单列表
- 退款详情展示
- 退款申请表单
- 退款进度追踪

#### 4.5.6 RiskPage.vue - 风控管理页面
主要功能：
- 风控账户列表
- 风控详情展示
- 风控规则说明
- 风控解除操作
- 风控统计报表

### 4.6 公共组件设计

#### 4.6.1 HeaderComponent.vue
导航栏组件，包含：
- 系统标题
- 用户信息
- 导航菜单
- 快捷操作

#### 4.6.2 SidebarComponent.vue
侧边栏组件，包含：
- 功能菜单
- 快速链接
- 统计信息

#### 4.6.3 BalanceCard.vue
余额卡片组件，展示：
- 账户余额
- 赠送金余额
- 优惠券数量
- 会员等级

#### 4.6.4 TransactionList.vue
交易列表组件，包含：
- 充值记录
- 消费记录
- 退款记录
- 筛选和搜索

## 5. 数据库设计（内存）

### 5.1 数据结构

#### 5.1.1 账户数据 (accounts)
Key: userId
Value: Account对象

#### 5.1.2 充值记录 (recharges)
Key: orderId
Value: RechargeRecord对象

#### 5.1.3 消费记录 (consumes)
Key: orderId
Value: ConsumeRecord对象

#### 5.1.4 退款记录 (refunds)
Key: refundId
Value: RefundRecord对象

#### 5.1.5 支付拆分 (splits)
Key: splitId
Value: PaymentSplit对象

### 5.2 数据索引

为了提高查询效率，建立以下索引：

#### 5.2.1 用户索引
- userId -> Account
- 用于快速查找用户账户

#### 5.2.2 订单索引
- orderId -> Record
- 用于快速查找订单详情

#### 5.2.3 时间索引
- createTime -> List<Record>
- 用于按时间排序查询

## 6. 业务规则引擎

### 6.1 会员等级晋升规则
```
IF totalRecharge >= 20000 THEN level = LV4
ELSE IF totalRecharge >= 5000 THEN level = LV3
ELSE IF totalRecharge >= 1000 THEN level = LV2
ELSE level = LV1
```

### 6.2 赠送金计算规则
```
giftAmount = rechargeAmount * memberLevel.giftRate
giftExpireTime = now + memberLevel.giftDays
```

### 6.3 消费扣减优先级规则
```
IF balance >= amount THEN
    use balance only
ELSE IF (balance + giftBalance) >= amount THEN
    use balance first, then giftBalance
ELSE
    use balance + giftBalance + coupon + cash
```

### 6.4 退款回退规则
```
balanceRefund = refundAmount * (consumeRecord.balanceUsed / consumeRecord.amount)
giftRefund = refundAmount * (consumeRecord.giftUsed / consumeRecord.amount)
cashRefund = refundAmount * (consumeRecord.cashUsed / consumeRecord.amount)
restore coupons
```

### 6.5 风控检测规则
```
IF rechargeCountLast24h > 5 THEN risk = true
IF refundIntervalLast < 1h THEN risk = true
IF refundCountLast30d > 3 THEN risk = true
IF singleRecharge > 5000 THEN risk = true
```

## 7. 安全性设计

### 7.1 参数校验
- 所有API请求参数进行校验
- 使用JSR-303注解验证
- 前端表单双重验证

### 7.2 异常处理
- 统一异常拦截
- 错误码和错误信息规范
- 前端友好错误提示

### 7.3 日志记录
- 操作日志记录
- 错误日志记录
- 风控日志记录

### 7.4 跨域配置
- CORS跨域支持
- 前端开发服务器代理配置

## 8. 性能优化

### 8.1 后端优化
- 使用ConcurrentHashMap保证线程安全
- 合理使用缓存
- 异步处理非核心逻辑

### 8.2 前端优化
- Vue 3 Composition API优化
- 按需加载组件
- 图片和资源优化

### 8.3 网络优化
- HTTP/2支持
- GZIP压缩
- 请求合并

## 9. 部署架构

### 9.1 开发环境
- 后端: http://localhost:8003
- 前端: http://localhost:3003
- 前端代理: /api -> http://localhost:8003

### 9.2 启动脚本

#### 9.2.1 后端启动脚本 (start-backend.sh)
```bash
#!/bin/bash
cd backend
mvn clean package
java -jar target/storage-1.0.0.jar
```

#### 9.2.2 前端启动脚本 (start-frontend.sh)
```bash
#!/bin/bash
cd frontend
npm install
npm run dev
```

## 10. 测试策略

### 10.1 单元测试
- Service层业务逻辑测试
- 工具类测试

### 10.2 集成测试
- API接口测试
- 数据流测试

### 10.3 手动测试
- 完整业务流程测试
- 边界条件测试
- 异常场景测试

## 11. 项目规范

### 11.1 命名规范
- Java类名: UpperCamelCase
- Java方法名: lowerCamelCase
- Java变量名: lowerCamelCase
- Vue组件名: PascalCase
- API路径: /api/resource/action (小写加连字符)

### 11.2 代码风格
- 缩进: 4空格
- 行宽: 120字符
- 注释: Javadoc风格
- 提交规范: feat/fix/docs/style/refactor/test/chore

### 11.3 Git工作流
- 主分支: main
- 开发分支: develop
- 功能分支: feature/xxx
- 修复分支: hotfix/xxx
