package com.member.storage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentSplit {
    private String splitId;
    private String orderId;
    private BigDecimal balanceAmount;
    private BigDecimal giftAmount;
    private BigDecimal cashAmount;
    private BigDecimal couponAmount;
    private LocalDateTime createTime;

    public PaymentSplit() {
    }

    public String getSplitId() {
        return splitId;
    }

    public void setSplitId(String splitId) {
        this.splitId = splitId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BigDecimal getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(BigDecimal giftAmount) {
        this.giftAmount = giftAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
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
        private String splitId;
        private String orderId;
        private BigDecimal balanceAmount;
        private BigDecimal giftAmount;
        private BigDecimal cashAmount;
        private BigDecimal couponAmount;
        private LocalDateTime createTime;

        public Builder splitId(String splitId) {
            this.splitId = splitId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder balanceAmount(BigDecimal balanceAmount) {
            this.balanceAmount = balanceAmount;
            return this;
        }

        public Builder giftAmount(BigDecimal giftAmount) {
            this.giftAmount = giftAmount;
            return this;
        }

        public Builder cashAmount(BigDecimal cashAmount) {
            this.cashAmount = cashAmount;
            return this;
        }

        public Builder couponAmount(BigDecimal couponAmount) {
            this.couponAmount = couponAmount;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public PaymentSplit build() {
            PaymentSplit split = new PaymentSplit();
            split.setSplitId(splitId);
            split.setOrderId(orderId);
            split.setBalanceAmount(balanceAmount);
            split.setGiftAmount(giftAmount);
            split.setCashAmount(cashAmount);
            split.setCouponAmount(couponAmount);
            split.setCreateTime(createTime);
            return split;
        }
    }
}
