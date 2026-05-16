package com.member.storage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RechargeRecord {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal giftAmount;
    private String payMethod;
    private String status;
    private LocalDateTime createTime;

    public RechargeRecord() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private BigDecimal giftAmount;
        private String payMethod;
        private String status;
        private LocalDateTime createTime;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder giftAmount(BigDecimal giftAmount) {
            this.giftAmount = giftAmount;
            return this;
        }

        public Builder payMethod(String payMethod) {
            this.payMethod = payMethod;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public RechargeRecord build() {
            RechargeRecord record = new RechargeRecord();
            record.setOrderId(orderId);
            record.setUserId(userId);
            record.setAmount(amount);
            record.setGiftAmount(giftAmount);
            record.setPayMethod(payMethod);
            record.setStatus(status);
            record.setCreateTime(createTime);
            return record;
        }
    }
}
