package com.member.storage.service;

import com.member.storage.dto.RefundRequest;
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
public class RefundService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GiftService giftService;

    @Autowired
    private MemoryStore memoryStore;

    public RefundRecord refund(String orderId, String reason) {
        ConsumeRecord consumeRecord = memoryStore.getConsumeRecord(orderId);
        if (consumeRecord == null) {
            throw new RuntimeException("消费记录不存在: " + orderId);
        }
        
        String userId = consumeRecord.getUserId();
        
        BigDecimal refundAmount = consumeRecord.getAmount();
        BigDecimal maxRefundAmount = consumeRecord.getBalanceUsed()
                .add(consumeRecord.getGiftUsed())
                .add(consumeRecord.getCouponUsed())
                .add(consumeRecord.getCashUsed());
        
        if (refundAmount.compareTo(maxRefundAmount) > 0) {
            refundAmount = maxRefundAmount;
        }
        
        String refundId = UUID.randomUUID().toString();
        
        BigDecimal balanceRefund = BigDecimal.ZERO;
        BigDecimal giftRefund = BigDecimal.ZERO;
        BigDecimal cashRefund = BigDecimal.ZERO;
        List<String> couponRefund = new ArrayList<>();
        
        BigDecimal remaining = refundAmount;
        
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && 
            consumeRecord.getBalanceUsed().compareTo(BigDecimal.ZERO) > 0) {
            balanceRefund = remaining.min(consumeRecord.getBalanceUsed());
            remaining = remaining.subtract(balanceRefund);
        }
        
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && 
            consumeRecord.getGiftUsed().compareTo(BigDecimal.ZERO) > 0) {
            giftRefund = remaining.min(consumeRecord.getGiftUsed());
            remaining = remaining.subtract(giftRefund);
        }
        
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && 
            consumeRecord.getCouponUsed().compareTo(BigDecimal.ZERO) > 0) {
            cashRefund = remaining;
        }
        
        if (consumeRecord.getCouponIds() != null) {
            couponRefund = new ArrayList<>(consumeRecord.getCouponIds());
        }
        
        RefundRecord record = RefundRecord.builder()
                .refundId(refundId)
                .orderId(orderId)
                .userId(userId)
                .refundAmount(refundAmount)
                .balanceRefund(balanceRefund)
                .giftRefund(giftRefund)
                .cashRefund(cashRefund)
                .couponRefund(couponRefund)
                .reason(reason)
                .status("SUCCESS")
                .createTime(LocalDateTime.now())
                .build();
        
        if (balanceRefund.compareTo(BigDecimal.ZERO) > 0) {
            accountService.addBalance(userId, balanceRefund);
        }
        
        if (giftRefund.compareTo(BigDecimal.ZERO) > 0) {
            Gift newGift = Gift.builder()
                    .giftId(UUID.randomUUID().toString())
                    .userId(userId)
                    .amount(giftRefund)
                    .remainAmount(giftRefund)
                    .expireTime(LocalDateTime.now().plusDays(30))
                    .scope("ALL")
                    .status("ACTIVE")
                    .createTime(LocalDateTime.now())
                    .build();
            giftService.createGift(newGift);
            accountService.addGiftBalance(userId, giftRefund);
        }
        
        if (!couponRefund.isEmpty()) {
            for (String couponId : couponRefund) {
                Coupon coupon = memoryStore.getCoupon(couponId);
                if (coupon != null) {
                    coupon.setStatus("UNUSED");
                    memoryStore.saveCoupon(coupon);
                }
            }
        }
        
        accountService.addTotalConsume(userId, refundAmount.negate());
        
        consumeRecord.setStatus(com.member.storage.enums.OrderStatus.REFUNDED);
        memoryStore.saveConsumeRecord(consumeRecord);
        memoryStore.saveRefundRecord(record);
        
        return record;
    }

    public RefundRecord getRefundRecord(String refundId) {
        return memoryStore.getRefundRecord(refundId);
    }

    public List<RefundRecord> getRefundRecordsByUserId(String userId) {
        return memoryStore.getRefundRecordsByUserId(userId);
    }
}
