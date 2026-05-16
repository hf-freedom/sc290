package com.member.storage.model;

import com.member.storage.enums.MemberLevel;
import com.member.storage.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Account {
    private String userId;
    private String name;
    private MemberLevel level;
    private BigDecimal balance;
    private BigDecimal giftBalance;
    private BigDecimal totalRecharge;
    private BigDecimal totalConsume;
    private RiskLevel riskLevel;
    private String riskReason;
    private LocalDateTime riskTime;
    private List<Gift> gifts = new CopyOnWriteArrayList<>();
    private List<Coupon> coupons = new CopyOnWriteArrayList<>();
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime lastActiveTime;

    public Account() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MemberLevel getLevel() {
        return level;
    }

    public void setLevel(MemberLevel level) {
        this.level = level;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getGiftBalance() {
        return giftBalance;
    }

    public void setGiftBalance(BigDecimal giftBalance) {
        this.giftBalance = giftBalance;
    }

    public BigDecimal getTotalRecharge() {
        return totalRecharge;
    }

    public void setTotalRecharge(BigDecimal totalRecharge) {
        this.totalRecharge = totalRecharge;
    }

    public BigDecimal getTotalConsume() {
        return totalConsume;
    }

    public void setTotalConsume(BigDecimal totalConsume) {
        this.totalConsume = totalConsume;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public void setRiskReason(String riskReason) {
        this.riskReason = riskReason;
    }

    public LocalDateTime getRiskTime() {
        return riskTime;
    }

    public void setRiskTime(LocalDateTime riskTime) {
        this.riskTime = riskTime;
    }

    public List<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public void addGift(Gift gift) {
        if (this.gifts == null) {
            this.gifts = new CopyOnWriteArrayList<>();
        }
        this.gifts.add(gift);
    }

    public void addCoupon(Coupon coupon) {
        if (this.coupons == null) {
            this.coupons = new CopyOnWriteArrayList<>();
        }
        this.coupons.add(coupon);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String name;
        private MemberLevel level;
        private BigDecimal balance;
        private BigDecimal giftBalance;
        private BigDecimal totalRecharge;
        private BigDecimal totalConsume;
        private RiskLevel riskLevel;
        private String riskReason;
        private LocalDateTime riskTime;
        private List<Gift> gifts;
        private List<Coupon> coupons;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private LocalDateTime lastActiveTime;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder level(MemberLevel level) {
            this.level = level;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder giftBalance(BigDecimal giftBalance) {
            this.giftBalance = giftBalance;
            return this;
        }

        public Builder totalRecharge(BigDecimal totalRecharge) {
            this.totalRecharge = totalRecharge;
            return this;
        }

        public Builder totalConsume(BigDecimal totalConsume) {
            this.totalConsume = totalConsume;
            return this;
        }

        public Builder riskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder riskReason(String riskReason) {
            this.riskReason = riskReason;
            return this;
        }

        public Builder riskTime(LocalDateTime riskTime) {
            this.riskTime = riskTime;
            return this;
        }

        public Builder gifts(List<Gift> gifts) {
            this.gifts = gifts;
            return this;
        }

        public Builder coupons(List<Coupon> coupons) {
            this.coupons = coupons;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder lastActiveTime(LocalDateTime lastActiveTime) {
            this.lastActiveTime = lastActiveTime;
            return this;
        }

        public Account build() {
            Account account = new Account();
            account.userId = this.userId;
            account.name = this.name;
            account.level = this.level;
            account.balance = this.balance;
            account.giftBalance = this.giftBalance;
            account.totalRecharge = this.totalRecharge;
            account.totalConsume = this.totalConsume;
            account.riskLevel = this.riskLevel;
            account.riskReason = this.riskReason;
            account.riskTime = this.riskTime;
            account.gifts = this.gifts != null ? this.gifts : new CopyOnWriteArrayList<>();
            account.coupons = this.coupons != null ? this.coupons : new CopyOnWriteArrayList<>();
            account.createTime = this.createTime;
            account.updateTime = this.updateTime;
            account.lastActiveTime = this.lastActiveTime;
            return account;
        }
    }
}
