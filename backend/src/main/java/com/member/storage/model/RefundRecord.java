package com.member.storage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    public RefundRecord() {
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
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

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getBalanceRefund() {
        return balanceRefund;
    }

    public void setBalanceRefund(BigDecimal balanceRefund) {
        this.balanceRefund = balanceRefund;
    }

    public BigDecimal getGiftRefund() {
        return giftRefund;
    }

    public void setGiftRefund(BigDecimal giftRefund) {
        this.giftRefund = giftRefund;
    }

    public BigDecimal getCashRefund() {
        return cashRefund;
    }

    public void setCashRefund(BigDecimal cashRefund) {
        this.cashRefund = cashRefund;
    }

    public List<String> getCouponRefund() {
        return couponRefund;
    }

    public void setCouponRefund(List<String> couponRefund) {
        this.couponRefund = couponRefund;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

        public Builder refundId(String refundId) {
            this.refundId = refundId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder refundAmount(BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        public Builder balanceRefund(BigDecimal balanceRefund) {
            this.balanceRefund = balanceRefund;
            return this;
        }

        public Builder giftRefund(BigDecimal giftRefund) {
            this.giftRefund = giftRefund;
            return this;
        }

        public Builder cashRefund(BigDecimal cashRefund) {
            this.cashRefund = cashRefund;
            return this;
        }

        public Builder couponRefund(List<String> couponRefund) {
            this.couponRefund = couponRefund;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
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

        public RefundRecord build() {
            RefundRecord record = new RefundRecord();
            record.setRefundId(refundId);
            record.setOrderId(orderId);
            record.setUserId(userId);
            record.setRefundAmount(refundAmount);
            record.setBalanceRefund(balanceRefund);
            record.setGiftRefund(giftRefund);
            record.setCashRefund(cashRefund);
            record.setCouponRefund(couponRefund);
            record.setReason(reason);
            record.setStatus(status);
            record.setCreateTime(createTime);
            return record;
        }
    }
}
