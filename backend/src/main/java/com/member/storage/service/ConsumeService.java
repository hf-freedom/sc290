package com.member.storage.service;

import com.member.storage.model.*;
import com.member.storage.repository.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConsumeService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private MemoryStore memoryStore;

    public ConsumeRecord consume(String userId, List<OrderItem> items, List<String> couponIds) {
        Account account = accountService.getAccount(userId);
        
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal balance = account.getBalance();
        BigDecimal giftBalance = account.getGiftBalance();
        
        BigDecimal balanceUsed = balance.min(totalAmount);
        BigDecimal remainAmount = totalAmount.subtract(balanceUsed);
        
        BigDecimal giftUsed = BigDecimal.ZERO;
        if (remainAmount.compareTo(BigDecimal.ZERO) > 0) {
            giftUsed = giftBalance.min(remainAmount);
            remainAmount = remainAmount.subtract(giftUsed);
        }
        
        BigDecimal couponUsed = BigDecimal.ZERO;
        List<String> usedCouponIds = new ArrayList<>();
        
        if (!couponIds.isEmpty() && remainAmount.compareTo(BigDecimal.ZERO) > 0) {
            for (String couponId : couponIds) {
                if (remainAmount.compareTo(BigDecimal.ZERO) <= 0) break;
                
                Coupon coupon = memoryStore.getCoupon(couponId);
                if (coupon != null && "UNUSED".equals(coupon.getStatus()) && 
                    coupon.getExpireTime().isAfter(LocalDateTime.now())) {
                    if (totalAmount.compareTo(coupon.getMinConsume()) >= 0) {
                        BigDecimal useAmount = remainAmount.min(coupon.getAmount());
                        couponUsed = couponUsed.add(useAmount);
                        remainAmount = remainAmount.subtract(useAmount);
                        usedCouponIds.add(couponId);
                        
                        coupon.setStatus("USED");
                        memoryStore.saveCoupon(coupon);
                    }
                }
            }
        }
        
        BigDecimal cashUsed = remainAmount;
        
        if (cashUsed.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("余额不足，需要额外支付现金: " + cashUsed);
        }
        
        String orderId = UUID.randomUUID().toString();
        
        ConsumeRecord record = ConsumeRecord.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(totalAmount)
                .balanceUsed(balanceUsed)
                .giftUsed(giftUsed)
                .couponUsed(couponUsed)
                .cashUsed(cashUsed)
                .couponIds(usedCouponIds)
                .items(items)
                .status(com.member.storage.enums.OrderStatus.SUCCESS)
                .createTime(LocalDateTime.now())
                .build();
        
        if (balanceUsed.compareTo(BigDecimal.ZERO) > 0) {
            accountService.deductBalance(userId, balanceUsed);
        }
        
        if (giftUsed.compareTo(BigDecimal.ZERO) > 0) {
            List<Gift> validGifts = giftService.getValidGifts(userId);
            BigDecimal remaining = giftUsed;
            
            for (Gift gift : validGifts) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal useAmount = remaining.min(gift.getRemainAmount());
                gift.setRemainAmount(gift.getRemainAmount().subtract(useAmount));
                remaining = remaining.subtract(useAmount);
                
                if (gift.getRemainAmount().compareTo(BigDecimal.ZERO) == 0) {
                    gift.setStatus("USED");
                }
                memoryStore.saveGift(gift);
            }
            
            accountService.deductGiftBalance(userId, giftUsed);
        }
        
        accountService.addTotalConsume(userId, totalAmount);
        memoryStore.saveConsumeRecord(record);
        
        return record;
    }

    public ConsumeRecord combinePay(String userId, List<OrderItem> items, BigDecimal balanceAmount, BigDecimal cashAmount) {
        Account account = accountService.getAccount(userId);
        
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal balanceUsed = balanceAmount.min(account.getBalance()).min(totalAmount);
        BigDecimal remainAmount = totalAmount.subtract(balanceUsed);
        BigDecimal cashUsed = cashAmount.min(remainAmount);
        remainAmount = remainAmount.subtract(cashUsed);
        
        BigDecimal giftUsed = BigDecimal.ZERO;
        if (remainAmount.compareTo(BigDecimal.ZERO) > 0) {
            giftUsed = remainAmount.min(account.getGiftBalance());
            remainAmount = remainAmount.subtract(giftUsed);
        }
        
        if (remainAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("支付金额不足以完成订单");
        }
        
        String orderId = UUID.randomUUID().toString();
        
        ConsumeRecord record = ConsumeRecord.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(totalAmount)
                .balanceUsed(balanceUsed)
                .giftUsed(giftUsed)
                .cashUsed(cashUsed)
                .items(items)
                .status(com.member.storage.enums.OrderStatus.SUCCESS)
                .createTime(LocalDateTime.now())
                .build();
        
        if (balanceUsed.compareTo(BigDecimal.ZERO) > 0) {
            accountService.deductBalance(userId, balanceUsed);
        }
        
        if (giftUsed.compareTo(BigDecimal.ZERO) > 0) {
            accountService.deductGiftBalance(userId, giftUsed);
        }
        
        PaymentSplit split = PaymentSplit.builder()
                .splitId(UUID.randomUUID().toString())
                .orderId(orderId)
                .balanceAmount(balanceUsed)
                .giftAmount(giftUsed)
                .cashAmount(cashUsed)
                .createTime(LocalDateTime.now())
                .build();
        memoryStore.savePaymentSplit(split);
        
        accountService.addTotalConsume(userId, totalAmount);
        memoryStore.saveConsumeRecord(record);
        
        return record;
    }

    public ConsumeRecord getConsumeRecord(String orderId) {
        return memoryStore.getConsumeRecord(orderId);
    }

    public List<ConsumeRecord> getConsumeRecordsByUserId(String userId) {
        return memoryStore.getConsumeRecordsByUserId(userId);
    }
}
