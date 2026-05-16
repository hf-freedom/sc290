package com.member.storage.service;

import com.member.storage.enums.RiskLevel;
import com.member.storage.model.Account;
import com.member.storage.model.ConsumeRecord;
import com.member.storage.model.RechargeRecord;
import com.member.storage.model.RefundRecord;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MemoryStore memoryStore;

    public RiskLevel checkRisk(String userId) {
        Account account = accountService.getAccountOrNull(userId);
        if (account == null) {
            return RiskLevel.NORMAL;
        }
        
        int riskScore = 0;
        List<String> riskReasons = new ArrayList<>();
        
        List<RechargeRecord> rechargeRecords = memoryStore.getRechargeRecordsByUserId(userId);
        List<ConsumeRecord> consumeRecords = memoryStore.getConsumeRecordsByUserId(userId);
        List<com.member.storage.model.RefundRecord> refundRecords = memoryStore.getRefundRecordsByUserId(userId);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        
        long rechargeCountLast24h = rechargeRecords.stream()
                .filter(r -> r.getCreateTime().isAfter(oneDayAgo))
                .count();
        if (rechargeCountLast24h > 5) {
            riskScore += 3;
            riskReasons.add("24小时内充值次数过多: " + rechargeCountLast24h);
        }
        
        for (RechargeRecord recharge : rechargeRecords) {
            if (recharge.getAmount().compareTo(new BigDecimal("5000")) > 0) {
                riskScore += 2;
                riskReasons.add("单笔充值金额过大: " + recharge.getAmount());
                break;
            }
        }
        
        long refundCountLast30d = refundRecords.stream()
                .filter(r -> r.getCreateTime().isAfter(thirtyDaysAgo))
                .count();
        if (refundCountLast30d > 3) {
            riskScore += 3;
            riskReasons.add("30天内退款次数过多: " + refundCountLast30d);
        }
        
        for (int i = 0; i < refundRecords.size() - 1; i++) {
            final RefundRecord currentRefund = refundRecords.get(i);
            RechargeRecord recharge = rechargeRecords.stream()
                    .filter(r -> r.getOrderId().equals(currentRefund.getOrderId()))
                    .findFirst().orElse(null);
            
            if (recharge != null && 
                recharge.getCreateTime().plusHours(1).isAfter(currentRefund.getCreateTime())) {
                riskScore += 2;
                riskReasons.add("充值后退款间隔过短");
                break;
            }
        }
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) == 0 &&
            account.getGiftBalance().compareTo(BigDecimal.ZERO) == 0 &&
            account.getTotalConsume().compareTo(BigDecimal.ZERO) > 0) {
            riskScore += 1;
            riskReasons.add("账户余额长期为0");
        }
        
        if (riskScore >= 5) {
            return RiskLevel.RESTRICTED;
        } else if (riskScore >= 2) {
            return RiskLevel.ATTENTION;
        }
        return RiskLevel.NORMAL;
    }

    public void autoUpdateRiskLevels() {
        List<Account> accounts = memoryStore.getAllAccounts();
        
        for (Account account : accounts) {
            RiskLevel riskLevel = checkRisk(account.getUserId());
            if (account.getRiskLevel() != riskLevel && riskLevel != RiskLevel.NORMAL) {
                accountService.updateRiskLevel(account.getUserId(), riskLevel, "系统自动风控");
            }
        }
    }

    public List<Map<String, Object>> getRiskAccounts() {
        List<Account> allAccounts = memoryStore.getAllAccounts();
        List<Map<String, Object>> riskAccounts = new ArrayList<>();
        
        for (Account account : allAccounts) {
            if (account.getRiskLevel() != RiskLevel.NORMAL) {
                Map<String, Object> info = new HashMap<>();
                info.put("userId", account.getUserId());
                info.put("level", account.getLevel());
                info.put("riskLevel", account.getRiskLevel());
                info.put("riskReason", account.getRiskReason());
                info.put("riskTime", account.getRiskTime());
                info.put("balance", account.getBalance());
                info.put("giftBalance", account.getGiftBalance());
                riskAccounts.add(info);
            }
        }
        
        return riskAccounts;
    }

    public Map<String, Object> getRiskDetail(String userId) {
        Account account = accountService.getAccountOrNull(userId);
        if (account == null) {
            return null;
        }
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("userId", account.getUserId());
        detail.put("level", account.getLevel());
        detail.put("riskLevel", account.getRiskLevel());
        detail.put("riskReason", account.getRiskReason());
        detail.put("riskTime", account.getRiskTime());
        detail.put("balance", account.getBalance());
        detail.put("giftBalance", account.getGiftBalance());
        detail.put("totalRecharge", account.getTotalRecharge());
        detail.put("totalConsume", account.getTotalConsume());
        
        List<RechargeRecord> rechargeRecords = memoryStore.getRechargeRecordsByUserId(userId);
        List<ConsumeRecord> consumeRecords = memoryStore.getConsumeRecordsByUserId(userId);
        List<com.member.storage.model.RefundRecord> refundRecords = memoryStore.getRefundRecordsByUserId(userId);
        
        detail.put("rechargeCount", rechargeRecords.size());
        detail.put("consumeCount", consumeRecords.size());
        detail.put("refundCount", refundRecords.size());
        
        return detail;
    }

    public void releaseRisk(String userId) {
        accountService.updateRiskLevel(userId, RiskLevel.NORMAL, "手动解除风控");
    }

    public Map<String, Object> getRiskStats() {
        List<Account> allAccounts = memoryStore.getAllAccounts();
        
        long normalCount = allAccounts.stream()
                .filter(a -> a.getRiskLevel() == RiskLevel.NORMAL)
                .count();
        long attentionCount = allAccounts.stream()
                .filter(a -> a.getRiskLevel() == RiskLevel.ATTENTION)
                .count();
        long restrictedCount = allAccounts.stream()
                .filter(a -> a.getRiskLevel() == RiskLevel.RESTRICTED)
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAccounts", allAccounts.size());
        stats.put("normalCount", normalCount);
        stats.put("attentionCount", attentionCount);
        stats.put("restrictedCount", restrictedCount);
        
        return stats;
    }

    public boolean canRecharge(String userId) {
        Account account = accountService.getAccountOrNull(userId);
        return account != null && account.getRiskLevel() != RiskLevel.RESTRICTED;
    }

    public boolean canConsume(String userId) {
        Account account = accountService.getAccountOrNull(userId);
        return account != null && account.getRiskLevel() != RiskLevel.RESTRICTED;
    }
}
