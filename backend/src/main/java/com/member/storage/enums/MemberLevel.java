package com.member.storage.enums;

public enum MemberLevel {
    LV1("LV1普通会员", 0, 0.0, 30),
    LV2("LV2银卡会员", 1, 0.05, 60),
    LV3("LV3金卡会员", 2, 0.10, 90),
    LV4("LV4钻石会员", 3, 0.15, 180);

    private final String description;
    private final int level;
    private final double giftRate;
    private final int giftDays;

    MemberLevel(String description, int level, double giftRate, int giftDays) {
        this.description = description;
        this.level = level;
        this.giftRate = giftRate;
        this.giftDays = giftDays;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public double getGiftRate() {
        return giftRate;
    }

    public int getGiftDays() {
        return giftDays;
    }

    public static MemberLevel fromRechargeAmount(double amount) {
        if (amount >= 10000) {
            return LV4;
        } else if (amount >= 5000) {
            return LV3;
        } else if (amount >= 2000) {
            return LV2;
        }
        return LV1;
    }
}
