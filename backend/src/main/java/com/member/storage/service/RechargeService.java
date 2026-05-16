package com.member.storage.service;

import com.member.storage.enums.MemberLevel;
import com.member.storage.model.Account;
import com.member.storage.model.Gift;
import com.member.storage.model.RechargeRecord;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RechargeService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private MemoryStore memoryStore;

    public RechargeRecord recharge(String userId, BigDecimal amount, String payMethod) {
        Account account = accountService.getAccount(userId);
        
        String orderId = UUID.randomUUID().toString();
        MemberLevel level = account.getLevel();
        BigDecimal giftAmount = amount.multiply(new BigDecimal(level.getGiftRate()));
        
        RechargeRecord record = RechargeRecord.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .giftAmount(giftAmount)
                .payMethod(payMethod)
                .status("SUCCESS")
                .createTime(LocalDateTime.now())
                .build();
        
        accountService.addBalance(userId, amount);
        accountService.addTotalRecharge(userId, amount);
        
        if (giftAmount.compareTo(BigDecimal.ZERO) > 0) {
            Gift gift = Gift.builder()
                    .giftId(UUID.randomUUID().toString())
                    .userId(userId)
                    .amount(giftAmount)
                    .remainAmount(giftAmount)
                    .expireTime(LocalDateTime.now().plusDays(level.getGiftDays()))
                    .scope("ALL")
                    .status("ACTIVE")
                    .createTime(LocalDateTime.now())
                    .build();
            giftService.createGift(gift);
            accountService.addGiftBalance(userId, giftAmount);
        }
        
        memoryStore.saveRechargeRecord(record);
        return record;
    }

    public RechargeRecord getRechargeRecord(String orderId) {
        return memoryStore.getRechargeRecord(orderId);
    }

    public List<RechargeRecord> getRechargeRecordsByUserId(String userId) {
        return memoryStore.getRechargeRecordsByUserId(userId);
    }
}
