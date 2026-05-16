package com.member.storage.service;

import com.member.storage.enums.MemberLevel;
import com.member.storage.model.Account;
import com.member.storage.model.Gift;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {

    @Autowired
    private GiftService giftService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RiskService riskService;

    @Autowired
    private MemoryStore memoryStore;

    private final AtomicInteger expireGiftCount = new AtomicInteger(0);
    private final AtomicInteger riskCheckCount = new AtomicInteger(0);
    private final AtomicInteger levelUpdateCount = new AtomicInteger(0);
    private LocalDateTime lastExpireGiftTime;
    private LocalDateTime lastRiskCheckTime;
    private LocalDateTime lastLevelUpdateTime;

    @Scheduled(fixedRate = 3600000)
    public void scheduledTasks() {
        processExpiredGifts();
        checkRisks();
        updateMemberLevels();
    }

    public int processExpiredGifts() {
        int count = 0;
        LocalDateTime now = LocalDateTime.now();
        
        List<Gift> allGifts = memoryStore.getAllGifts();
        for (Gift gift : allGifts) {
            if ("ACTIVE".equals(gift.getStatus()) && gift.getExpireTime().isBefore(now)) {
                giftService.expireGift(gift.getGiftId());
                
                Account account = accountService.getAccountOrNull(gift.getUserId());
                if (account != null) {
                    account.setGiftBalance(account.getGiftBalance().subtract(gift.getRemainAmount()));
                    memoryStore.saveAccount(account);
                }
                count++;
            }
        }
        
        expireGiftCount.addAndGet(count);
        lastExpireGiftTime = LocalDateTime.now();
        return count;
    }

    public int checkRisks() {
        riskService.autoUpdateRiskLevels();
        int count = (int) memoryStore.getAllAccounts().stream()
                .filter(a -> a.getRiskLevel() != com.member.storage.enums.RiskLevel.NORMAL)
                .count();
        
        riskCheckCount.addAndGet(count);
        lastRiskCheckTime = LocalDateTime.now();
        return count;
    }

    public int updateMemberLevels() {
        int count = 0;
        List<Account> accounts = memoryStore.getAllAccounts();
        
        for (Account account : accounts) {
            MemberLevel newLevel = calculateMemberLevel(account.getTotalRecharge());
            if (newLevel.getLevel() > account.getLevel().getLevel()) {
                account.setLevel(newLevel);
                memoryStore.saveAccount(account);
                count++;
            }
        }
        
        levelUpdateCount.addAndGet(count);
        lastLevelUpdateTime = LocalDateTime.now();
        return count;
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
