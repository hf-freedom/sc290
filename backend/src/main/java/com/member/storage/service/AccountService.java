package com.member.storage.service;

import com.member.storage.dto.CreateAccountRequest;
import com.member.storage.enums.MemberLevel;
import com.member.storage.enums.RiskLevel;
import com.member.storage.model.Account;
import com.member.storage.model.Gift;
import com.member.storage.model.Coupon;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private MemoryStore memoryStore;

    public Account createAccount(String userId) {
        if (memoryStore.getAccount(userId) != null) {
            throw new RuntimeException("账户已存在: " + userId);
        }
        
        Account account = Account.builder()
                .userId(userId)
                .name("会员" + userId)
                .level(MemberLevel.LV1)
                .balance(BigDecimal.ZERO)
                .giftBalance(BigDecimal.ZERO)
                .totalRecharge(BigDecimal.ZERO)
                .totalConsume(BigDecimal.ZERO)
                .riskLevel(RiskLevel.NORMAL)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .lastActiveTime(LocalDateTime.now())
                .build();
        
        memoryStore.saveAccount(account);
        return account;
    }

    public Account getAccount(String userId) {
        Account account = memoryStore.getAccount(userId);
        if (account == null) {
            throw new RuntimeException("账户不存在: " + userId);
        }
        return account;
    }

    public Account getAccountOrNull(String userId) {
        return memoryStore.getAccount(userId);
    }

    public List<Account> getAllAccounts() {
        return memoryStore.getAllAccounts();
    }

    public Account updateMemberLevel(String userId, MemberLevel level) {
        Account account = getAccount(userId);
        account.setLevel(level);
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
        return account;
    }

    public Account updateRiskLevel(String userId, RiskLevel riskLevel, String reason) {
        Account account = getAccount(userId);
        account.setRiskLevel(riskLevel);
        account.setRiskReason(reason);
        account.setRiskTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
        return account;
    }

    public void addBalance(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void deductBalance(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，当前余额: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void addGiftBalance(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        account.setGiftBalance(account.getGiftBalance().add(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void deductGiftBalance(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        if (account.getGiftBalance().compareTo(amount) < 0) {
            throw new RuntimeException("赠送金余额不足");
        }
        account.setGiftBalance(account.getGiftBalance().subtract(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void addTotalRecharge(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        account.setTotalRecharge(account.getTotalRecharge().add(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
        
        MemberLevel newLevel = calculateMemberLevel(account.getTotalRecharge());
        if (newLevel.getLevel() > account.getLevel().getLevel()) {
            account.setLevel(newLevel);
            memoryStore.saveAccount(account);
        }
    }

    public void addTotalConsume(String userId, BigDecimal amount) {
        Account account = getAccount(userId);
        account.setTotalConsume(account.getTotalConsume().add(amount));
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void addGift(String userId, Gift gift) {
        Account account = getAccount(userId);
        account.addGift(gift);
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void addCoupon(String userId, Coupon coupon) {
        Account account = getAccount(userId);
        account.addCoupon(coupon);
        account.setUpdateTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
    }

    public void updateLastActiveTime(String userId) {
        Account account = getAccount(userId);
        account.setLastActiveTime(LocalDateTime.now());
        memoryStore.saveAccount(account);
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
}
