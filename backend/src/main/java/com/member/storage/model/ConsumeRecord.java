package com.member.storage.model;

import com.member.storage.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ConsumeRecord {
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal balanceUsed;
    private BigDecimal giftUsed;
    private BigDecimal couponUsed;
    private BigDecimal cashUsed;
    private List<String> couponIds;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createTime;
    private String remark;

    public ConsumeRecord() {
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

    public BigDecimal getBalanceUsed() {
        return balanceUsed;
    }

    public void setBalanceUsed(BigDecimal balanceUsed) {
        this.balanceUsed = balanceUsed;
    }

    public BigDecimal getGiftUsed() {
        return giftUsed;
    }

    public void setGiftUsed(BigDecimal giftUsed) {
        this.giftUsed = giftUsed;
    }

    public BigDecimal getCouponUsed() {
        return couponUsed;
    }

    public void setCouponUsed(BigDecimal couponUsed) {
        this.couponUsed = couponUsed;
    }

    public BigDecimal getCashUsed() {
        return cashUsed;
    }

    public void setCashUsed(BigDecimal cashUsed) {
        this.cashUsed = cashUsed;
    }

    public List<String> getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(List<String> couponIds) {
        this.couponIds = couponIds;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private String userId;
        private BigDecimal amount;
        private BigDecimal balanceUsed;
        private BigDecimal giftUsed;
        private BigDecimal couponUsed;
        private BigDecimal cashUsed;
        private List<String> couponIds;
        private List<OrderItem> items;
        private OrderStatus status;
        private LocalDateTime createTime;
        private String remark;

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

        public Builder balanceUsed(BigDecimal balanceUsed) {
            this.balanceUsed = balanceUsed;
            return this;
        }

        public Builder giftUsed(BigDecimal giftUsed) {
            this.giftUsed = giftUsed;
            return this;
        }

        public Builder couponUsed(BigDecimal couponUsed) {
            this.couponUsed = couponUsed;
            return this;
        }

        public Builder cashUsed(BigDecimal cashUsed) {
            this.cashUsed = cashUsed;
            return this;
        }

        public Builder couponIds(List<String> couponIds) {
            this.couponIds = couponIds;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public ConsumeRecord build() {
            ConsumeRecord record = new ConsumeRecord();
            record.setOrderId(orderId);
            record.setUserId(userId);
            record.setAmount(amount);
            record.setBalanceUsed(balanceUsed);
            record.setGiftUsed(giftUsed);
            record.setCouponUsed(couponUsed);
            record.setCashUsed(cashUsed);
            record.setCouponIds(couponIds);
            record.setItems(items);
            record.setStatus(status);
            record.setCreateTime(createTime);
            record.setRemark(remark);
            return record;
        }
    }
}
