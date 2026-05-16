package com.member.storage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Gift {
    private String giftId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal remainAmount;
    private LocalDateTime expireTime;
    private String scope;
    private List<String> categories;
    private String status;
    private LocalDateTime createTime;

    public Gift() {
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
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

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
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
        private String giftId;
        private String userId;
        private BigDecimal amount;
        private BigDecimal remainAmount;
        private LocalDateTime expireTime;
        private String scope;
        private List<String> categories;
        private String status;
        private LocalDateTime createTime;

        public Builder giftId(String giftId) {
            this.giftId = giftId;
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

        public Builder remainAmount(BigDecimal remainAmount) {
            this.remainAmount = remainAmount;
            return this;
        }

        public Builder expireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder categories(List<String> categories) {
            this.categories = categories;
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

        public Gift build() {
            Gift gift = new Gift();
            gift.setGiftId(giftId);
            gift.setUserId(userId);
            gift.setAmount(amount);
            gift.setRemainAmount(remainAmount);
            gift.setExpireTime(expireTime);
            gift.setScope(scope);
            gift.setCategories(categories);
            gift.setStatus(status);
            gift.setCreateTime(createTime);
            return gift;
        }
    }
}
