package com.member.storage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Coupon {
    private String couponId;
    private String userId;
    private String type;
    private BigDecimal amount;
    private BigDecimal minConsume;
    private LocalDateTime expireTime;
    private String status;
    private LocalDateTime createTime;

    public Coupon() {
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMinConsume() {
        return minConsume;
    }

    public void setMinConsume(BigDecimal minConsume) {
        this.minConsume = minConsume;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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
        private String couponId;
        private String userId;
        private String type;
        private BigDecimal amount;
        private BigDecimal minConsume;
        private LocalDateTime expireTime;
        private String status;
        private LocalDateTime createTime;

        public Builder couponId(String couponId) {
            this.couponId = couponId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder minConsume(BigDecimal minConsume) {
            this.minConsume = minConsume;
            return this;
        }

        public Builder expireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
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

        public Coupon build() {
            Coupon coupon = new Coupon();
            coupon.setCouponId(couponId);
            coupon.setUserId(userId);
            coupon.setType(type);
            coupon.setAmount(amount);
            coupon.setMinConsume(minConsume);
            coupon.setExpireTime(expireTime);
            coupon.setStatus(status);
            coupon.setCreateTime(createTime);
            return coupon;
        }
    }
}
