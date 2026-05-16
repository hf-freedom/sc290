package com.member.storage.repository;

import com.member.storage.model.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MemoryStore {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, RechargeRecord> rechargeRecords = new ConcurrentHashMap<>();
    private final Map<String, ConsumeRecord> consumeRecords = new ConcurrentHashMap<>();
    private final Map<String, RefundRecord> refundRecords = new ConcurrentHashMap<>();
    private final Map<String, Gift> gifts = new ConcurrentHashMap<>();
    private final Map<String, Coupon> coupons = new ConcurrentHashMap<>();
    private final Map<String, PaymentSplit> paymentSplits = new ConcurrentHashMap<>();

    public void saveAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account getAccount(String userId) {
        return accounts.get(userId);
    }

    public List<Account> getAllAccounts() {
        return accounts.values().stream().collect(Collectors.toList());
    }

    public void deleteAccount(String userId) {
        accounts.remove(userId);
    }

    public void saveRechargeRecord(RechargeRecord record) {
        rechargeRecords.put(record.getOrderId(), record);
    }

    public RechargeRecord getRechargeRecord(String orderId) {
        return rechargeRecords.get(orderId);
    }

    public List<RechargeRecord> getRechargeRecordsByUserId(String userId) {
        return rechargeRecords.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void saveConsumeRecord(ConsumeRecord record) {
        consumeRecords.put(record.getOrderId(), record);
    }

    public ConsumeRecord getConsumeRecord(String orderId) {
        return consumeRecords.get(orderId);
    }

    public List<ConsumeRecord> getConsumeRecordsByUserId(String userId) {
        return consumeRecords.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void saveRefundRecord(RefundRecord record) {
        refundRecords.put(record.getRefundId(), record);
    }

    public RefundRecord getRefundRecord(String refundId) {
        return refundRecords.get(refundId);
    }

    public List<RefundRecord> getRefundRecordsByUserId(String userId) {
        return refundRecords.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void saveGift(Gift gift) {
        gifts.put(gift.getGiftId(), gift);
    }

    public Gift getGift(String giftId) {
        return gifts.get(giftId);
    }

    public List<Gift> getGiftsByUserId(String userId) {
        return gifts.values().stream()
                .filter(g -> g.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Gift> getValidGiftsByUserId(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return gifts.values().stream()
                .filter(g -> g.getUserId().equals(userId) 
                    && "ACTIVE".equals(g.getStatus()) 
                    && g.getExpireTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public List<Gift> getAllGifts() {
        return gifts.values().stream().collect(Collectors.toList());
    }

    public void saveCoupon(Coupon coupon) {
        coupons.put(coupon.getCouponId(), coupon);
    }

    public Coupon getCoupon(String couponId) {
        return coupons.get(couponId);
    }

    public List<Coupon> getCouponsByUserId(String userId) {
        return coupons.values().stream()
                .filter(c -> c.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Coupon> getValidCouponsByUserId(String userId) {
        LocalDateTime now = LocalDateTime.now();
        return coupons.values().stream()
                .filter(c -> c.getUserId().equals(userId) 
                    && "UNUSED".equals(c.getStatus()) 
                    && c.getExpireTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public void savePaymentSplit(PaymentSplit split) {
        paymentSplits.put(split.getSplitId(), split);
    }

    public PaymentSplit getPaymentSplit(String splitId) {
        return paymentSplits.get(splitId);
    }

    public List<PaymentSplit> getPaymentSplitsByOrderId(String orderId) {
        return paymentSplits.values().stream()
                .filter(s -> s.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }
}
